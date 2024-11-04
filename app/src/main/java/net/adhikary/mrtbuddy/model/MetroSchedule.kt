package net.adhikary.mrtbuddy.model

import java.time.DayOfWeek
import java.time.LocalTime

data class TimeSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val frequency: Int // in minutes
)

data class DailySchedule(
    val firstTrain: LocalTime,
    val lastTrain: LocalTime,
    val timeSlots: List<TimeSlot>,
    val isRapidPassOnly: (LocalTime) -> Boolean
)

object MetroSchedule {
    private val weekdaySchedule = DailySchedule(
        firstTrain = LocalTime.of(7, 10),
        lastTrain = LocalTime.of(21, 0),
        timeSlots = listOf(
            TimeSlot(LocalTime.of(7, 10), LocalTime.of(7, 30), 10),
            TimeSlot(LocalTime.of(7, 31), LocalTime.of(11, 36), 8),
            TimeSlot(LocalTime.of(11, 37), LocalTime.of(14, 36), 10),
            TimeSlot(LocalTime.of(14, 37), LocalTime.of(20, 36), 8),
            TimeSlot(LocalTime.of(20, 37), LocalTime.of(21, 0), 10)
        ),
        isRapidPassOnly = { time ->
            time <= LocalTime.of(7, 20) || time >= LocalTime.of(21, 13) ||
            (time == LocalTime.of(7, 10) || time == LocalTime.of(7, 20))
        }
    )

    private val saturdaySchedule = DailySchedule(
        firstTrain = LocalTime.of(7, 10),
        lastTrain = LocalTime.of(21, 0),
        timeSlots = listOf(
            TimeSlot(LocalTime.of(7, 10), LocalTime.of(10, 32), 12),
            TimeSlot(LocalTime.of(10, 33), LocalTime.of(21, 0), 10)
        ),
        isRapidPassOnly = { time ->
            time <= LocalTime.of(7, 20) || time >= LocalTime.of(21, 13) ||
            (time == LocalTime.of(7, 10) || time == LocalTime.of(7, 20))
        }
    )

    private val fridaySchedule = DailySchedule(
        firstTrain = LocalTime.of(15, 30),
        lastTrain = LocalTime.of(21, 0),
        timeSlots = listOf(
            TimeSlot(LocalTime.of(15, 30), LocalTime.of(21, 0), 10)
        ),
        isRapidPassOnly = { time ->
            time >= LocalTime.of(21, 13)
        }
    )

    fun getScheduleForDay(dayOfWeek: DayOfWeek): DailySchedule {
        return when (dayOfWeek) {
            DayOfWeek.FRIDAY -> fridaySchedule
            DayOfWeek.SATURDAY -> saturdaySchedule
            else -> weekdaySchedule
        }
    }

    fun isTicketSalesAvailable(dayOfWeek: DayOfWeek, time: LocalTime): Boolean {
        val startTime = if (dayOfWeek == DayOfWeek.FRIDAY) {
            LocalTime.of(15, 30)
        } else {
            LocalTime.of(7, 20)
        }
        val endTime = LocalTime.of(20, 50)

        return time in startTime..endTime
    }
}
