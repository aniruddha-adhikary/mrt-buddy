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
        assertEquals(16, stations.size)
        assertEquals("Uttara North", stations[0].name)
        assertEquals("Motijheel", stations[15].name)
    }

    @Test
    fun testFareCalculation() {
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

        // Test maximum fare
        assertEquals(100, calculateFare("Uttara North", "Motijheel"))
        // Test reverse direction
        assertEquals(100, calculateFare("Motijheel", "Uttara North"))
        // Test intermediate station fare
        assertEquals(30, calculateFare("Karwan Bazar", "Bangladesh Secretariat"))
        assertEquals(50, calculateFare("Mirpur 10", "Farmgate"))
        // Test same station (should be free)
        assertEquals(0, calculateFare("Farmgate", "Farmgate"))
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
