package com.checkvies.example

import com.checkvies.client.CheckViesClient
import com.checkvies.client.CheckViesException
import com.checkvies.client.generated.model.CacheUsageOptionsDto
import com.checkvies.client.generated.model.RequestListStateCheckDto
import com.checkvies.client.generated.model.RequestStateDto
import com.checkvies.client.generated.model.StartCheckRequestDto
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class CheckViesService(config: AppConfig) {
    private val client = CheckViesClient(config.toClientConfig())

    suspend fun runDemo() {
        val createRequest = StartCheckRequestDto(
            number = "7171642051",
            isoAlpha2 = "PL",
            cache = CacheUsageOptionsDto(cacheOnly = false, maxAgeMinutes = "60")
        )

        val created = runCatching { client.startCheck(createRequest) }
            .onSuccess { println("startCheck success: requestId=${it.id}") }
            .onFailure { logFailure("startCheck", it) }
            .getOrElse { return }

        runCatching { client.getCheckStates(RequestListStateCheckDto(requests = listOf(created.id))) }
            .onSuccess { stateList ->
                println("getCheckStates success: ${stateList.requests.size} item(s)")
                stateList.requests.forEach { item ->
                    println(" - requestId=${item.requestId}, state=${item.state}")
                }
            }
            .onFailure { logFailure("getCheckStates", it) }

        for (i in 0..10) {
            println("Retrieve status, attempt $i")
            val result = runCatching { client.getCheckDetails(created.id) }
                .onSuccess { details ->
                    println("getCheckDetails success: id=${details.requestId}, state=${details.state}")
                    println(" - valid=${details.result?.valid}, cacheUsed=${details.result?.cacheUsed}")
                    details.state
                }
                .onFailure { logFailure("getCheckDetails", it) }
                .getOrNull()

            if (result != null && (result.state == RequestStateDto.Pending || result.state == RequestStateDto.TemporaryError)) {
                if (result.state == RequestStateDto.TemporaryError && result.errorCode != null)
                    println(" - errorCode=${result.errorCode}")

                // avoid too frequent requests
                delay(1.seconds)
                continue
            } else if (result != null && result.state == RequestStateDto.Error) {
                println("Check finished with error, error code: ${result.errorCode}")
            }
            break
        }
    }

    private fun logFailure(operation: String, throwable: Throwable) {
        when (throwable) {
            is CheckViesException -> {
                System.err.println(
                    "$operation failed: code=${throwable.errorCode}, status=${throwable.statusCode}, message=${throwable.message}"
                )
            }

            else -> {
                System.err.println("$operation failed unexpectedly: ${throwable.message}")
            }
        }
    }
}
