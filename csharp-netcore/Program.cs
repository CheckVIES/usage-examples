using CheckVIES.Example.Core;
using Deimdal.CheckVIES.Client.Extensions;

var builder = Host.CreateApplicationBuilder(args);
builder.Services.AddHostedService<Worker>();
builder.Services.AddCheckViesClient("<put your API key here>", "<put service API URL here>");

var host = builder.Build();
host.Run();
