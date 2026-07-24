package net.adhikary.mrtbuddy.nfc.service

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TimestampServiceTest {
    private fun encodeTimestamp(
        yearOffset: Int,
        month: Int,
        day: Int,
        hour: Int,
    ): Int = (yearOffset shl 17) or (month shl 13) or (day shl 8) or (hour shl 3)

    @Test
    fun decodesAllBitFieldsWithExplicitBaseYear() {
        val value = encodeTimestamp(yearOffset = 24, month = 7, day = 15, hour = 14)

        val result = TimestampService.decodeTimestamp(value, baseYear = 2000)

        assertEquals(LocalDateTime(2024, 7, 15, 14, 0), result)
    }

    @Test
    fun explicitBaseYearMakesDecodingIndependentOfClock() {
        val value = encodeTimestamp(yearOffset = 5, month = 1, day = 2, hour = 0)

        val result = TimestampService.decodeTimestamp(value, baseYear = 2100)

        assertEquals(2105, result.year)
    }

    @Test
    fun invalidMonthClampsToJanuary() {
        val value = encodeTimestamp(yearOffset = 24, month = 13, day = 10, hour = 9)

        val result = TimestampService.decodeTimestamp(value, baseYear = 2000)

        assertEquals(1, result.monthNumber)
        assertEquals(10, result.dayOfMonth)
    }

    @Test
    fun invalidDayClampsToFirst() {
        val value = encodeTimestamp(yearOffset = 24, month = 6, day = 0, hour = 9)

        val result = TimestampService.decodeTimestamp(value, baseYear = 2000)

        assertEquals(6, result.monthNumber)
        assertEquals(1, result.dayOfMonth)
    }

    @Test
    fun hourWrapsModulo24() {
        val value = encodeTimestamp(yearOffset = 24, month = 6, day = 10, hour = 31)

        val result = TimestampService.decodeTimestamp(value, baseYear = 2000)

        assertEquals(7, result.hour)
    }

    @Test
    fun defaultBaseYearMatchesCurrentCentury() {
        val value = encodeTimestamp(yearOffset = 24, month = 7, day = 15, hour = 14)

        val defaulted = TimestampService.decodeTimestamp(value)
        val explicit = TimestampService.decodeTimestamp(value, baseYear = TimestampService.currentBaseYear())

        assertEquals(explicit, defaulted)
    }
}
