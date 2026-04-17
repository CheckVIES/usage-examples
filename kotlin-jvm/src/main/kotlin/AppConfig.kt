package com.checkvies.example

import com.checkvies.client.CheckViesConfig
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel

data class AppConfig(
    val apiKey: String,
    val baseUrl: String,
    val requestTimeoutMillis: Long,
    val connectTimeoutMillis: Long,
    val socketTimeoutMillis: Long,
    val clientLogLevel: LogLevel
) {
    fun toClientConfig(): CheckViesConfig = CheckViesConfig(
        apiKey = apiKey,
        baseUrl = baseUrl,
        logLevel = clientLogLevel,
        httpClientConfig = {
            configureTimeouts()
        }
    )

    private fun HttpClientConfig<*>.configureTimeouts() {
        install(HttpTimeout) {
            requestTimeoutMillis = this@AppConfig.requestTimeoutMillis
            connectTimeoutMillis = this@AppConfig.connectTimeoutMillis
            socketTimeoutMillis = this@AppConfig.socketTimeoutMillis
        }
    }

    companion object {
        private const val DEFAULT_BASE_URL = "https://api.checkvies.com"
        private const val DEFAULT_REQUEST_TIMEOUT_MS = 30_000L
        private const val DEFAULT_CONNECT_TIMEOUT_MS = 10_000L
        private const val DEFAULT_SOCKET_TIMEOUT_MS = 30_000L

        fun fromEnvironment(environment: Map<String, String> = System.getenv()): AppConfig {
            val apiKey = environment["CHECKVIES_API_KEY"]
                ?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Environment variable CHECKVIES_API_KEY is required")

            return AppConfig(
                apiKey = apiKey,
                baseUrl = environment["CHECKVIES_BASE_URL"].orDefault(DEFAULT_BASE_URL),
                requestTimeoutMillis = environment["CHECKVIES_REQUEST_TIMEOUT_MS"].toLongOrDefault(DEFAULT_REQUEST_TIMEOUT_MS),
                connectTimeoutMillis = environment["CHECKVIES_CONNECT_TIMEOUT_MS"].toLongOrDefault(DEFAULT_CONNECT_TIMEOUT_MS),
                socketTimeoutMillis = environment["CHECKVIES_SOCKET_TIMEOUT_MS"].toLongOrDefault(DEFAULT_SOCKET_TIMEOUT_MS),
                clientLogLevel = environment["CHECKVIES_HTTP_LOG_LEVEL"].toLogLevelOrDefault(LogLevel.ALL)
            )
        }

        private fun String?.orDefault(defaultValue: String): String =
            this?.takeIf { it.isNotBlank() } ?: defaultValue

        private fun String?.toLongOrDefault(defaultValue: Long): Long =
            this?.toLongOrNull() ?: defaultValue

        private fun String?.toLogLevelOrDefault(defaultValue: LogLevel): LogLevel =
            this
                ?.takeIf { it.isNotBlank() }
                ?.uppercase()
                ?.let { value ->
                    LogLevel.entries.firstOrNull { it.name == value }
                }
                ?: defaultValue
    }
}
