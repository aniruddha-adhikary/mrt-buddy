package net.adhikary.mrtbuddy.nfc.service

import net.adhikary.mrtbuddy.model.TransactionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StationServiceTest {
    @Test
    fun knownMetroStations() {
        assertEquals("Motijheel", StationService.getStationName(10))
        assertEquals("Uttara North", StationService.getStationName(90))
    }

    @Test
    fun knownHatirjheelStations() {
        assertEquals("Mohanagar (HJ)", StationService.getStationName(13))
        assertEquals("FDC (HJ)", StationService.getStationName(28))
    }

    @Test
    fun unknownCodeRendersWithCodeNumber() {
        assertEquals("Unknown (200)", StationService.getStationName(200))
    }

    @Test
    fun station0MapsToUnknown0() {
        assertEquals(StationService.UNKNOWN_STATION_ZERO, StationService.getStationName(0))
    }

    @Test
    fun isRechargeWhenBalanceUpdateTypeReturnsTrue() {
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.BalanceUpdate,
                toStation = "Motijheel",
                amount = null,
            ),
        )
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.BalanceUpdate,
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = -20,
            ),
        )
    }

    @Test
    fun isRechargeWhenPositiveAmountReturnsTrue() {
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.CommuteDhakaMetro,
                toStation = "Uttara North",
                amount = 500,
            ),
        )
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.CommuteUnknown,
                toStation = "Motijheel",
                amount = 100,
            ),
        )
    }

    @Test
    fun isRechargeWhenToStationIsUnknown0ReturnsTrue() {
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.CommuteDhakaMetro,
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = null,
            ),
        )
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.CommuteDhakaMetro,
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = 0,
            ),
        )
    }

    @Test
    fun isRechargeWhenCommuteHatirjheelBusReturnsFalse() {
        assertFalse(
            StationService.isRecharge(
                type = TransactionType.CommuteHatirjheelBusEnd,
                toStation = "FDC (HJ)",
                amount = 25,
            ),
        )
        assertFalse(
            StationService.isRecharge(
                type = TransactionType.CommuteHatirjheelBusStart,
                toStation = "Mohanagar (HJ)",
                amount = -40,
            ),
        )
    }

    @Test
    fun isRechargeWhenStandardCommuteReturnsFalse() {
        assertFalse(
            StationService.isRecharge(
                type = TransactionType.CommuteDhakaMetro,
                toStation = "Motijheel",
                amount = -20,
            ),
        )
        assertFalse(
            StationService.isRecharge(
                type = TransactionType.CommuteDhakaMetro,
                toStation = "Motijheel",
                amount = null,
            ),
        )
    }

    @Test
    fun formatTransactionTitleRechargeEnglish() {
        val title =
            StationService.formatTransactionTitle(
                type = TransactionType.CommuteDhakaMetro,
                fromStation = "Uttara North",
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = 500,
                rechargePrefix = "Recharge",
                translatedFrom = "Uttara North",
                translatedTo = "Unknown (0)",
            )
        assertEquals("Recharge • Uttara North", title)
    }

    @Test
    fun formatTransactionTitleRechargeBengali() {
        val title =
            StationService.formatTransactionTitle(
                type = TransactionType.CommuteDhakaMetro,
                fromStation = "Uttara North",
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = 500,
                rechargePrefix = "রিচার্জ",
                translatedFrom = "উত্তরা উত্তর",
                translatedTo = "Unknown (0)",
            )
        assertEquals("রিচার্জ • উত্তরা উত্তর", title)
    }

    @Test
    fun formatTransactionTitleRechargeUnassignedFromStation() {
        val titleEnglish =
            StationService.formatTransactionTitle(
                type = TransactionType.CommuteDhakaMetro,
                fromStation = StationService.UNKNOWN_STATION_ZERO,
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = 500,
                rechargePrefix = "Recharge",
                translatedFrom = "Unknown (0)",
                translatedTo = "Unknown (0)",
            )
        assertEquals("Recharge", titleEnglish)

        val titleBengali =
            StationService.formatTransactionTitle(
                type = TransactionType.CommuteDhakaMetro,
                fromStation = "",
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = 500,
                rechargePrefix = "রিচার্জ",
                translatedFrom = "",
                translatedTo = "Unknown (0)",
            )
        assertEquals("রিচার্জ", titleBengali)
    }

    @Test
    fun formatTransactionTitleStandardCommutePreservesRoute() {
        val titleEnglish =
            StationService.formatTransactionTitle(
                type = TransactionType.CommuteDhakaMetro,
                fromStation = "Uttara North",
                toStation = "Motijheel",
                amount = -100,
                rechargePrefix = "Recharge",
                translatedFrom = "Uttara North",
                translatedTo = "Motijheel",
            )
        assertEquals("Uttara North → Motijheel", titleEnglish)

        val titleBengali =
            StationService.formatTransactionTitle(
                type = TransactionType.CommuteDhakaMetro,
                fromStation = "Uttara North",
                toStation = "Motijheel",
                amount = -100,
                rechargePrefix = "রিচার্জ",
                translatedFrom = "উত্তরা উত্তর",
                translatedTo = "মতিঝিল",
            )
        assertEquals("উত্তরা উত্তর → মতিঝিল", titleBengali)
    }
}
