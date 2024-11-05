package net.adhikary.mrtbuddy.nfc

class NfcCommandGenerator {
    fun generateReadCommand(
        idm: ByteArray,
        serviceCode: Int = 0x220F,
        numberOfBlocksToRead: Int = 10
    ): ByteArray {
        val serviceCodeList = byteArrayOf(
            (serviceCode and 0xFF).toByte(),
            ((serviceCode shr 8) and 0xFF).toByte()
        )

        val blockListElements = ByteArray(numberOfBlocksToRead * 2)
        for (i in 0 until numberOfBlocksToRead) {
            blockListElements[i * 2] = 0x80.toByte()
            blockListElements[i * 2 + 1] = i.toByte()
        }

        val commandLength = 14 + blockListElements.size
        val command = ByteArray(commandLength)
        var idx = 0
        command[idx++] = commandLength.toByte()
        command[idx++] = 0x06.toByte()
        System.arraycopy(idm, 0, command, idx, idm.size)
        idx += idm.size
        command[idx++] = 0x01.toByte()
        command[idx++] = serviceCodeList[0]
        command[idx++] = serviceCodeList[1]
        command[idx++] = numberOfBlocksToRead.toByte()
        System.arraycopy(blockListElements, 0, command, idx, blockListElements.size)

        return command
    }
}