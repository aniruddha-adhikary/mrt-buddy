package net.adhikary.mrtbuddy.nfc.demo

/**
 * Synthetic demo card data used by the debug-only "Scan demo card" action. The blocks are
 * built to parse through the production [net.adhikary.mrtbuddy.nfc.FelicaReader] pipeline with
 * a fixed [BASE_YEAR], so the demo scan exercises the identical read-parse-persist path a real
 * card takes. The [IDM] is a fixed, recognizable identity so repeated demo scans update one
 * deletable "demo" card.
 */
object DemoCards {
    /** Fixed base year the demo scan decodes timestamps against, keeping demo data stable. */
    const val BASE_YEAR = 2000

    /** Recognizable demo IDm — stable across scans so it maps to a single identifiable card. */
    val IDM: ByteArray =
        byteArrayOf(
            0xD3.toByte(),
            0xA0.toByte(),
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x01,
        )

    // Known fixed headers (see TransactionType.fromHeader).
    private val METRO = byteArrayOf(0x08, 0x52, 0x10, 0x00)
    private val BUS_START = byteArrayOf(0x08, 0xD2.toByte(), 0x20, 0x00)
    private val BUS_END = byteArrayOf(0x42, 0xD6.toByte(), 0x30, 0x00)
    private val BALANCE_UPDATE = byteArrayOf(0x1D, 0x60, 0x02, 0x01)

    /**
     * Newest-first plausible 2025–2026 Dhaka commute history. Chronologically a top-up to
     * 1000 BDT followed by metro and Hatirjheel-bus fares spending the balance down to 340.
     */
    val blocks: List<ByteArray> =
        listOf(
            block(METRO, yearOffset = 26, month = 1, day = 5, hour = 9, from = 90, to = 10, balance = 340),
            block(METRO, yearOffset = 26, month = 1, day = 4, hour = 18, from = 10, to = 90, balance = 440),
            block(BUS_END, yearOffset = 25, month = 12, day = 20, hour = 19, from = 16, to = 28, balance = 540),
            block(BUS_START, yearOffset = 25, month = 12, day = 20, hour = 18, from = 13, to = 16, balance = 560),
            block(METRO, yearOffset = 25, month = 12, day = 15, hour = 9, from = 50, to = 10, balance = 620),
            block(METRO, yearOffset = 25, month = 11, day = 30, hour = 18, from = 40, to = 50, balance = 720),
            block(METRO, yearOffset = 25, month = 11, day = 30, hour = 9, from = 65, to = 40, balance = 820),
            block(BALANCE_UPDATE, yearOffset = 25, month = 11, day = 1, hour = 12, from = 0, to = 0, balance = 1000),
        )

    @Suppress("LongParameterList")
    private fun block(
        header: ByteArray,
        yearOffset: Int,
        month: Int,
        day: Int,
        hour: Int,
        from: Int,
        to: Int,
        balance: Int,
    ): ByteArray {
        val timestamp = (yearOffset shl 17) or (month shl 13) or (day shl 8) or (hour shl 3)
        val out = ByteArray(BLOCK_SIZE)
        header.copyInto(out, 0)
        // Timestamp int24 big-endian at bytes 4–6 (byte 6 doubles as a transaction-type byte).
        out[4] = (timestamp shr 16).toByte()
        out[5] = (timestamp shr 8).toByte()
        out[6] = timestamp.toByte()
        out[8] = from.toByte()
        out[10] = to.toByte()
        // Balance int24 little-endian at bytes 11–13.
        out[11] = balance.toByte()
        out[12] = (balance shr 8).toByte()
        out[13] = (balance shr 16).toByte()
        return out
    }

    private const val BLOCK_SIZE = 16
}
