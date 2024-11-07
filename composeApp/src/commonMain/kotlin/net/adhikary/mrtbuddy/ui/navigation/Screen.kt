package net.adhikary.mrtbuddy.ui.navigation

/**
 * Represents the different screens in the application
 */
sealed class Screen {
    object CardScan : Screen()
    object FareCalculator : Screen()
}
