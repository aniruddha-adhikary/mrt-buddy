package net.adhikary.mrtbuddy

import net.adhikary.mrtbuddy.data.model.FareCalculator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FareCalculatorTest {
    private val calculator = FareCalculator

    @Test
    fun testStationList() {
        val stations = calculator.getAllStations()
        assertEquals(17, stations.size)
        assertEquals("Uttara North", stations[0].name)
        assertEquals("Kamalapur", stations[16].name)
    }

    @Test
    fun testFareCalculation() {
        val stations = calculator.getAllStations().associateBy { it.name }
        
        // Test maximum fare
        assertEquals(100, calculator.calculateFare(stations["Uttara North"]!!, stations["Motijheel"]!!))
        // Test reverse direction
        assertEquals(100, calculator.calculateFare(stations["Motijheel"]!!, stations["Uttara North"]!!))
        // Test intermediate station fare
        assertEquals(20, calculator.calculateFare(stations["Karwan Bazar"]!!, stations["Bangladesh Secretariat"]!!))
        assertEquals(30, calculator.calculateFare(stations["Mirpur 10"]!!, stations["Farmgate"]!!))
        // Test same station (should be free)
        assertEquals(0, calculator.calculateFare(stations["Farmgate"]!!, stations["Farmgate"]!!))
        // Test Kamalapur station
        assertEquals(100, calculator.calculateFare(stations["Uttara North"]!!, stations["Kamalapur"]!!))
    }

    @Test
    fun testStationLookup() {
        val stations = calculator.getAllStations().associateBy { it.name }

        // Test lookup by name
        val stationByName = stations["Farmgate"]
        assertNotNull(stationByName)

        // Test invalid lookups
        assertNull(stations["Invalid Station"])
    }
}
