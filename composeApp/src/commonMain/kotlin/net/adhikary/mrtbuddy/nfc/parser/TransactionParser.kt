package net.adhikary.mrtbuddy.nfc.parser

import kotlinx.datetime.LocalDateTime
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService

object TransactionParser {
    private fun isValidTransaction(transaction: Transaction): Boolean {
        val cutoffDate = LocalDateTime(2020, 1, 1, 0, 0)
        return transaction.timestamp > cutoffDate;
    }

    fun parseTransactionResponse(response: ByteArray): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

//        Log.d("NFC", "Response: ${ByteParser.toHexString(response)}")

        if (response.size < 13) {
//            Log.e("NFC", "Response too short")
            return transactions
        }

        val statusFlag1 = response[10]
        val statusFlag2 = response[11]

        if (statusFlag1 != 0x00.toByte() || statusFlag2 != 0x00.toByte()) {
//            Log.e("NFC", "Error reading card: Status flags $statusFlag1 $statusFlag2")
            return transactions
        }

        val numBlocks = response[12].toInt() and 0xFF
        val blockData = response.copyOfRange(13, response.size)

        val blockSize = 16
        if (blockData.size < numBlocks * blockSize) {
//            Log.e("NFC", "Incomplete block data")
            return transactions
        }

        for (i in 0 until numBlocks) {
            val offset = i * blockSize
            val block = blockData.copyOfRange(offset, offset + blockSize)
            val transaction = parseTransactionBlock(block)
            if (isValidTransaction(transaction)) {
                transactions.add(transaction)
            }
        }

        return transactions
    }

    fun parseTransactionBlock(block: ByteArray): Transaction {
        if (block.size != 16) {
            throw IllegalArgumentException("Invalid block size")
        }

        val fixedHeader = block.copyOfRange(0, 4)
        val fixedHeaderStr = ByteParser.toHexString(fixedHeader)

        val timestampValue = ByteParser.extractInt24BigEndian(block, 4)
        val transactionTypeBytes = block.copyOfRange(6, 8)
        val transactionType = ByteParser.toHexString(transactionTypeBytes)

        val fromStationCode = ByteParser.extractByte(block, 8)
        val toStationCode = ByteParser.extractByte(block, 10)
        val balance = ByteParser.extractInt24(block, 11)

        val trailingBytes = block.copyOfRange(14, 16)
        val trailing = ByteParser.toHexString(trailingBytes)

        val timestamp = TimestampService.decodeTimestamp(timestampValue)
        val fromStation = StationService.getStationName(fromStationCode)
        val toStation = StationService.getStationName(toStationCode)

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
}
