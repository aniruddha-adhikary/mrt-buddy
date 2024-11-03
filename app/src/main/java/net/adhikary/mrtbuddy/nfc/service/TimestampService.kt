package net.adhikary.mrtbuddy.nfc.service

import java.text.SimpleDateFormat
import java.util.Date

class TimestampService {
    fun decodeTimestamp(value: Int): String {
        val baseTime = System.currentTimeMillis() - (value * 60 * 1000L)
        val date = Date(baseTime)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.format(date)
    }
}