package net.adhikary.mrtbuddy.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RoundaboutLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Created by Pronay Sarker on 12/11/2024 (1:59 AM)
 */
@Composable
fun SettingScreenRoute(
    modifier: Modifier = Modifier,
    prefs: DataStore<Preferences>
) {
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val themes = remember {
        listOf(
            Themes.Light,
            Themes.Dark,
            Themes.SystemDefault
        )
    }
    val initialTheme by prefs.data.map {
        val themekey = intPreferencesKey("themeKey")
        it[themekey] ?: 0
    }.collectAsState(0)

    var currentThemeIndex by rememberSaveable { mutableStateOf(initialTheme) }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(text = "Select Theme", style = MaterialTheme.typography.h6)
            },
            text = {
                Column {
                    themes.forEachIndexed { _, themes ->
                        ThemeOptionRow(
                            label = themes.title,
                            isSelected = currentThemeIndex == themes.themeSerialNo,
                            onOptionSelected = {
                                currentThemeIndex = themes.themeSerialNo
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            showThemeDialog = false
                            prefs.edit { preferences ->
                                val themeKey = intPreferencesKey("themeKey")
                                preferences[themeKey] = currentThemeIndex
                            }
                        }
                    }
                ) {
                    Text("Set Theme")
                }
            },
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column {
            SettingsItem(
                title = "Change Theme",
                details = "Tap here to change device theme",
                icon = Icons.Filled.Nightlight,
                onclick = {
                    showThemeDialog = true
                }
            )
            SettingsItem(
                title = "View Source",
                details = "view source code of this app",
                icon = Icons.Filled.Code,
                onclick = {
                    //TODO add source code link
                }
            )
            SettingsItem(
                title = "About MRTBuddy",
                details = "view about",
                icon = Icons.Filled.Info,
                onclick = {

                }
            )
        }
    }
}

sealed class Themes(
    val title: String,
    val themeSerialNo: Int
) {
    data object Light : Themes("Light", 2)
    data object Dark : Themes("Dark", 1)
    data object SystemDefault : Themes("System Default", 0)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsItem(
    title: String,
    details: String,
    icon: ImageVector?,
    onclick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        onClick = { onclick.invoke() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            icon?.let {
                Icon(
                    tint = MaterialTheme.colors.primary,
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.weight(0.2f)
                )
            }
            if (icon == null) {
                Spacer(modifier = Modifier.weight(0.2f))
            }
            Column(
                modifier = Modifier.weight(0.8f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = details,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}


@Composable
private fun ThemeOptionRow(
    label: String,
    isSelected: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onOptionSelected(label) }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onOptionSelected(label) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.body1)
    }
}