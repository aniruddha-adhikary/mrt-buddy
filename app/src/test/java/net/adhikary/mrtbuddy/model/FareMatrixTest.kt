package net.adhikary.mrtbuddy.model

import org.junit.Test
import org.junit.Assert.*

class FareMatrixTest {
    @Test
    fun `test Uttara South to Farmgate fare calculation`() {
        val fare = FareMatrix.getFare("Uttara South", "Farmgate")
        assertEquals("Regular fare from Uttara South to Farmgate should be 70 BDT", 70, fare)

        // Calculate pass fare (10% discount)
        val passFare = (fare * 0.9).toInt()
        assertEquals("MRT/Rapid Pass fare should be 63 BDT (10% discount)", 63, passFare)
    }

    @Test
    fun `test same station fare calculation`() {
        // Same station fares should be 0 BDT
        assertEquals("Same station fare should be 0 BDT", 0, FareMatrix.getFare("Uttara North", "Uttara North"))
        assertEquals("Same station fare should be 0 BDT", 0, FareMatrix.getFare("Bangladesh Secretariat", "Bangladesh Secretariat"))
        assertEquals("Same station fare should be 0 BDT", 0, FareMatrix.getFare("Mirpur-11", "Mirpur-11"))
    }

    @Test
    fun `test reverse direction fare calculation`() {
        // Test multiple station pairs for symmetric fares
        val pairs = listOf(
            "Uttara South" to "Farmgate",
            "Mirpur-11" to "Bangladesh Secretariat",
            "Agargaon" to "Kamalapur",
            "Pallabi" to "Shahbagh"
        )

        pairs.forEach { (station1, station2) ->
            val forwardFare = FareMatrix.getFare(station1, station2)
            val reverseFare = FareMatrix.getFare(station2, station1)
            assertEquals("Fare should be same in both directions for $station1 and $station2", forwardFare, reverseFare)
        }
    }

    @Test
    fun `test various station combinations`() {
        // Test different starting stations with multiple destinations
        val testCases = listOf(
            Triple("Uttara North", "Motijheel", 100),
            Triple("Uttara Center", "Bangladesh Secretariat", 100),
            Triple("Uttara South", "Kamalapur", 100),
            Triple("Pallabi", "Motijheel", 80),
            Triple("Mirpur-11", "Bangladesh Secretariat", 80),
            Triple("Mirpur-10", "Kamalapur", 80),
            Triple("Agargaon", "Motijheel", 60),
            Triple("Bijoy Sarani", "Shahbagh", 30),  // Fixed: fare from Bijoy Sarani to Shahbagh is 30, not 40
            Triple("Farmgate", "Karwan Bazar", 20),
            Triple("Mirpur-10", "Kazipara", 20)
        )

        testCases.forEach { (from, to, expectedFare) ->
            val actualFare = FareMatrix.getFare(from, to)
            assertEquals(
                "Fare from $from to $to should be $expectedFare BDT",
                expectedFare,
                actualFare
            )
        }
    }

    @Test
    fun `test empty station handling`() {
        assertEquals("Empty from station should return 0",
            0, FareMatrix.getFare("", "Farmgate"))
        assertEquals("Empty to station should return 0",
            0, FareMatrix.getFare("Uttara South", ""))
        assertEquals("Both empty stations should return 0",
            0, FareMatrix.getFare("", ""))
    }

    @Test
    fun `test invalid station handling`() {
        assertEquals("Invalid from station should return 0", 0, FareMatrix.getFare("Invalid Station", "Farmgate"))
        assertEquals("Invalid to station should return 0", 0, FareMatrix.getFare("Uttara North", "Invalid Station"))
        assertEquals("Both invalid stations should return 0", 0, FareMatrix.getFare("Invalid1", "Invalid2"))
    }
}
