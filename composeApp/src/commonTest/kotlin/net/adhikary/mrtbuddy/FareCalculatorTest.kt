package net.adhikary.mrtbuddy

import net.adhikary.mrtbuddy.data.model.FareCalculator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FareCalculatorTest {
    private val calculator = FareCalculator.getInstance()

    @Test
    fun testStationList() {
        val stations = calculator.getAllStations()
        assertEquals(17, stations.size)
        assertEquals("Uttara North", stations[0].name)
        assertEquals("Kamalapur", stations[16].name)
    }

    @Test
    fun testFareCalculation() {
        val uttaraNorth = calculator.getStation(0)!!
        val kamalapur = calculator.getStation(16)!!
        val farmgate = calculator.getStation(10)!!

        // Test maximum fare
        assertEquals(100, calculator.calculateFare(uttaraNorth, kamalapur))
        // Test reverse direction (should be same due to symmetry)
        assertEquals(100, calculator.calculateFare(kamalapur, uttaraNorth))
        // Test intermediate station fare
        assertEquals(70, calculator.calculateFare(uttaraNorth, farmgate))
        // Test same station (should be free)
        assertEquals(0, calculator.calculateFare(uttaraNorth, uttaraNorth))
    }

    @Test
    fun testStationLookup() {
        // Test lookup by name
        val stationByName = calculator.getStation("Farmgate")
        assertNotNull(stationByName)
        assertEquals(10, stationByName.id)

        // Test lookup by ID
        val stationById = calculator.getStation(10)
        assertNotNull(stationById)
        assertEquals("Farmgate", stationById.name)

        // Test invalid lookups
        assertNull(calculator.getStation("Invalid Station"))
        assertNull(calculator.getStation(99))
    }
}
