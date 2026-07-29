package net.adhikary.mrtbuddy.nfc

/**
 * [CardTransceiver] decorator that copies each read window into [NfcDumpRecorder] when capture
 * is enabled, then returns the delegate's result untouched. Platform `NFCManager`s wrap their
 * transceiver in this unconditionally; it no-ops (pure passthrough) while capture is disabled.
 */
class RecordingCardTransceiver(
    private val delegate: CardTransceiver,
) : CardTransceiver {
    override val idm: ByteArray = delegate.idm

    override suspend fun readBlocks(
        serviceCode: Int,
        startBlock: Int,
        count: Int,
    ): FelicaReadResult {
        val result = delegate.readBlocks(serviceCode, startBlock, count)
        NfcDumpRecorder.record(
            NfcDumpRecorder.Window(
                serviceCode = serviceCode,
                startBlock = startBlock,
                count = count,
                statusFlag1 = result.statusFlag1,
                statusFlag2 = result.statusFlag2,
                blocks = result.blocks,
            ),
        )
        return result
    }
}
