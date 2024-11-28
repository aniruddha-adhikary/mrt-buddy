package net.adhikary.mrtbuddy.ui.screens.stationmap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.stationMap
import net.adhikary.mrtbuddy.Language
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun StationMapScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: StationMapViewModel = koinViewModel(),
) {
    val uiState by viewModel.state.collectAsState()

    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(Res.string.stationMap)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            windowInsets = WindowInsets.statusBars
        )

        CoilZoomAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = if (uiState.currentLanguage == Language.English.isoFormat) {
                Res.getUri("files/map_en.webp")
            } else {
                Res.getUri("files/map_bn.webp")
            },
            contentDescription = "Station Map",
            contentScale = ContentScale.FillHeight,
            filterQuality = FilterQuality.High,
            scrollBar = null
        )
    }
}