package net.adhikary.mrtbuddy.nfc.parser

import android.util.Log
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService

class TransactionParser(
    private val byteParser: ByteParser,
    private val timestampService: TimestampService,
    private val stationService: StationService
) {
    fun parseTransactionResponse(response: ByteArray): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        Log.d("NFC", "Response: ${byteParser.toHexString(response)}")

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
        val fixedHeaderStr = byteParser.toHexString(fixedHeader)

        val timestampValue = byteParser.extractInt16(block, 4)
        val transactionTypeBytes = block.copyOfRange(6, 8)
        val transactionType = byteParser.toHexString(transactionTypeBytes)

        val fromStationCode = byteParser.extractByte(block, 8)
        val toStationCode = byteParser.extractByte(block, 10)
        val balance = byteParser.extractInt24(block, 11)

        val trailingBytes = block.copyOfRange(14, 16)
        val trailing = byteParser.toHexString(trailingBytes)

        val timestamp = timestampService.decodeTimestamp(timestampValue)
        val fromStation = stationService.getStationName(fromStationCode)
        val toStation = stationService.getStationName(toStationCode)

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