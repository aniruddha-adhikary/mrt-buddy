package net.adhikary.mrtbuddy.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun StationsMapScreen() {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Initialize osmdroid configuration
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    val stations = listOf(
        MapStation("Uttara North", 23.8716, 90.3897),
        MapStation("Uttara Center", 23.8605, 90.3897),
        MapStation("Uttara South", 23.8494, 90.3900),
        MapStation("Pallabi", 23.8283, 90.3775),
        MapStation("Mirpur-11", 23.8196, 90.3775),
        MapStation("Mirpur-10", 23.8066, 90.3686),
        MapStation("Kazipara", 23.7977, 90.3686),
        MapStation("Shewrapara", 23.7888, 90.3686),
        MapStation("Agargaon", 23.7781, 90.3789),
        MapStation("Bijoy Sarani", 23.7644, 90.3889),
        MapStation("Farmgate", 23.7575, 90.3925),
        MapStation("Karwan Bazar", 23.7508, 90.3933),
        MapStation("Shahbagh", 23.7397, 90.3958),
        MapStation("Dhaka University", 23.7328, 90.3975),
        MapStation("Secretariat", 23.7275, 90.4047),
        MapStation("Motijheel", 23.7233, 90.4175),
        MapStation("Kamalapur", 23.7331, 90.4264)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    controller.setZoom(12.0)
                    controller.setCenter(GeoPoint(23.773710, 90.3815369)) // Dhaka center

                    // Add station markers
                    stations.forEach { station ->
                        val marker = Marker(this)
                        marker.position = GeoPoint(station.latitude, station.longitude)
                        marker.title = station.name
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        overlays.add(marker)
                    }

                    // Add user location if permission granted
                    if (hasLocationPermission) {
                        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
                        locationOverlay.enableMyLocation()
                        overlays.add(locationOverlay)
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { mapView ->
                mapView.onResume()
            }
        )
        Text(
            text = "Implemented by Irfan",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://irfanhasan.vercel.app/"))
                    context.startActivity(intent)
                },
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

data class MapStation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
