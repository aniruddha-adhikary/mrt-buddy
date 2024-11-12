package net.adhikary.mrtbuddy.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.datetime.Month
import net.adhikary.mrtbuddy.ui.screens.fare.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.settings.SettingScreenRoute

/**
 * Created by Pronay Sarker on 12/11/2024 (3:53 AM)
 */
@Composable
fun Navigation(
    contentPadding : PaddingValues,
    state: MainScreenState,
    prefs : DataStore<Preferences>,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavHomeDestinationScreens.HomeScreen.route
    ) {
        composable(
            route = NavHomeDestinationScreens.HomeScreen.route
        ) {
            MainScreen(uiState = state)
        }

        composable(
            route = NavHomeDestinationScreens.FareScreen.route
        ) {
            FareCalculatorScreen(cardState = state.cardState)
        }

        composable(
            route = NavHomeDestinationScreens.SettingsScreen.route
        ) {
            SettingScreenRoute(prefs = prefs, modifier = Modifier.padding(contentPadding))
        }
    }
}