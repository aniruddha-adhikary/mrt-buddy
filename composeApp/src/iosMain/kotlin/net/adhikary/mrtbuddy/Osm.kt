package net.adhikary.mrtbuddy

import net.adhikary.mrtbuddy.utils.getKtorClient
import net.adhikary.mrtbuddy.utils.getStream
import ovh.plrapps.mapcompose.core.TileStreamProvider

actual fun makeOsmTileStreamProvider(): TileStreamProvider {
    // so there is no need for an asynchronous http client such as Ktor.
    val httpClient = getKtorClient()
    return TileStreamProvider { row, col, zoomLvl ->
        try {
            getStream(httpClient, "https://tile.openstreetmap.org/$zoomLvl/$col/$row.png")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}