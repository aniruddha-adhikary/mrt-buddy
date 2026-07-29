package net.adhikary.mrtbuddy.nfc.demo

import net.adhikary.mrtbuddy.nfc.CardTransceiver
import net.adhikary.mrtbuddy.nfc.FelicaReadResult

/**
 * In-app [CardTransceiver] backed by [DemoCards], letting [net.adhikary.mrtbuddy.nfc.FelicaReader]
 * run its real orchestration over fixture bytes with no hardware. The first window carries the
 * demo blocks; the second window is empty, matching a short card.
 */
class DemoCardTransceiver : CardTransceiver {
    override val idm: ByteArray = DemoCards.IDM

    override suspend fun readBlocks(
        serviceCode: Int,
        startBlock: Int,
        count: Int,
    ): FelicaReadResult =
        if (startBlock == 0) {
            FelicaReadResult(statusFlag1 = 0, statusFlag2 = 0, blocks = DemoCards.blocks)
        } else {
            FelicaReadResult(statusFlag1 = 0, statusFlag2 = 0, blocks = emptyList())
        }
}
