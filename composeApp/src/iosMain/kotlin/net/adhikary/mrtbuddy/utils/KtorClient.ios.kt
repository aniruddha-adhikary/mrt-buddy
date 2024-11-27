package net.adhikary.mrtbuddy.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun getKtorClient(): HttpClient {
    return HttpClient(Darwin) {
        engine {
            configureRequest {
                setAllowsCellularAccess(true)
            }
        }
    }
}