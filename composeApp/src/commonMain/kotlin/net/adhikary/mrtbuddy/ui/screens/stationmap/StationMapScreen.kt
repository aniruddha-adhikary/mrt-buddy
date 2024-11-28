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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.stationMap
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ovh.plrapps.mapcompose.ui.MapUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationMapScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: StationMapViewModel = koinViewModel(),
) {
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


        LaunchedEffect(Unit) {

        }

        MapUI(
            Modifier,
            state = viewModel.state
        )
    }
}