package net.adhikary.mrtbuddy.utils

import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.dayAgo
import mrtbuddy.composeapp.generated.resources.daysAgo
import mrtbuddy.composeapp.generated.resources.hourAgo
import mrtbuddy.composeapp.generated.resources.hoursAgo
import mrtbuddy.composeapp.generated.resources.justNow
import mrtbuddy.composeapp.generated.resources.minuteAgo
import mrtbuddy.composeapp.generated.resources.minutesAgo
import mrtbuddy.composeapp.generated.resources.monthAgo
import mrtbuddy.composeapp.generated.resources.monthsAgo
import mrtbuddy.composeapp.generated.resources.yearAgo
import mrtbuddy.composeapp.generated.resources.yearsAgo
import org.jetbrains.compose.resources.stringResource

object TimeUtils {
    @Composable
    fun getTimeAgoString(timestamp: Long): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val diff = now - timestamp
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = days / 365

        return when {
            years > 0 -> "$years ${stringResource(if (years == 1L) Res.string.yearAgo else Res.string.yearsAgo)}"
            months > 0 -> "$months ${stringResource(if (months == 1L) Res.string.monthAgo else Res.string.monthsAgo)}"
            days > 0 -> "$days ${stringResource(if (days == 1L) Res.string.dayAgo else Res.string.daysAgo)}"
            hours > 0 -> "$hours ${stringResource(if (hours == 1L) Res.string.hourAgo else Res.string.hoursAgo)}"
            minutes > 0 -> "$minutes ${stringResource(if (minutes == 1L) Res.string.minuteAgo else Res.string.minutesAgo)}"
            else -> stringResource(Res.string.justNow)
        }
    }
}
