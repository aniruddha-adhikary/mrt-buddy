package net.adhikary.mrtbuddy.model

data class Transaction(
    val fixedHeader: String,
    val timestamp: String,
    val transactionType: String,
    val fromStation: String,
    val toStation: String,
    val balance: Int,
    val trailing: String
) {
    val station: String?
        get() = when (transactionType) {
            "Commute" -> "$fromStation → $toStation"
            else -> null
        }
}

data class TransactionWithAmount(
    val transaction: Transaction,
    val amount: Int?
)

sealed class TransactionType {
    data object Commute : TransactionType()
    data object BalanceUpdate : TransactionType()
}

sealed class CardState {
    data class Balance(
        val amount: Int,
        val alias: String? = null,
        val lowBalanceThreshold: Double? = null,
        val isLowBalance: Boolean = lowBalanceThreshold != null && amount < lowBalanceThreshold
    ) : CardState()
    data object WaitingForTap : CardState()
    data object Reading : CardState()
    data class Error(val message: String) : CardState()
    data object NoNfcSupport : CardState()
    data object NfcDisabled : CardState()
}
