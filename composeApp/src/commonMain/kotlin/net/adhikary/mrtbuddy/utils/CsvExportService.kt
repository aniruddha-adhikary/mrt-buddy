package net.adhikary.mrtbuddy.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount
import net.adhikary.mrtbuddy.model.TransactionType

object CsvExportService {

    private const val CSV_HEADER = "Date,Time,From Station,To Station,Amount,Balance,Card ID,Transaction Type"

    /**
     * Writes CSV header to the file writer
     */
    fun writeHeader(writer: CsvFileWriter) {
        writer.appendLine(CSV_HEADER)
    }

    /**
     * Writes a batch of transactions to the file writer (streaming mode)
     */
    fun writeBatch(
        writer: CsvFileWriter,
        transactions: List<TransactionEntityWithAmount>,
        cardIdm: String
    ) {
        for (txnWithAmount in transactions) {
            val line = formatTransactionLine(txnWithAmount, cardIdm)
            writer.appendLine(line)
        }
    }

    private fun formatTransactionLine(txnWithAmount: TransactionEntityWithAmount, cardIdm: String): String {
        val txn = txnWithAmount.transactionEntity
        val dateTime = Instant.fromEpochMilliseconds(txn.dateTime)
            .toLocalDateTime(TimeZone.of("Asia/Dhaka"))

        return buildString {
            append(formatDate(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth))
            append(',')
            append(formatTime(dateTime.hour, dateTime.minute))
            append(',')
            append(escapeCsvField(txn.fromStation))
            append(',')
            append(escapeCsvField(txn.toStation))
            append(',')
            append(txnWithAmount.amount?.toString() ?: "")
            append(',')
            append(txn.balance)
            append(',')
            append(cardIdm)
            append(',')
            append(getTransactionTypeName(txn.fixedHeader))
        }
    }

    fun generateCsv(
        transactions: List<TransactionEntityWithAmount>,
        cardIdm: String
    ): String {
        // Pre-calculate capacity to avoid StringBuilder resizing
        val estimatedSize = CSV_HEADER.length + 2 + (transactions.size * 100)

        return StringBuilder(estimatedSize).apply {
            appendLine(CSV_HEADER)

            // Process each transaction directly without intermediate list
            for (txnWithAmount in transactions) {
                val txn = txnWithAmount.transactionEntity
                val dateTime = Instant.fromEpochMilliseconds(txn.dateTime)
                    .toLocalDateTime(TimeZone.of("Asia/Dhaka"))

                append(formatDate(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth))
                append(',')
                append(formatTime(dateTime.hour, dateTime.minute))
                append(',')
                append(escapeCsvField(txn.fromStation))
                append(',')
                append(escapeCsvField(txn.toStation))
                append(',')
                append(txnWithAmount.amount?.toString() ?: "")
                append(',')
                append(txn.balance)
                append(',')
                append(cardIdm)
                append(',')
                appendLine(getTransactionTypeName(txn.fixedHeader))
            }
        }.toString()
    }

    fun generateFilename(cardName: String?, timestamp: Long): String {
        val dateTime = Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.of("Asia/Dhaka"))
        val date = formatDate(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth)

        val sanitizedName = if (cardName.isNullOrBlank()) {
            "Card"
        } else {
            sanitizeFilename(cardName)
        }

        return "MRT_${sanitizedName}_$date.csv"
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }

    private fun formatTime(hour: Int, minute: Int): String {
        return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

    private fun escapeCsvField(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun sanitizeFilename(name: String): String {
        return name
            .replace(Regex("[\\\\/:*?\"<>|]"), "")
            .replace(" ", "-")
            .take(50)
    }

    private fun getTransactionTypeName(fixedHeader: String): String {
        return when (TransactionType.fromHeader(fixedHeader)) {
            is TransactionType.CommuteDhakaMetro -> "CommuteDhakaMetro"
            is TransactionType.CommuteHatirjheelBusStart -> "CommuteHatirjheelBusStart"
            is TransactionType.CommuteHatirjheelBusEnd -> "CommuteHatirjheelBusEnd"
            is TransactionType.BalanceUpdate -> "BalanceUpdate"
            is TransactionType.CommuteUnknown -> "CommuteUnknown"
        }
    }
}
