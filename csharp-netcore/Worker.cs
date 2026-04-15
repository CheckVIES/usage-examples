using Deimdal.CheckVIES.Client.ApiClient;

namespace CheckVIES.Example.Core;

public class Worker(ILogger<Worker> logger, ICheckViesClient apiClient, IHost host) : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await ProcessCheck(stoppingToken);
        await host.StopAsync(stoppingToken);
    }

    private async Task ProcessCheck(CancellationToken stoppingToken)
    {
        CheckRequestCreatedDto request;

        try
        {
            request = await apiClient.StartAsync(new StartCheckRequestDto
            {
                IsoAlpha2 = "DE",
                Number = "12345678",
                Cache = new CacheUsageOptionsDto
                {
                    MaxAgeMinutes = "120"
                }
            }, stoppingToken);
            logger.LogInformation("Check started successfully, id={}", request.Id);
        }
        catch (Exception e)
        {
            logger.LogError(e, "Failed to start check");
            throw;
        }

        while (!stoppingToken.IsCancellationRequested)
        {
            var result = await apiClient.DetailsAsync(request.Id, stoppingToken);
            if (result.State == RequestStateDto.Success)
            {
                logger.LogInformation("Check finished, valid={valid}, fromCache={fromCache}", result.Result?.Valid, result.Result?.CacheUsed);
                return;
            }

            if (result.State is RequestStateDto.Pending or RequestStateDto.TemporaryError)
            {
                logger.LogInformation("Check is in progress now. Let's wait a bit...");
                // NB: avoid too frequent checks
                await Task.Delay(1000, stoppingToken);
            }
            else
            {
                logger.LogInformation("Check stopped due to {state}", result.State);
                return;
            }
        }
    }
}
