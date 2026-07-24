package net.adhikary.mrtbuddy.nfc

/**
 * Platform-agnostic seam over a FeliCa card. Adapters (Android `NfcF`, iOS CoreNFC)
 * implement this so [FelicaReader] can orchestrate reads on any platform and in tests.
 */
interface CardTransceiver {
    val idm: ByteArray

    suspend fun readBlocks(
        serviceCode: Int,
        startBlock: Int,
        count: Int,
    ): FelicaReadResult
}

/**
 * Normalized result of a single block-read window: the two FeliCa status flags plus
 * the raw 16-byte blocks. [isSuccess] is true only when both flags are zero.
 */
data class FelicaReadResult(
    val statusFlag1: Int,
    val statusFlag2: Int,
    val blocks: List<ByteArray>,
) {
    val isSuccess: Boolean
        get() = statusFlag1 == 0 && statusFlag2 == 0
}

/**
 * Thrown by a [CardTransceiver] when the underlying radio read fails. [FelicaReader]
 * catches it to preserve partial-result semantics (return what was read before the failure).
 */
class CardTransceiverException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
