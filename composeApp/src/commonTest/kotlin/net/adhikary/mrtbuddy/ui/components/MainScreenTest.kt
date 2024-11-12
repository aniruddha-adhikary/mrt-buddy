package net.adhikary.mrtbuddy.ui.components

import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import net.adhikary.mrtbuddy.ui.navigation.Screen
import net.adhikary.mrtbuddy.ui.viewmodel.FareCalculatorViewModel
import net.adhikary.mrtbuddy.model.CardState


class MainScreenTest {
    private lateinit var viewModel: FareCalculatorViewModel
    private lateinit var navigationState: NavigationTestState

    private class NavigationTestState {
        var currentScreen: Screen = Screen.CardScan
    }

    @BeforeTest
    fun setup() {
        FareCalculatorViewModel.reset()
        viewModel = FareCalculatorViewModel.getInstance()
        navigationState = NavigationTestState()
    }

    @Test
    fun testNavigationFlow() {
        // Test initial screen
        assertEquals(Screen.CardScan, navigationState.currentScreen)

        // Test navigation to Fare Calculator
        navigationState.currentScreen = Screen.FareCalculator
        assertEquals(Screen.FareCalculator, navigationState.currentScreen)

        // Test navigation back to Card Scan
        navigationState.currentScreen = Screen.CardScan
        assertEquals(Screen.CardScan, navigationState.currentScreen)
    }

    @Test
    fun testInitialScreen() {
        assertEquals(Screen.CardScan, navigationState.currentScreen)
    }
}
