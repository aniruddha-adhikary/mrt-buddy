package net.adhikary.mrtbuddy.nfc.service

import java.text.SimpleDateFormat
import java.util.GregorianCalendar

class TimestampService {
    companion object {
        val FORMAT = SimpleDateFormat("dd MMM yyyy | hh:00 - hh:59 a")
    }

    fun decodeTimestamp(value: Int): String {
        val hour = (value shr 3) and 0x1F
        val day = (value shr 8) and 0x1F
        val month = (value shr 13) and 0x0F
        val year = (value shr 17) and 0x1F

        val date = GregorianCalendar(year + 2000, month-1, day, hour, 0)
        return FORMAT.format(date.time)
    }
}