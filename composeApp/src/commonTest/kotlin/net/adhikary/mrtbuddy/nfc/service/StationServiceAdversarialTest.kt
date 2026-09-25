package net.adhikary.mrtbuddy.nfc.service

import net.adhikary.mrtbuddy.model.TransactionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StationServiceAdversarialTest {
    private data class RechargeTestCase(
        val type: TransactionType,
        val fromStation: String,
        val toStation: String,
        val amount: Int?,
    )

    @Test
    fun boundaryAmountsWithBalanceUpdate() {
        val boundaryAmounts = listOf(null, 0, -1, -20, Int.MIN_VALUE, 1, 500, Int.MAX_VALUE)
        for (amt in boundaryAmounts) {
            assertTrue(
                StationService.isRecharge(
                    type = TransactionType.BalanceUpdate,
                    toStation = "Unknown (0)",
                    amount = amt,
                ),
            )
            val title =
                StationService.formatTransactionTitle(
                    type = TransactionType.BalanceUpdate,
                    fromStation = "Uttara North",
                    toStation = "Unknown (0)",
                    amount = amt,
                    rechargePrefix = "Recharge",
                    translatedFrom = "Uttara North",
                    translatedTo = "Unknown (0)",
                )
            assertEquals("Recharge • Uttara North", title)
            assertFalse(title.contains("Unknown (0)"))
            assertFalse(title.contains("→"))
        }
    }

    @Test
    fun boundaryAmountsWithStandardCommutePreservesRoute() {
        val commuteAmounts = listOf(null, 0, -1, -20, -100, Int.MIN_VALUE)
        for (amt in commuteAmounts) {
            assertFalse(
                StationService.isRecharge(
                    type = TransactionType.CommuteDhakaMetro,
                    toStation = "Motijheel",
                    amount = amt,
                ),
            )
            val titleEn =
                StationService.formatTransactionTitle(
                    type = TransactionType.CommuteDhakaMetro,
                    fromStation = "Uttara North",
                    toStation = "Motijheel",
                    amount = amt,
                    rechargePrefix = "Recharge",
                    translatedFrom = "Uttara North",
                    translatedTo = "Motijheel",
                )
            assertEquals("Uttara North → Motijheel", titleEn)

            val titleBn =
                StationService.formatTransactionTitle(
                    type = TransactionType.CommuteDhakaMetro,
                    fromStation = "Uttara North",
                    toStation = "Motijheel",
                    amount = amt,
                    rechargePrefix = "রিচার্জ",
                    translatedFrom = "উত্তরা উত্তর",
                    translatedTo = "মতিঝিল",
                )
            assertEquals("উত্তরা উত্তর → মতিঝিল", titleBn)
        }
    }

    @Test
    fun boundaryAmountsWithMetroRechargeHeader() {
        val rechargeAmounts = listOf(null, 0, 1, 500, 1000, Int.MAX_VALUE)
        for (amt in rechargeAmounts) {
            assertTrue(
                StationService.isRecharge(
                    type = TransactionType.CommuteDhakaMetro,
                    toStation = StationService.UNKNOWN_STATION_ZERO,
                    amount = amt,
                ),
            )
            val title =
                StationService.formatTransactionTitle(
                    type = TransactionType.CommuteDhakaMetro,
                    fromStation = "Mirpur 10",
                    toStation = StationService.UNKNOWN_STATION_ZERO,
                    amount = amt,
                    rechargePrefix = "Recharge",
                    translatedFrom = "Mirpur 10",
                    translatedTo = StationService.UNKNOWN_STATION_ZERO,
                )
            assertEquals("Recharge • Mirpur 10", title)
            assertFalse(title.contains(StationService.UNKNOWN_STATION_ZERO))
            assertFalse(title.contains("→"))
        }
    }

    @Test
    fun unusualStationCombinationsForRecharge() {
        val testCases =
            listOf(
                Pair("Unknown (0)", "Unknown (0)"),
                Pair("", "Unknown (0)"),
                Pair("   ", "Unknown (0)"),
                Pair("", ""),
                Pair("   ", ""),
                Pair("Unknown (0)", ""),
            )

        for ((fromStn, toStn) in testCases) {
            val titleEn =
                StationService.formatTransactionTitle(
                    type = TransactionType.BalanceUpdate,
                    fromStation = fromStn,
                    toStation = toStn,
                    amount = 500,
                    rechargePrefix = "Recharge",
                    translatedFrom = fromStn,
                    translatedTo = toStn,
                )
            assertEquals("Recharge", titleEn)
            assertFalse(titleEn.contains("Unknown (0)"))
            assertFalse(titleEn.contains("•"))
            assertFalse(titleEn.contains("→"))

            val titleBn =
                StationService.formatTransactionTitle(
                    type = TransactionType.BalanceUpdate,
                    fromStation = fromStn,
                    toStation = toStn,
                    amount = 500,
                    rechargePrefix = "রিচার্জ",
                    translatedFrom = fromStn,
                    translatedTo = toStn,
                )
            assertEquals("রিচার্জ", titleBn)
            assertFalse(titleBn.contains("Unknown (0)"))
            assertFalse(titleBn.contains("•"))
            assertFalse(titleBn.contains("→"))
        }
    }

    @Test
    fun unusualStationCombinationsForCommute() {
        val commuteCases =
            listOf(
                Pair("", ""),
                Pair("   ", "   "),
                Pair("Uttara North", ""),
            )

        for ((fromStn, toStn) in commuteCases) {
            assertFalse(
                StationService.isRecharge(
                    type = TransactionType.CommuteDhakaMetro,
                    toStation = toStn,
                    amount = -20,
                ),
            )
            val title =
                StationService.formatTransactionTitle(
                    type = TransactionType.CommuteDhakaMetro,
                    fromStation = fromStn,
                    toStation = toStn,
                    amount = -20,
                    rechargePrefix = "Recharge",
                    translatedFrom = fromStn,
                    translatedTo = toStn,
                )
            assertEquals("$fromStn → $toStn", title)
        }
    }

    @Test
    fun busJourneysVsMetroJourneysVsRecharges() {
        // Hatirjheel bus exit with positive refund amount must not be detected as recharge
        assertFalse(
            StationService.isRecharge(
                type = TransactionType.CommuteHatirjheelBusEnd,
                toStation = "FDC (HJ)",
                amount = 25,
            ),
        )
        val busEndTitle =
            StationService.formatTransactionTitle(
                type = TransactionType.CommuteHatirjheelBusEnd,
                fromStation = "Mohanagar (HJ)",
                toStation = "FDC (HJ)",
                amount = 25,
                rechargePrefix = "Recharge",
                translatedFrom = "Mohanagar (HJ)",
                translatedTo = "FDC (HJ)",
            )
        assertEquals("Mohanagar (HJ) → FDC (HJ)", busEndTitle)

        // Hatirjheel bus entry tap
        assertFalse(
            StationService.isRecharge(
                type = TransactionType.CommuteHatirjheelBusStart,
                toStation = "Rampura (HJ)",
                amount = -40,
            ),
        )

        // Standard Metro commute
        assertFalse(
            StationService.isRecharge(
                type = TransactionType.CommuteDhakaMetro,
                toStation = "Motijheel",
                amount = -100,
            ),
        )

        // Metro recharge with BalanceUpdate type
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.BalanceUpdate,
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = 500,
            ),
        )

        // Metro recharge with CommuteDhakaMetro header
        assertTrue(
            StationService.isRecharge(
                type = TransactionType.CommuteDhakaMetro,
                toStation = StationService.UNKNOWN_STATION_ZERO,
                amount = 200,
            ),
        )
    }

    @Test
    fun noLeakOfUnknown0OrArrowInAnyRechargeCombination() {
        val testFromStations = listOf("Uttara North", "Motijheel", "Unknown (0)", "", "   ")
        val testToStations = listOf("Unknown (0)", "", "Motijheel")
        val testAmounts = listOf(null, 0, 50, 500, Int.MAX_VALUE)
        val testTypes = listOf(TransactionType.BalanceUpdate, TransactionType.CommuteDhakaMetro)

        val combinations =
            testFromStations.flatMap { from ->
                testToStations.flatMap { to ->
                    testAmounts.flatMap { amt ->
                        testTypes.map { type -> RechargeTestCase(type, from, to, amt) }
                    }
                }
            }

        combinations.filter { StationService.isRecharge(it.type, it.toStation, it.amount) }
            .forEach { tc ->
                val title =
                    StationService.formatTransactionTitle(
                        type = tc.type,
                        fromStation = tc.fromStation,
                        toStation = tc.toStation,
                        amount = tc.amount,
                        rechargePrefix = "Recharge",
                        translatedFrom = tc.fromStation,
                        translatedTo = tc.toStation,
                    )
                assertFalse(title.contains(StationService.UNKNOWN_STATION_ZERO), "Leaked Unknown (0): $title")
                assertFalse(title.contains("→"), "Leaked arrow: $title")
                assertFalse(title.contains("Unknown"), "Leaked Unknown: $title")
            }
    }
}
