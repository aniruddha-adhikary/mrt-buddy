package net.adhikary.mrtbuddy.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount
import net.adhikary.mrtbuddy.model.TransactionType

object CsvExportService {

    private const val CSV_HEADER = "Date,Time,From Station,To Station,Amount,Balance,Card ID,Transaction Type"

    fun generateCsv(
        transactions: List<TransactionEntityWithAmount>,
        cardIdm: String
    ): String {
        val rows = transactions.map { txnWithAmount ->
            val txn = txnWithAmount.transactionEntity
            val dateTime = Instant.fromEpochMilliseconds(txn.dateTime)
                .toLocalDateTime(TimeZone.of("Asia/Dhaka"))

            val date = formatDate(dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth)
            val time = formatTime(dateTime.hour, dateTime.minute)
            val fromStation = escapeCsvField(txn.fromStation)
            val toStation = escapeCsvField(txn.toStation)
            val amount = txnWithAmount.amount?.toString() ?: ""
            val balance = txn.balance.toString()
            val transactionType = getTransactionTypeName(txn.fixedHeader)

            "$date,$time,$fromStation,$toStation,$amount,$balance,$cardIdm,$transactionType"
        }

        return buildString {
            appendLine(CSV_HEADER)
            rows.forEach { appendLine(it) }
        }
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
