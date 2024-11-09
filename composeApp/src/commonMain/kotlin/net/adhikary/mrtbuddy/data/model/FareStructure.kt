package net.adhikary.mrtbuddy.data.model

/**
 * Represents a metro station in the system
 */
data class Station(
    val name: String,
    val id: Int
)

/**
 * Manages the fare calculation between stations
 */
class FareCalculator {
    private val stations = listOf(
        Station("Uttara North", 0),
        Station("Uttara Center", 1),
        Station("Uttara South", 2),
        Station("Pallabi", 3),
        Station("Mirpur-11", 4),
        Station("Mirpur-10", 5),
        Station("Kazipara", 6),
        Station("Shewrapara", 7),
        Station("Agargaon", 8),
        Station("Bijoy Sarani", 9),
        Station("Farmgate", 10),
        Station("Karwan Bazar", 11),
        Station("Shahbagh", 12),
        Station("Dhaka University", 13),
        Station("Bangladesh Secretariat", 14),
        Station("Motijheel", 15),
        Station("Kamalapur", 16)
    )

    // Fare matrix based on station IDs
    private val fareMatrix = mapOf(
        // Uttara North (0)
        Pair(0 to 0, 0), Pair(0 to 1, 20), Pair(0 to 2, 20), Pair(0 to 3, 30),
        Pair(0 to 4, 30), Pair(0 to 5, 40), Pair(0 to 6, 40), Pair(0 to 7, 40),
        Pair(0 to 8, 60), Pair(0 to 9, 60), Pair(0 to 10, 70), Pair(0 to 11, 70),
        Pair(0 to 12, 80), Pair(0 to 13, 80), Pair(0 to 14, 100), Pair(0 to 15, 100),
        Pair(0 to 16, 100),

        // Uttara Center (1)
        Pair(1 to 1, 0), Pair(1 to 2, 20), Pair(1 to 3, 30), Pair(1 to 4, 30),
        Pair(1 to 5, 40), Pair(1 to 6, 40), Pair(1 to 7, 40), Pair(1 to 8, 60),
        Pair(1 to 9, 60), Pair(1 to 10, 70), Pair(1 to 11, 70), Pair(1 to 12, 80),
        Pair(1 to 13, 80), Pair(1 to 14, 100), Pair(1 to 15, 100), Pair(1 to 16, 100),

        // Uttara South (2)
        Pair(2 to 2, 0), Pair(2 to 3, 20), Pair(2 to 4, 30), Pair(2 to 5, 30),
        Pair(2 to 6, 40), Pair(2 to 7, 40), Pair(2 to 8, 60), Pair(2 to 9, 60),
        Pair(2 to 10, 70), Pair(2 to 11, 70), Pair(2 to 12, 80), Pair(2 to 13, 80),
        Pair(2 to 14, 100), Pair(2 to 15, 100), Pair(2 to 16, 100),

        // Pallabi (3)
        Pair(3 to 3, 0), Pair(3 to 4, 20), Pair(3 to 5, 20), Pair(3 to 6, 30),
        Pair(3 to 7, 30), Pair(3 to 8, 40), Pair(3 to 9, 50), Pair(3 to 10, 60),
        Pair(3 to 11, 60), Pair(3 to 12, 70), Pair(3 to 13, 70), Pair(3 to 14, 80),
        Pair(3 to 15, 80), Pair(3 to 16, 80),

        // Mirpur-11 (4)
        Pair(4 to 4, 0), Pair(4 to 5, 20), Pair(4 to 6, 20), Pair(4 to 7, 30),
        Pair(4 to 8, 40), Pair(4 to 9, 50), Pair(4 to 10, 60), Pair(4 to 11, 60),
        Pair(4 to 12, 70), Pair(4 to 13, 70), Pair(4 to 14, 80), Pair(4 to 15, 80),
        Pair(4 to 16, 80),

        // Mirpur-10 (5)
        Pair(5 to 5, 0), Pair(5 to 6, 20), Pair(5 to 7, 20), Pair(5 to 8, 30),
        Pair(5 to 9, 40), Pair(5 to 10, 50), Pair(5 to 11, 50), Pair(5 to 12, 60),
        Pair(5 to 13, 60), Pair(5 to 14, 70), Pair(5 to 15, 70), Pair(5 to 16, 70),

        // Kazipara (6)
        Pair(6 to 6, 0), Pair(6 to 7, 20), Pair(6 to 8, 30), Pair(6 to 9, 40),
        Pair(6 to 10, 50), Pair(6 to 11, 50), Pair(6 to 12, 60), Pair(6 to 13, 60),
        Pair(6 to 14, 70), Pair(6 to 15, 70), Pair(6 to 16, 70),

        // Shewrapara (7)
        Pair(7 to 7, 0), Pair(7 to 8, 20), Pair(7 to 9, 30), Pair(7 to 10, 40),
        Pair(7 to 11, 40), Pair(7 to 12, 50), Pair(7 to 13, 50), Pair(7 to 14, 60),
        Pair(7 to 15, 60), Pair(7 to 16, 60),

        // Agargaon (8)
        Pair(8 to 8, 0), Pair(8 to 9, 20), Pair(8 to 10, 30), Pair(8 to 11, 30),
        Pair(8 to 12, 40), Pair(8 to 13, 40), Pair(8 to 14, 50), Pair(8 to 15, 50),
        Pair(8 to 16, 50),

        // Bijoy Sarani (9)
        Pair(9 to 9, 0), Pair(9 to 10, 20), Pair(9 to 11, 20), Pair(9 to 12, 30),
        Pair(9 to 13, 30), Pair(9 to 14, 40), Pair(9 to 15, 40), Pair(9 to 16, 40),

        // Farmgate (10)
        Pair(10 to 10, 0), Pair(10 to 11, 20), Pair(10 to 12, 20), Pair(10 to 13, 20),
        Pair(10 to 14, 30), Pair(10 to 15, 30), Pair(10 to 16, 30),

        // Karwan Bazar (11)
        Pair(11 to 11, 0), Pair(11 to 12, 20), Pair(11 to 13, 20), Pair(11 to 14, 30),
        Pair(11 to 15, 30), Pair(11 to 16, 30),

        // Shahbagh (12)
        Pair(12 to 12, 0), Pair(12 to 13, 20), Pair(12 to 14, 20), Pair(12 to 15, 20),
        Pair(12 to 16, 20),

        // Dhaka University (13)
        Pair(13 to 13, 0), Pair(13 to 14, 20), Pair(13 to 15, 20), Pair(13 to 16, 20),

        // Secretariat (14)
        Pair(14 to 14, 0), Pair(14 to 15, 20), Pair(14 to 16, 20),

        // Motijheel (15)
        Pair(15 to 15, 0), Pair(15 to 16, 20),

        // Kamalapur (16)
        Pair(16 to 16, 0)
    )

    fun getAllStations(): List<Station> = stations

    fun calculateFare(from: Station, to: Station): Int {
        if (from.id == to.id) return 0
        // Try both directions since the matrix is symmetric
        return fareMatrix[Pair(from.id, to.id)]
            ?: fareMatrix[Pair(to.id, from.id)]
            ?: 0
    }

    fun getStation(name: String): Station? =
        stations.find { it.name == name }

    fun getStation(id: Int): Station? =
        stations.getOrNull(id)

    companion object {
        private var instance: FareCalculator? = null

        fun getInstance(): FareCalculator {
            if (instance == null) {
                instance = FareCalculator()
            }
            return instance!!
        }
    }
}
