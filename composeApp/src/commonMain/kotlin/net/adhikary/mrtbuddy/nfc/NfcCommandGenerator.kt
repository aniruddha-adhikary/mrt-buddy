package net.adhikary.mrtbuddy.nfc

class NfcCommandGenerator {
    fun generateReadCommand(
        idm: ByteArray,
        serviceCode: Int = 0x220F,
        numberOfBlocksToRead: Int = 10,
        startBlockNumber: Int = 0
    ): ByteArray {
        // Convert the service code into a byte array (little-endian)
        val serviceCodeList = byteArrayOf(
            (serviceCode and 0xFF).toByte(),
            ((serviceCode shr 8) and 0xFF).toByte()
        )

        // Prepare the block list elements
        val blockListElements = ByteArray(numberOfBlocksToRead * 2)
        for (i in 0 until numberOfBlocksToRead) {
            blockListElements[i * 2] = 0x80.toByte() // Control byte
            blockListElements[i * 2 + 1] = (startBlockNumber + i).toByte() // Block number
        }

        // Calculate the total length of the command
        val commandLength = 14 + blockListElements.size
        val command = ByteArray(commandLength)
        var idx = 0

        // Populate the command array step by step
        command[idx++] = commandLength.toByte()      // Command length
        command[idx++] = 0x06.toByte()              // Command code

        // Copy the IDM into the command array
        idm.copyInto(destination = command, destinationOffset = idx)
        idx += idm.size

        command[idx++] = 0x01.toByte()              // Some fixed byte (e.g., command type)
        command[idx++] = serviceCodeList[0]         // Service code low byte
        command[idx++] = serviceCodeList[1]         // Service code high byte
        command[idx++] = numberOfBlocksToRead.toByte() // Number of blocks to read

        // Copy the block list elements into the command array
        blockListElements.copyInto(destination = command, destinationOffset = idx)

        return command
    }
}
