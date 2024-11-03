data class Transaction(
    val fixedHeader: String,
    val timestamp: String,
    val transactionType: String,
    val fromStation: Int,
    val toStation: Int,
    val balance: Int,
    val trailing: String
)
