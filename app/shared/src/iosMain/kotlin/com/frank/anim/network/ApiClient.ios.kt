package com.frank.anim.network

import com.frank.anim.network.model.RemotePhoto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private fun createClient(): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        )
    }
    install(HttpTimeout)
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = Unit
        }
        level = LogLevel.INFO
    }
}

actual suspend fun getRemotePhotosFromPlatform(url: String): Result<List<RemotePhoto>> = runCatching {
    val client = createClient()
    try {
        client.get(url).body()
    } finally {
        client.close()
    }
}

actual suspend fun downloadBytesFromPlatform(url: String): ByteArray? = runCatching {
    val client = createClient()
    try {
        client.get(url).body<ByteArray>()
    } finally {
        client.close()
    }
}.getOrNull()
