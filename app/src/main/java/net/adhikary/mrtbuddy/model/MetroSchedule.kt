package net.adhikary.mrtbuddy.model

data class MetroSchedule(
    val stationName: String,
    val weekdaySchedule: List<String>,
    val weekendSchedule: List<String>
)

data class MetroStation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

// List of all metro stations with their coordinates
object MetroStations {
    val stations = listOf(
        MetroStation("Uttara North", 23.8759, 90.3995),
        MetroStation("Uttara Center", 23.8687, 90.3989),
        MetroStation("Uttara South", 23.8615, 90.3983),
        MetroStation("Pallabi", 23.8543, 90.3977),
        MetroStation("Mirpur-11", 23.8471, 90.3971),
        MetroStation("Mirpur-10", 23.8399, 90.3965),
        MetroStation("Kazipara", 23.8327, 90.3959),
        MetroStation("Shewrapara", 23.8255, 90.3953),
        MetroStation("Agargaon", 23.8183, 90.3947),
        MetroStation("Bijoy Sarani", 23.8111, 90.3941),
        MetroStation("Farmgate", 23.8039, 90.3935),
        MetroStation("Karwan Bazar", 23.7967, 90.3929),
        MetroStation("Shahbagh", 23.7895, 90.3923),
        MetroStation("Dhaka University", 23.7823, 90.3917),
        MetroStation("Secretariat", 23.7751, 90.3911),
        MetroStation("Motijheel", 23.7679, 90.3905),
        MetroStation("Kamalapur", 23.7331, 90.4264)
    )
}
