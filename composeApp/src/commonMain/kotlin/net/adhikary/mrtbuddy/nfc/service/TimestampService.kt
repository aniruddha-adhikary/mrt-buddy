package net.adhikary.mrtbuddy.nfc.service

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.LocalDateTime
import mrtbuddy.composeapp.generated.resources.*
import net.adhikary.mrtbuddy.translateNumber
import org.jetbrains.compose.resources.stringResource

class TimestampService {
    companion object {
        @Composable
        fun formatDateTime(dateTime: LocalDateTime): String {
            val zero = translateNumber(0)[0]
            val day = translateNumber(dateTime.dayOfMonth).padStart(2, zero)
            val month = getMonth(dateTime.monthNumber)
            val year = translateNumber(dateTime.year)

            val minutes = if (dateTime.minute == 0) ":$zero$zero" else ":${translateNumber(dateTime.minute).padStart(2, zero)}"
            return "$day $month $year, ${getHour(dateTime.hour)}$minutes ${getAmPm(dateTime.hour)}"
        }

        fun getAmPm(hour: Int): String {
            if (Locale.current.language == "bn") {
                return if (hour < 12) "এএম" else "পিএম"
            }
            return if (hour < 12) "AM" else "PM"
        }

        fun getHour(hour: Int): String {
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return translateNumber(hour12).padStart(2, translateNumber(0)[0])
        }

        @Composable
        fun getMonth(month: Int): String {
            return when(month) {
                1 -> stringResource(Res.string.jan)
                2 -> stringResource(Res.string.feb)
                3 -> stringResource(Res.string.mar)
                4 -> stringResource(Res.string.apr)
                5 -> stringResource(Res.string.may)
                6 -> stringResource(Res.string.jun)
                7 -> stringResource(Res.string.jul)
                8 -> stringResource(Res.string.aug)
                9 -> stringResource(Res.string.sep)
                10 -> stringResource(Res.string.oct)
                11 -> stringResource(Res.string.nov)
                12 -> stringResource(Res.string.dec)
                else -> "Unknown"
            }
        }
    }


    fun decodeTimestamp(value: Int): LocalDateTime {
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
        return LocalDateTime(
            year = fullYear,
            monthNumber = validMonth,
            dayOfMonth = validDay,
            hour = hour % 24,
            minute = 0,
            second = 0,
            nanosecond = 0
        )
    }
}
