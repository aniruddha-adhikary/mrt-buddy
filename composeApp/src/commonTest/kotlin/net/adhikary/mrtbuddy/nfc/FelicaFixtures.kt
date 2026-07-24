package net.adhikary.mrtbuddy.nfc

/**
 * Synthetic FeliCa fixtures for hardware-free testing.
 *
 * Builds 16-byte transaction blocks and full response frames from the documented
 * layout (see the `felica` skill). Extend this rather than hand-rolling byte arrays.
 */
object FelicaFixtures {
    const val BASE_YEAR = 2000

    // Known fixed headers (see TransactionType.fromHeader).
    val METRO_HEADER = byteArrayOf(0x08, 0x52, 0x10, 0x00)
    val BUS_START_HEADER = byteArrayOf(0x08, 0xD2.toByte(), 0x20, 0x00)
    val BUS_END_HEADER = byteArrayOf(0x42, 0xD6.toByte(), 0x30, 0x00)
    val BALANCE_UPDATE_MRT_HEADER = byteArrayOf(0x1D, 0x60, 0x02, 0x01)
    val BALANCE_UPDATE_RAPID_HEADER = byteArrayOf(0x42, 0x60, 0x02, 0x00)
    val UNKNOWN_HEADER = byteArrayOf(0x00, 0x00, 0x00, 0x00)

    fun encodeTimestamp(
        yearOffset: Int,
        month: Int,
        day: Int,
        hour: Int,
    ): Int = (yearOffset shl 17) or (month shl 13) or (day shl 8) or (hour shl 3)

    /**
     * Builds a 16-byte transaction block. `timestampValue` is written big-endian at
     * bytes 4–6; note byte 6 is shared with the transaction-type bytes (6–7) exactly
     * as the production parser reads them.
     */
    @Suppress("LongParameterList")
    fun block(
        header: ByteArray,
        timestampValue: Int,
        fromStation: Int,
        toStation: Int,
        balance: Int,
        byte7: Int = 0x00,
        byte9: Int = 0x00,
        trailing: ByteArray = byteArrayOf(0x00, 0x00),
    ): ByteArray {
        val out = ByteArray(BLOCK_SIZE)
        header.copyInto(out, 0)
        out[4] = (timestampValue shr 16).toByte()
        out[5] = (timestampValue shr 8).toByte()
        out[6] = timestampValue.toByte()
        out[7] = byte7.toByte()
        out[8] = fromStation.toByte()
        out[9] = byte9.toByte()
        out[10] = toStation.toByte()
        out[11] = balance.toByte()
        out[12] = (balance shr 8).toByte()
        out[13] = (balance shr 16).toByte()
        trailing.copyInto(out, 14)
        return out
    }

    /**
     * Builds a full Android-style response frame: 10-byte header (LEN, code, IDm),
     * status flags, block count, then the concatenated 16-byte blocks.
     */
    fun frame(
        blocks: List<ByteArray>,
        statusFlag1: Int = 0x00,
        statusFlag2: Int = 0x00,
        idm: ByteArray = ByteArray(IDM_SIZE),
    ): ByteArray {
        val total = FRAME_HEADER_SIZE + blocks.size * BLOCK_SIZE
        val out = ByteArray(total)
        out[0] = total.toByte()
        out[1] = 0x07
        idm.copyInto(out, 2)
        out[10] = statusFlag1.toByte()
        out[11] = statusFlag2.toByte()
        out[12] = blocks.size.toByte()
        var offset = FRAME_HEADER_SIZE
        blocks.forEach { block ->
            block.copyInto(out, offset)
            offset += BLOCK_SIZE
        }
        return out
    }

    // A recent, valid timestamp (2024-07-15 14:00 with BASE_YEAR).
    private val validTimestamp = encodeTimestamp(yearOffset = 24, month = 7, day = 15, hour = 14)

    // A pre-2020 timestamp (2015-03-10 09:00 with BASE_YEAR) — filtered as invalid.
    private val pre2020Timestamp = encodeTimestamp(yearOffset = 15, month = 3, day = 10, hour = 9)

    val metroBlock: ByteArray =
        block(METRO_HEADER, validTimestamp, fromStation = 90, toStation = 10, balance = 5000)

    val busStartBlock: ByteArray =
        block(BUS_START_HEADER, validTimestamp, fromStation = 13, toStation = 16, balance = 4200)

    val busEndBlock: ByteArray =
        block(BUS_END_HEADER, validTimestamp, fromStation = 16, toStation = 28, balance = 4100)

    val balanceUpdateMrtBlock: ByteArray =
        block(BALANCE_UPDATE_MRT_HEADER, validTimestamp, fromStation = 0, toStation = 0, balance = 10000)

    val balanceUpdateRapidBlock: ByteArray =
        block(BALANCE_UPDATE_RAPID_HEADER, validTimestamp, fromStation = 0, toStation = 0, balance = 9000)

    val unknownHeaderBlock: ByteArray =
        block(UNKNOWN_HEADER, validTimestamp, fromStation = 90, toStation = 10, balance = 3000)

    val unknownStationBlock: ByteArray =
        block(METRO_HEADER, validTimestamp, fromStation = 200, toStation = 201, balance = 2500)

    val pre2020Block: ByteArray =
        block(METRO_HEADER, pre2020Timestamp, fromStation = 90, toStation = 10, balance = 1500)

    private const val BLOCK_SIZE = 16
    private const val IDM_SIZE = 8
    private const val FRAME_HEADER_SIZE = 13
}
