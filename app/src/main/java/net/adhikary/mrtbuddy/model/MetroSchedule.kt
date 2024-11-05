package net.adhikary.mrtbuddy.model

import java.time.LocalTime

data class MetroSchedule(
    val stationName: String,
    val weekdaySchedules: List<TimeSlot>,
    val weekendSchedules: List<TimeSlot>,
    val direction: Direction,
    val frequency: Frequency
)

data class TimeSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val frequency: Int, // in minutes
    val isPeakHour: Boolean
)

data class Frequency(
    val peakHourFrequency: Int, // in minutes
    val offPeakFrequency: Int,  // in minutes
    val peakHours: List<PeakHourRange>
)

data class PeakHourRange(
    val startTime: LocalTime,
    val endTime: LocalTime
)

// Direction enum moved to Direction.kt

data class MetroStation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val platformNumber: Int = 1,
    val hasElevator: Boolean = false,
    val hasWheelchairAccess: Boolean = false,
    val isInterchange: Boolean = false,
    val interchangeLines: List<String> = emptyList(),
    val estimatedTravelTimeToNext: Int = 5 // in minutes
)

// List of all metro stations with their coordinates
object MetroStations {
    val stations = listOf(
        MetroStation("Uttara North", 23.8759, 90.3995, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 4),
        MetroStation("Uttara Center", 23.8687, 90.3989, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 3),
        MetroStation("Uttara South", 23.8615, 90.3983, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 5),
        MetroStation("Pallabi", 23.8543, 90.3977, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 4),
        MetroStation("Mirpur-11", 23.8471, 90.3971, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 3),
        MetroStation("Mirpur-10", 23.8399, 90.3965, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, isInterchange = true, interchangeLines = listOf("Bus Route"), estimatedTravelTimeToNext = 4),
        MetroStation("Kazipara", 23.8327, 90.3959, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 3),
        MetroStation("Shewrapara", 23.8255, 90.3953, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 5),
        MetroStation("Agargaon", 23.8183, 90.3947, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, isInterchange = true, interchangeLines = listOf("Bus Route"), estimatedTravelTimeToNext = 4),
        MetroStation("Bijoy Sarani", 23.8111, 90.3941, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 3),
        MetroStation("Farmgate", 23.8039, 90.3935, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, isInterchange = true, interchangeLines = listOf("Bus Route"), estimatedTravelTimeToNext = 4),
        MetroStation("Karwan Bazar", 23.7967, 90.3929, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 3),
        MetroStation("Shahbagh", 23.7895, 90.3923, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 4),
        MetroStation("Dhaka University", 23.7823, 90.3917, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 4),
        MetroStation("Bangladesh Secretariat", 23.7751, 90.3911, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, estimatedTravelTimeToNext = 5),
        MetroStation("Motijheel", 23.7679, 90.3905, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, isInterchange = true, interchangeLines = listOf("Bus Route"), estimatedTravelTimeToNext = 6),
        MetroStation("Kamalapur", 23.7331, 90.4264, platformNumber = 2, hasElevator = true, hasWheelchairAccess = true, isInterchange = true, interchangeLines = listOf("Railway Station"))
    )

    // Default peak hours for the metro system
    val defaultPeakHours = listOf(
        PeakHourRange(LocalTime.of(8, 0), LocalTime.of(10, 0)),  // Morning peak
        PeakHourRange(LocalTime.of(17, 0), LocalTime.of(19, 0))  // Evening peak
    )

    // Default frequencies
    val defaultFrequency = Frequency(
        peakHourFrequency = 10,    // Every 10 minutes during peak hours
        offPeakFrequency = 15,     // Every 15 minutes during off-peak hours
        peakHours = defaultPeakHours
    )
}
