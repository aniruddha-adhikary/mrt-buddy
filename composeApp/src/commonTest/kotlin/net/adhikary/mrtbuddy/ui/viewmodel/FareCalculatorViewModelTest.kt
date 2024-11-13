package net.adhikary.mrtbuddy.ui.viewmodel

import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FareCalculatorViewModelTest {
    private lateinit var viewModel: FareCalculatorViewModel

    @BeforeTest
    fun setup() {
        FareCalculatorViewModel.reset()
        viewModel = FareCalculatorViewModel.getInstance()
    }

    @Test
    fun testInitialState() {
        assertNull(viewModel.fromStation)
        assertNull(viewModel.toStation)
        assertEquals(0, viewModel.calculatedFare)
        assertEquals(false, viewModel.fromExpanded)
        assertEquals(false, viewModel.toExpanded)
    }

    @Test
    fun testStationSelection() {
        // Get first and last stations
        val firstStation = viewModel.stations.first()
        val lastStation = viewModel.stations.last()

        // Test setting from station
        viewModel.updateFromStation(firstStation)
        assertEquals(firstStation, viewModel.fromStation)
        assertEquals(false, viewModel.fromExpanded)

        // Test setting to station
        viewModel.updateToStation(lastStation)
        assertEquals(lastStation, viewModel.toStation)
        assertEquals(false, viewModel.toExpanded)

        // Verify fare calculation
        assertEquals(100, viewModel.calculatedFare) // Maximum fare for furthest stations
    }

    @Test
    fun testDropdownToggle() {
        // Test from dropdown
        viewModel.toggleFromExpanded()
        assertEquals(true, viewModel.fromExpanded)
        assertEquals(false, viewModel.toExpanded)

        // Test to dropdown
        viewModel.toggleToExpanded()
        assertEquals(false, viewModel.fromExpanded)
        assertEquals(true, viewModel.toExpanded)

        // Test dismiss
        viewModel.dismissDropdowns()
        assertEquals(false, viewModel.fromExpanded)
        assertEquals(false, viewModel.toExpanded)
    }

    @Test
    fun testSameStationFare() {
        val station = viewModel.stations.first()
        viewModel.updateFromStation(station)
        viewModel.updateToStation(station)
        assertEquals(0, viewModel.calculatedFare) // Same station should have 0 fare
        assertEquals(0, viewModel.discountedFare) // Discounted fare should also be 0
    }

    @Test
    fun testSameStationWithBalance() {
        val station = viewModel.stations.first()
        viewModel.updateFromStation(station)
        viewModel.updateToStation(station)
        viewModel.updateCardState(CardState.Balance(1000))
        assertEquals(0, viewModel.calculatedFare)
        assertEquals(true, viewModel.hasEnoughBalance()) // Should always have enough balance for 0 fare
    }
}
