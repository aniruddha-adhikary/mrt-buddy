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
        val fares = mapOf(
            "Uttara North" to 0,
            "Uttara Center" to 20,
            "Uttara South" to 20,
            "Pallabi" to 30,
            "Mirpur 11" to 30,
            "Mirpur 10" to 40,
            "Kazipara" to 40,
            "Shewrapara" to 50,
            "Agargaon" to 50,
            "Bijoy Sarani" to 60,
            "Farmgate" to 60,
            "Karwan Bazar" to 70,
            "Shahbagh" to 80,
            "Dhaka University" to 90,
            "Bangladesh Secretariat" to 90,
            "Motijheel" to 100
        )

        fun calculateFare(start: String, end: String): Int {
            if (!fares.containsKey(start) || !fares.containsKey(end)) return -1
            return kotlin.math.abs(fares[end]!! - fares[start]!!)
        }

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

        // Verify fare calculation using updated logic
        assertEquals(calculateFare(firstStation.name, lastStation.name), viewModel.calculatedFare)
    }

    @Test
    fun testSameStationFare() {
        val station = viewModel.stations.first()
        viewModel.updateFromStation(station)
        viewModel.updateToStation(station)

        // Updated test case with new fare logic
        assertEquals(0, viewModel.calculatedFare) // Same station should have 0 fare
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
    fun testSameStationWithBalance() {
        val station = viewModel.stations.first()
        viewModel.updateFromStation(station)
        viewModel.updateToStation(station)
        viewModel.updateCardState(CardState.Balance(1000))

        assertEquals(0, viewModel.calculatedFare)
        assertEquals(true, viewModel.hasEnoughBalance()) // Should always have enough balance for 0 fare
    }
}
