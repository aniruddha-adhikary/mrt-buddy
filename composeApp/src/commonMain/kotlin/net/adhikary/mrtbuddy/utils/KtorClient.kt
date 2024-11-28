package net.adhikary.mrtbuddy.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.io.Buffer
import kotlinx.io.RawSource


expect fun getKtorClient(): HttpClient

/**
 * Utility method to get a [RawSource] from a Ktor HTTP client, using a GET method.
 */
suspend fun getStream(client: HttpClient, path: String): RawSource {
    val buffer = Buffer()
    client.prepareGet(path).execute { httpResponse ->
        val channel: ByteReadChannel = httpResponse.body()
        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
            while (!packet.isEmpty) {
                val bytes = packet.readBytes()
                buffer.write(bytes, 0, bytes.size)
            }
        }
    }
    return buffer
}

private const val DEFAULT_BUFFER_SIZE = 8192