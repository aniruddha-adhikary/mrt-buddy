package net.adhikary.mrtbuddy.nfc

import android.nfc.tech.NfcF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Android [CardTransceiver] adapter over [NfcF]. Builds the read command with
 * [NfcCommandGenerator], performs the `transceive`, and decodes the raw response frame
 * with the shared [FelicaFrameDecoder]. Wraps `IOException` as [CardTransceiverException]
 * so [FelicaReader] can apply its partial-result semantics.
 */
class NfcFTransceiver(
    private val nfcF: NfcF,
) : CardTransceiver {
    private val commandGenerator = NfcCommandGenerator()

    override val idm: ByteArray = nfcF.tag.id

    override suspend fun readBlocks(
        serviceCode: Int,
        startBlock: Int,
        count: Int,
    ): FelicaReadResult =
        withContext(Dispatchers.IO) {
            try {
                val command =
                    commandGenerator.generateReadCommand(
                        idm = idm,
                        serviceCode = serviceCode,
                        numberOfBlocksToRead = count,
                        startBlockNumber = startBlock,
                    )
                FelicaFrameDecoder.decode(nfcF.transceive(command))
            } catch (e: IOException) {
                throw CardTransceiverException(e.message, e)
            }
        }
}
