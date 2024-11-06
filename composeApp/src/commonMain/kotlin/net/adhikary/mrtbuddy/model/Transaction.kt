package net.adhikary.mrtbuddy.model

data class Transaction(
    val fixedHeader: String,
    val timestamp: String,
    val transactionType: String,
    val fromStation: String,
    val toStation: String,
    val balance: Int,
    val trailing: String
)

data class TransactionWithAmount(
    val transaction: Transaction,
    val amount: Int?
)

sealed class TransactionType {
    data object Commute : TransactionType()
    data object BalanceUpdate : TransactionType()
}

sealed class CardState {
    data class Balance(val amount: Int) : CardState()
    data object WaitingForTap : CardState()
    data object Reading : CardState()
    data class Error(val message: String) : CardState()
    data object NoNfcSupport : CardState()
    data object NfcDisabled : CardState()
}