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
        val fare = FareMatrix.getFare("Uttara North", "Uttara North")
        assertEquals("Same station fare should be 20 BDT", 20, fare)
    }

    @Test
    fun `test reverse direction fare calculation`() {
        val forwardFare = FareMatrix.getFare("Uttara South", "Farmgate")
        val reverseFare = FareMatrix.getFare("Farmgate", "Uttara South")
        assertEquals("Fare should be same in both directions", forwardFare, reverseFare)
    }

    @Test
    fun `test various station combinations`() {
        // Test different fare ranges
        assertEquals("Fare from Uttara North to Motijheel should be 100 BDT",
            100, FareMatrix.getFare("Uttara North", "Motijheel"))
        assertEquals("Fare from Pallabi to Motijheel should be 80 BDT",
            80, FareMatrix.getFare("Pallabi", "Motijheel"))
        assertEquals("Fare from Agargaon to Motijheel should be 60 BDT",
            60, FareMatrix.getFare("Agargaon", "Motijheel"))
        assertEquals("Fare from Mirpur-10 to Kazipara should be 20 BDT",
            20, FareMatrix.getFare("Mirpur-10", "Kazipara"))
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
}
