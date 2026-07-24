package net.adhikary.mrtbuddy.model

import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionTypeTest {
    @Test
    fun metroHeaderMapsToDhakaMetro() {
        assertEquals(TransactionType.CommuteDhakaMetro, TransactionType.fromHeader("08 52 10 00"))
    }

    @Test
    fun busStartHeaderMapsToHatirjheelBusStart() {
        assertEquals(TransactionType.CommuteHatirjheelBusStart, TransactionType.fromHeader("08 D2 20 00"))
    }

    @Test
    fun busEndHeaderMapsToHatirjheelBusEnd() {
        assertEquals(TransactionType.CommuteHatirjheelBusEnd, TransactionType.fromHeader("42 D6 30 00"))
    }

    @Test
    fun mrtBalanceUpdateHeaderMapsToBalanceUpdate() {
        assertEquals(TransactionType.BalanceUpdate, TransactionType.fromHeader("1D 60 02 01"))
    }

    @Test
    fun rapidBalanceUpdateHeaderMapsToBalanceUpdate() {
        assertEquals(TransactionType.BalanceUpdate, TransactionType.fromHeader("42 60 02 00"))
    }

    @Test
    fun unknownHeaderMapsToCommuteUnknown() {
        assertEquals(TransactionType.CommuteUnknown, TransactionType.fromHeader("00 00 00 00"))
    }
}
