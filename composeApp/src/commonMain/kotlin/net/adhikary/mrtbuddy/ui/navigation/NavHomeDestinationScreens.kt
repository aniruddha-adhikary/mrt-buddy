package net.adhikary.mrtbuddy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavHomeDestinationScreens(
    val title: String,
    val activeIcon: ImageVector,
    val disabledIcon: ImageVector,
    val route: String
) {
    data object HomeScreen : NavHomeDestinationScreens(
        title = "Balance",
        activeIcon = Icons.Filled.CreditCard,
        disabledIcon = Icons.Outlined.CreditCard,
        route = "balance_route"
    )

    data object FareScreen : NavHomeDestinationScreens(
        title = "Fare",
        activeIcon = Icons.Filled.Calculate,
        disabledIcon = Icons.Outlined.Calculate,
        route = "fare_route"
    )

    data object SettingsScreen : NavHomeDestinationScreens(
        title = "Settings",
        activeIcon = Icons.Filled.Settings,
        disabledIcon = Icons.Outlined.Settings,
        route = "settings_route"
    )
}
