package net.adhikary.mrtbuddy.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.nav_map
import mrtbuddy.composeapp.generated.resources.nearest_metro
import org.jetbrains.compose.resources.stringResource

/**
 * Created by Shakil Ahmed Shaj on 10,Nov,2024.
 * shakilahmedshaj@gmail.com
 */

@Composable
actual fun MetroMap() {
    val context = LocalContext.current
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM)
    }

    var permissionGranted by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var nearestStation by remember { mutableStateOf<MetroModel?>(null) }
    var showDefaultMarker by remember { mutableStateOf(false) }

    RequestLocationPermission {
        permissionGranted = true
    }

    LaunchedEffect(currentLocation, nearestStation) {
        if (currentLocation != null && nearestStation != null) {
            val bounds = LatLngBounds.builder()
                .include(currentLocation!!)
                .include(nearestStation!!.location)
                .build()
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        } else if (currentLocation != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(currentLocation!!, DEFAULT_ZOOM)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMapUI(
            cameraPositionState = cameraPositionState,
            permissionGranted = permissionGranted,
            metroStations = METRO_STATIONS,
            currentLocation = currentLocation,
            nearestStation = nearestStation,
            showDefaultMarker = showDefaultMarker
        )

        MoveToNearestMetroButton(
            onMoveToLocation = {
                if (permissionGranted) {
                    handleCurrentLocation(
                        fusedLocationProviderClient = fusedLocationProviderClient,
                        onCurrentLocationUpdate = { location -> currentLocation = location },
                        onNearestStationUpdate = { station -> nearestStation = station },
                        stations = METRO_STATIONS,
                        context = context
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 120.dp, end = 8.dp) // padding to avoid screen edges
        )
    }
}

@Composable
fun GoogleMapUI(
    cameraPositionState: CameraPositionState,
    permissionGranted: Boolean,
    metroStations: List<MetroModel>,
    currentLocation: LatLng?,
    nearestStation: MetroModel?,
    showDefaultMarker: Boolean
) {
    val context = LocalContext.current

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = true,
            compassEnabled = true
        ),
        properties = MapProperties(isMyLocationEnabled = permissionGranted)
    ) {
        metroStations.forEach { station ->
            val markerState = remember { MarkerState(position = station.location) }

            // Conditionally display the default marker
            if (showDefaultMarker) {
                Marker(
                    state = markerState,
                    title = station.name,
                    snippet = if (station == nearestStation) "Nearest Metro Station" else null,
                    icon = if (station == nearestStation) {
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    } else {
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    }
                )
            }

            // Custom label marker
            val isNearest = station == nearestStation
            val offsetLocation = station.location.offset(0.0, 0.0002) // Adjust offset as needed
            val labelBitmap = createCustomMarker(context, station.name, isNearest) // No `remember` here
            Marker(
                state = MarkerState(position = offsetLocation),
                icon = labelBitmap
            )
        }

        // Add a marker for the current location
        currentLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Your Location",
                icon = if (nearestStation != null) {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                } else {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                }
            )
        }

        // Draw connecting lines between stations
        if (metroStations.isNotEmpty()) {
            Polyline(
                points = metroStations.map { it.location },
                color = Color.Blue,
                width = 5f
            )
        }

        // Draw a line between the current location and the nearest station
        if (currentLocation != null && nearestStation != null) {
            Polyline(
                points = listOf(currentLocation, nearestStation.location),
                color = Color.Green,
                width = 5f
            )
        }
    }
}

