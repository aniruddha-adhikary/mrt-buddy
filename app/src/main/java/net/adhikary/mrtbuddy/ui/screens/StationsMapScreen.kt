package net.adhikary.mrtbuddy.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsMapScreen() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    // Configuration is handled by MrtBuddyApplication

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Stations Map") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Map view takes most of the space
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                controller.setZoom(13.0)
                                controller.setCenter(GeoPoint(23.8103, 90.4125)) // Dhaka coordinates

                                // Add location overlay
                                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
                                locationOverlay.enableMyLocation()
                                overlays.add(locationOverlay)

                                // Add markers for MRT stations
                                val stations = listOf(
                                    "Uttara North" to GeoPoint(23.8759, 90.3995),
                                    "Uttara Center" to GeoPoint(23.8687, 90.3989),
                                    "Uttara South" to GeoPoint(23.8614, 90.3983),
                                    "Pallabi" to GeoPoint(23.8283, 90.3722),
                                    "Mirpur-11" to GeoPoint(23.8179, 90.3686),
                                    "Mirpur-10" to GeoPoint(23.8066, 90.3686),
                                    "Kazipara" to GeoPoint(23.7977, 90.3722),
                                    "Shewrapara" to GeoPoint(23.7892, 90.3731),
                                    "Agargaon" to GeoPoint(23.7787, 90.3789),
                                    "Bijoy Sarani" to GeoPoint(23.7639, 90.3886),
                                    "Farmgate" to GeoPoint(23.7572, 90.3914),
                                    "Karwan Bazar" to GeoPoint(23.7506, 90.3931),
                                    "Shahbagh" to GeoPoint(23.7389, 90.3956),
                                    "Dhaka University" to GeoPoint(23.7328, 90.3975),
                                    "Bangladesh Secretariat" to GeoPoint(23.7267, 90.4008),
                                    "Motijheel" to GeoPoint(23.7233, 90.4181),
                                    "Kamalapur" to GeoPoint(23.7331, 90.4264)
                                )

                                stations.forEach { (name, point) ->
                                    val marker = Marker(this)
                                    marker.position = point
                                    marker.title = name
                                    overlays.add(marker)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Attribution text at the bottom
                Text(
                    text = "Implemented by Irfan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { uriHandler.openUri("https://irfanhasan.vercel.app/") }
                        .padding(8.dp)
                )
            }
        }
    }
}
