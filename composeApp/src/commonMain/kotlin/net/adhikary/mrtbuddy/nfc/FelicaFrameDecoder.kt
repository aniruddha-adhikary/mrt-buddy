package net.adhikary.mrtbuddy.nfc

/**
 * Pure decoder for an Android-style FeliCa response frame into a [FelicaReadResult].
 *
 * Frame layout: 10-byte header (LEN, response code, 8-byte IDm), status flag 1 at
 * byte 10, status flag 2 at byte 11, block count at byte 12, then 16-byte blocks.
 * A frame shorter than the header, or one whose payload is too small for the declared
 * block count, yields an unsuccessful result with no blocks.
 */
object FelicaFrameDecoder {
    private const val FRAME_HEADER_SIZE = 13
    private const val BLOCK_SIZE = 16
    private const val STATUS_FLAG1_INDEX = 10
    private const val STATUS_FLAG2_INDEX = 11
    private const val BLOCK_COUNT_INDEX = 12
    private const val ERROR_FLAG = -1

    fun decode(frame: ByteArray): FelicaReadResult {
        if (frame.size < FRAME_HEADER_SIZE) {
            return FelicaReadResult(ERROR_FLAG, ERROR_FLAG, emptyList())
        }

        val statusFlag1 = frame[STATUS_FLAG1_INDEX].toInt() and 0xFF
        val statusFlag2 = frame[STATUS_FLAG2_INDEX].toInt() and 0xFF
        val numBlocks = frame[BLOCK_COUNT_INDEX].toInt() and 0xFF
        val blockData = frame.copyOfRange(FRAME_HEADER_SIZE, frame.size)

        val blocks = mutableListOf<ByteArray>()
        if (blockData.size >= numBlocks * BLOCK_SIZE) {
            for (i in 0 until numBlocks) {
                val offset = i * BLOCK_SIZE
                blocks.add(blockData.copyOfRange(offset, offset + BLOCK_SIZE))
            }
        }

        return FelicaReadResult(statusFlag1, statusFlag2, blocks)
    }
}
