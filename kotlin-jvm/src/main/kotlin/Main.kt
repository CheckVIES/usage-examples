package com.checkvies.example

import com.checkvies.client.CheckViesException
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val config = AppConfig.fromEnvironment()
    println("Starting CheckVies example against ${config.baseUrl}")

    val service = CheckViesService(config)

    try {
        service.runDemo()
    } catch (e: CheckViesException) {
        System.err.println("CheckVies API error: code=${e.errorCode}, status=${e.statusCode}, message=${e.message}")
    } catch (e: Throwable) {
        System.err.println("Unexpected error: ${e.message}")
        e.printStackTrace()
    }
}