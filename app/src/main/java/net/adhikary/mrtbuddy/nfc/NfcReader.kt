package net.adhikary.mrtbuddy.nfc

import android.nfc.tech.NfcF
import android.util.Log
import net.adhikary.mrtbuddy.model.Transaction
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class NfcReader {
    fun readTransactionHistory(nfcF: NfcF): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val idm = nfcF.tag.id
        val serviceCode = 0x220F
        val serviceCodeList = byteArrayOf(
            (serviceCode and 0xFF).toByte(),
            ((serviceCode shr 8) and 0xFF).toByte()
        )

        val numberOfBlocksToRead = 10

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

        try {
            val response = nfcF.transceive(command)
            transactions.addAll(parseTransactionResponse(response))
        } catch (e: IOException) {
            Log.e("NFC", "Error communicating with card", e)
        }

        return transactions
    }

    private fun parseTransactionResponse(response: ByteArray): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        Log.d("NFC", "Response: ${response.joinToString(" ") { "%02X".format(it) }}")

        if (response.size < 13) {
            Log.e("NFC", "Response too short")
            return transactions
        }

        val statusFlag1 = response[10]
        val statusFlag2 = response[11]

        if (statusFlag1 != 0x00.toByte() || statusFlag2 != 0x00.toByte()) {
            Log.e("NFC", "Error reading card: Status flags $statusFlag1 $statusFlag2")
            return transactions
        }

        val numBlocks = response[12].toInt() and 0xFF
        val blockData = response.copyOfRange(13, response.size)

        val blockSize = 16
        if (blockData.size < numBlocks * blockSize) {
            Log.e("NFC", "Incomplete block data")
            return transactions
        }

        for (i in 0 until numBlocks) {
            val offset = i * blockSize
            val block = blockData.copyOfRange(offset, offset + blockSize)
            val transaction = parseTransactionBlock(block)
            transactions.add(transaction)
        }

        return transactions
    }

    private fun parseTransactionBlock(block: ByteArray): Transaction {
        if (block.size != 16) {
            throw IllegalArgumentException("Invalid block size")
        }

        val fixedHeader = block.copyOfRange(0, 4)
        val fixedHeaderStr = fixedHeader.joinToString(" ") { "%02X".format(it) }

        val timestampBytes = block.copyOfRange(4, 6)
        val timestampValue = ((timestampBytes[1].toInt() and 0xFF) shl 8) or
                (timestampBytes[0].toInt() and 0xFF)

        val transactionTypeBytes = block.copyOfRange(6, 8)
        val transactionType = transactionTypeBytes.joinToString(" ") { "%02X".format(it) }

        val fromStationCode = block[8].toInt() and 0xFF
        val separator = block[9].toInt() and 0xFF
        val toStationCode = block[10].toInt() and 0xFF

        val balanceBytes = block.copyOfRange(11, 14)
        val balance = ((balanceBytes[2].toInt() and 0xFF) shl 16) or
                ((balanceBytes[1].toInt() and 0xFF) shl 8) or
                (balanceBytes[0].toInt() and 0xFF)

        val trailingBytes = block.copyOfRange(14, 16)
        val trailing = trailingBytes.joinToString(" ") { "%02X".format(it) }

        val timestamp = decodeTimestamp(timestampValue)
        val fromStation = getStationName(fromStationCode)
        val toStation = getStationName(toStationCode)

        return Transaction(
            fixedHeader = fixedHeaderStr,
            timestamp = timestamp,
            transactionType = transactionType,
            fromStation = fromStation,
            toStation = toStation,
            balance = balance,
            trailing = trailing
        )
    }

    private fun decodeTimestamp(value: Int): String {
        val baseTime = System.currentTimeMillis() - (value * 60 * 1000L)
        val date = Date(baseTime)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.format(date)
    }

    private fun getStationName(code: Int): String {
        val stationMap = mapOf(
            10 to "Motijheel",
            20 to "Bangladesh Secretariat",
            25 to "Dhaka University",
            30 to "Shahbagh",
            35 to "Karwan Bazar",
            40 to "Farmgate",
            45 to "Bijoy Sarani",
            50 to "Agargaon",
            55 to "Shewrapara",
            60 to "Kazipara",
            65 to "Mirpur 10",
            70 to "Mirpur 11",
            75 to "Pallabi",
            80 to "Uttara South",
            85 to "Uttara Center",
            95 to "Uttara North"
        )
        return stationMap.getOrDefault(code, "Unknown Station ($code)")
    }
}