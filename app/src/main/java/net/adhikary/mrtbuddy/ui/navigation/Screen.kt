package net.adhikary.mrtbuddy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object FareCalculator : Screen(
        route = "fare_calculator",
        title = "Fare",
        icon = Icons.Default.Calculate
    )

    object CardAlias : Screen(
        route = "card_alias",
        title = "Cards",
        icon = Icons.Default.CreditCard
    )

    object MetroSchedule : Screen(
        route = "metro_schedule",
        title = "Schedule",
        icon = Icons.Default.Schedule
    )

    object StationsMap : Screen(
        route = "stations_map",
        title = "Map",
        icon = Icons.Default.Map
    )

    object MonthlyReports : Screen(
        route = "monthly_reports",
        title = "Reports",
        icon = Icons.Default.Assessment
    )

    companion object {
        fun bottomNavItems() = listOf(Home, FareCalculator, CardAlias, MetroSchedule, StationsMap, MonthlyReports)

        fun fromRoute(route: String?): Screen {
            return when (route) {
                Home.route -> Home
                FareCalculator.route -> FareCalculator
                CardAlias.route -> CardAlias
                MetroSchedule.route -> MetroSchedule
                StationsMap.route -> StationsMap
                MonthlyReports.route -> MonthlyReports
                else -> Home
            }
        }
    }
}