fun createCustomMarker(context: Context, text: String, isNearest: Boolean = false): BitmapDescriptor {
    val baseHeight = 100
    val padding = 20f // padding around the text

    val textPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 36f // font size
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    val textWidth = textPaint.measureText(text)
    val markerWidth = (textWidth + (2 * padding)).toInt()
    val markerHeight = baseHeight

    val bitmap = Bitmap.createBitmap(markerWidth, markerHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val backgroundPaint = Paint().apply {
        color = if (isNearest) android.graphics.Color.GREEN else android.graphics.Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Draw rounded rectangle background
    canvas.drawRoundRect(
        0f,
        0f,
        markerWidth.toFloat(),
        markerHeight.toFloat(),
        20f, // Corner radius X
        20f, // Corner radius Y
        backgroundPaint
    )

    val xPos = markerWidth / 2f
    val yPos = (markerHeight / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)

    canvas.drawText(text, xPos, yPos, textPaint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) onPermissionGranted() }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Composable
fun MoveToNearestMetroButton(onMoveToLocation: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onMoveToLocation,
        modifier = modifier
    ) {
        Text(stringResource(Res.string.nearest_metro))
    }
}

fun handleCurrentLocation(
    context: android.content.Context,
    fusedLocationProviderClient: com.google.android.gms.location.FusedLocationProviderClient,
    onCurrentLocationUpdate: (LatLng) -> Unit,
    onNearestStationUpdate: (MetroModel?) -> Unit,
    stations: List<MetroModel>
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        return
    }

    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
        if (location == null) {
            Toast.makeText(context, "Unable to fetch current location", Toast.LENGTH_SHORT).show()
            return@addOnSuccessListener
        }

        val currentLatLng = LatLng(location.latitude, location.longitude)
        onCurrentLocationUpdate(currentLatLng)

        // Find the nearest metro station
        val nearest = stations.minByOrNull { station ->
            val dx = currentLatLng.latitude - station.location.latitude
            val dy = currentLatLng.longitude - station.location.longitude
            Math.sqrt(dx * dx + dy * dy)
        }

        onNearestStationUpdate(nearest)

        nearest?.let {
            Toast.makeText(context, "Nearest Metro Station: ${it.name}", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(context, "No nearby metro stations found", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Error fetching location: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// for easy maintenance these value are placed below, once ios version developed, we should move these to separate files

val DEFAULT_LOCATION = LatLng(23.798765492096376, 90.38918652845055)

const val DEFAULT_ZOOM = 12f

val METRO_STATIONS = listOf(
    MetroModel("Uttara North", LatLng(23.869471207716987, 90.36755343277423)),
    MetroModel("Uttara Center", LatLng(23.859678651226307, 90.3650513890263)),
    MetroModel("Uttara South", LatLng(23.84755359557104, 90.36366587422005)),
    MetroModel("Pallabi", LatLng(23.826305182548563, 90.3642373690619)),
    MetroModel("Mirpur 11", LatLng(23.819318824040355, 90.36526741637955)),
    MetroModel("Mirpur 10", LatLng(23.80859625981341, 90.36811057917744)),
    MetroModel("Kazipara", LatLng(23.800611241398062, 90.37175651713184)),
    MetroModel("Shewrapara", LatLng(23.791869924905853, 90.37557238552674)),
    MetroModel("Agargaon", LatLng(23.77988914074858, 90.38008498964417)),
    MetroModel("Bijoy Sarani", LatLng(23.76663577392801, 90.38315755342117)),
    MetroModel("Farmgate", LatLng(23.759049516640832, 90.38699796246453)),
    MetroModel("Karwan Bazar", LatLng(23.751260890448197, 90.39275548126527)),
    MetroModel("Shahbagh", LatLng(23.739569746008907, 90.39606747923115)),
    MetroModel("Dhaka University", LatLng(23.73188795267384, 90.39658925524438)),
    MetroModel("Bangladesh Secretariat", LatLng(23.73002373248729, 90.40672048740873)),
    MetroModel("Motijheel", LatLng(23.728058576887513, 90.41925162856687))
)

data class MetroModel(val name: String, val location: LatLng)

fun LatLng.offset(latOffset: Double, lngOffset: Double): LatLng {
    return LatLng(this.latitude + latOffset, this.longitude + lngOffset)
}