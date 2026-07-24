package net.adhikary.mrtbuddy.nfc

/**
 * In-memory [CardTransceiver] for driving [FelicaReader] on the JVM without hardware.
 * Maps a window's start block to a canned [FelicaReadResult], and can simulate an
 * I/O failure on a given window by throwing [CardTransceiverException].
 */
class FakeCardTransceiver(
    override val idm: ByteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08),
    private val responses: Map<Int, FelicaReadResult> = emptyMap(),
    private val throwOnStartBlock: Int? = null,
) : CardTransceiver {
    override suspend fun readBlocks(
        serviceCode: Int,
        startBlock: Int,
        count: Int,
    ): FelicaReadResult {
        if (startBlock == throwOnStartBlock) {
            throw CardTransceiverException("Simulated I/O failure at block $startBlock")
        }
        return responses[startBlock] ?: FelicaReadResult(0, 0, emptyList())
    }
}
