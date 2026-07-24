package net.adhikary.mrtbuddy.nfc

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreNFC.NFCFeliCaTagProtocol
import platform.Foundation.NSData
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * iOS [CardTransceiver] adapter over CoreNFC's [NFCFeliCaTagProtocol]. Wraps the
 * callback-based `readWithoutEncryptionWithServiceCodeList` in a suspend function and
 * maps the returned status flags + per-block `NSData` into a [FelicaReadResult]. The
 * exact CoreNFC call as before — no radio-level change.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class FelicaTagTransceiver(
    private val tag: NFCFeliCaTagProtocol,
) : CardTransceiver {
    override val idm: ByteArray = tag.currentIDm().toByteArray()

    override suspend fun readBlocks(
        serviceCode: Int,
        startBlock: Int,
        count: Int,
    ): FelicaReadResult =
        suspendCancellableCoroutine { continuation ->
            val serviceCodeList =
                listOf(
                    byteArrayOf(
                        (serviceCode and 0xFF).toByte(),
                        ((serviceCode shr 8) and 0xFF).toByte(),
                    ),
                )
            val blockList =
                (startBlock until startBlock + count).map { blockNumber ->
                    byteArrayOf(0x80.toByte(), blockNumber.toByte())
                }

            tag.readWithoutEncryptionWithServiceCodeList(
                serviceCodeList = serviceCodeList.map { it.toNSData() },
                blockList = blockList.map { it.toNSData() },
                completionHandler = { statusFlag1, statusFlag2, dataList, error ->
                    if (error != null) {
                        continuation.resumeWithException(
                            CardTransceiverException(error.localizedDescription),
                        )
                    } else {
                        val blocks =
                            (dataList ?: emptyList<Any?>()).mapNotNull { element ->
                                (element as? NSData)?.toByteArray()
                            }
                        continuation.resume(
                            FelicaReadResult(
                                statusFlag1 = statusFlag1.toInt(),
                                statusFlag2 = statusFlag2.toInt(),
                                blocks = blocks,
                            ),
                        )
                    }
                },
            )
        }
}
