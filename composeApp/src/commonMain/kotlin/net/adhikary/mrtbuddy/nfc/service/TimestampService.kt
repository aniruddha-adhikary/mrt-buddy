package net.adhikary.mrtbuddy.nfc.service

import kotlinx.datetime.LocalDateTime

class TimestampService {
    companion object {
        private val monthNames = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
    }

    fun decodeTimestamp(value: Int): String {
        val hour = (value shr 3) and 0x1F
        val day = (value shr 8) and 0x1F
        val month = (value shr 13) and 0x0F
        val year = (value shr 17) and 0x1F

        // Calculate the actual year
        val fullYear = 2000 + year

        // Validate month and day
        val validMonth = if (month in 1..12) month else 1
        val validDay = if (day in 1..31) day else 1

        // Create a LocalDateTime instance
        val dateTime = LocalDateTime(
            year = fullYear,
            monthNumber = validMonth,
            dayOfMonth = validDay,
            hour = hour % 24,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        // Format the date and time
        return formatDateTime(dateTime)
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val month = monthNames.getOrElse(dateTime.monthNumber - 1) { "Unknown" }
        val year = dateTime.year

        // Convert to 12-hour format
        val hour12 = when {
            dateTime.hour == 0 -> 12
            dateTime.hour > 12 -> dateTime.hour - 12
            else -> dateTime.hour
        }.toString().padStart(2, '0')

        val amPm = if (dateTime.hour < 12) "AM" else "PM"

        return "$day $month $year | $hour12:00 - $hour12:59 $amPm"
    }
}
