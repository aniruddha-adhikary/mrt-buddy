package net.adhikary.mrtbuddy.ui.screens.stationmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.marker
import mrtbuddy.composeapp.generated.resources.marker_train
import net.adhikary.mrtbuddy.data.model.MarkerInfo
import net.adhikary.mrtbuddy.data.model.StationMapData
import net.adhikary.mrtbuddy.makeOsmTileStreamProvider
import net.adhikary.mrtbuddy.ui.components.InfoWindow
import org.jetbrains.compose.resources.painterResource
import ovh.plrapps.mapcompose.api.ExperimentalClusteringApi
import ovh.plrapps.mapcompose.api.addCallout
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.addLazyLoader
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.addPath
import ovh.plrapps.mapcompose.api.centerOnMarker
import ovh.plrapps.mapcompose.api.enableRotation
import ovh.plrapps.mapcompose.api.onMarkerClick
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.shouldLoopScale
import ovh.plrapps.mapcompose.ui.layout.Forced
import ovh.plrapps.mapcompose.ui.state.MapState
import ovh.plrapps.mapcompose.ui.state.markers.model.RenderingStrategy
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

@OptIn(ExperimentalClusteringApi::class)
class StationMapViewModel : ViewModel() {

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val tileStreamProvider = makeOsmTileStreamProvider()

    private val maxLevel = 16
    private val minLevel = 12
    private val mapSize = mapSizeAtLevel(maxLevel, tileSize = 256)

    val state = MapState(levelCount = maxLevel + 1, mapSize, mapSize, workerCount = 16) {
        minimumScaleMode(Forced((1 / 2.0.pow(maxLevel - minLevel)).toFloat()))

        val (centerLat, centerLon) = calculateBoundingBox(StationMapData.markers)
        val (scrollX, scrollY) = latLonToMapCoordinates(centerLat, centerLon)
        scroll(scrollX, scrollY)
    }.apply {
        addLayer(tileStreamProvider)
        shouldLoopScale = true
        enableRotation()
        scale = 0.2f

        onMarkerClick { id, x, y ->
            var shouldAnimate by mutableStateOf(true)
            addCallout(
                id, x, y,
                absoluteOffset = DpOffset(0.dp, (-20).dp),
            ) {
                InfoWindow(title = id, shouldAnimate) {
                    shouldAnimate = false
                }
            }

            viewModelScope.launch {
                centerOnMarker(id)
            }
        }
    }

    init {
        state.addLazyLoader("default")

        StationMapData.markers.mapIndexed { index, station ->
            val (x, y) = latLonToMapCoordinates(station.lat, station.lon)

            state.addMarker(
                station.name, x, y,
                renderingStrategy = RenderingStrategy.LazyLoading(lazyLoaderId = "default")
            ) {
                Image(
                    painter = painterResource(
                        if (index == 0) Res.drawable.marker_train else Res.drawable.marker
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                )
            }
        }

        state.addPath(
            id = "route",
            color = Color(0xFF0E9D58),
        ) {
            for (line in StationMapData.routes) {
                val (x, y) = latLonToMapCoordinates(line.first, line.second)
                addPoint(x, y)
            }
        }

        state.addPath(
            id = "polygon",
            color = Color(0xFF0E9D58),
            fillColor = Color(0xFF0E9D58).copy(alpha = .6f),
        ) {
            for (polygon in StationMapData.depot) {
                val (x, y) = latLonToMapCoordinates(polygon.first, polygon.second)
                addPoint(x, y)
            }
        }
    }

    private fun calculateBoundingBox(stations: List<MarkerInfo>): Pair<Double, Double> {
        val minLat = stations.minOf { it.lat }
        val maxLat = stations.maxOf { it.lat }
        val minLon = stations.minOf { it.lon }
        val maxLon = stations.maxOf { it.lon }

        val centerLat = (minLat + maxLat) / 2
        val centerLon = (minLon + maxLon) / 2

        return (centerLat to centerLon)
    }

    // Converts latitude and longitude to map coordinates at a specific zoom level
    private fun latLonToMapCoordinates(lat: Double, lon: Double): Pair<Double, Double> {
        val x = (lon + 180.0) / 360.0
        val y = 0.5 - ln(tan(PI * lat / 180.0) + 1 / cos(PI * lat / 180.0)) / (2 * PI)

        val pixelX = x * mapSize
        val pixelY = y * mapSize

        // Normalize to [0, 1]
        return pixelX / mapSize to pixelY / mapSize
    }


    /**
     * wmts level are 0 based.
     * At level 0, the map corresponds to just one tile.
     */
    private fun mapSizeAtLevel(wmtsLevel: Int, tileSize: Int): Int {
        return tileSize * 2.0.pow(wmtsLevel).toInt()
    }
}