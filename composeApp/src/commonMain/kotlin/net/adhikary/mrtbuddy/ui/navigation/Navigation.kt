package net.adhikary.mrtbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.adhikary.mrtbuddy.ui.screens.fare.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.settings.SettingScreenRoute

/**
 * Created by Pronay Sarker on 12/11/2024 (3:53 AM)
 */
@Composable
fun Navigation(
    state: MainScreenState,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavHomeDestinationScreens.HomeScreen.route
    ) {
        composable(
            route = NavHomeDestinationScreens.HomeScreen.route
        ) {
            MainScreen(
                uiState = state
            )
        }

        composable(
            route = NavHomeDestinationScreens.FareScreen.route
        ) {
            FareCalculatorScreen(cardState = state.cardState)
        }

        composable(
            route = NavHomeDestinationScreens.SettingsScreen.route
        ) {
            SettingScreenRoute()
        }
    }
}