package net.adhikary.mrtbuddy.nfc.parser

import kotlinx.datetime.LocalDateTime
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.FelicaFrameDecoder
import net.adhikary.mrtbuddy.nfc.FelicaReadResult
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService

object TransactionParser {
    private const val BLOCK_SIZE = 16

    private fun isValidTransaction(transaction: Transaction): Boolean {
        val cutoffDate = LocalDateTime(2020, 1, 1, 0, 0)
        return transaction.timestamp > cutoffDate
    }

    fun parseTransactionResponse(
        response: ByteArray,
        baseYear: Int = TimestampService.currentBaseYear(),
    ): List<Transaction> = parseValidTransactions(FelicaFrameDecoder.decode(response), baseYear)

    /**
     * Parses the blocks of a single read window into valid transactions, honoring the
     * status flags (a non-success window yields no transactions) and the pre-2020 filter.
     */
    fun parseValidTransactions(
        result: FelicaReadResult,
        baseYear: Int = TimestampService.currentBaseYear(),
    ): List<Transaction> {
        if (!result.isSuccess) {
            return emptyList()
        }
        return result.blocks
            .filter { it.size == BLOCK_SIZE }
            .mapNotNull { block ->
                parseTransactionBlock(block, baseYear).takeIf { isValidTransaction(it) }
            }
    }

    fun parseTransactionBlock(
        block: ByteArray,
        baseYear: Int = TimestampService.currentBaseYear(),
    ): Transaction {
        if (block.size != BLOCK_SIZE) {
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

        val timestamp = TimestampService.decodeTimestamp(timestampValue, baseYear)
        val fromStation = StationService.getStationName(fromStationCode)
        val toStation = StationService.getStationName(toStationCode)

        return Transaction(
            fixedHeader = fixedHeaderStr,
            timestamp = timestamp,
            transactionType = transactionType,
            fromStation = fromStation,
            toStation = toStation,
            balance = balance,
            trailing = trailing,
        )
    }
}
