package net.adhikary.mrtbuddy.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme

class MonthlyReportsTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create sample transactions across different months
        val sampleTransactions = listOf(
            Transaction(
                fixedHeader = "header",
                timestamp = "20240304153000",
                transactionType = "Commute",
                fromStation = "Uttara North",
                toStation = "Agargaon",
                balance = 500,
                trailing = "trailing"
            ),
            Transaction(
                fixedHeader = "header",
                timestamp = "20240304143000",
                transactionType = "Commute",
                fromStation = "Agargaon",
                toStation = "Uttara North",
                balance = 440,
                trailing = "trailing"
            ),
            Transaction(
                fixedHeader = "header",
                timestamp = "20240303123000",
                transactionType = "BalanceUpdate",
                fromStation = "",
                toStation = "",
                balance = 600,
                trailing = "trailing"
            ),
            Transaction(
                fixedHeader = "header",
                timestamp = "20240228183000",
                transactionType = "Commute",
                fromStation = "Uttara Center",
                toStation = "Farmgate",
                balance = 530,
                trailing = "trailing"
            )
        )

        setContent {
            MRTBuddyTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MonthlyReportsScreen(transactions = sampleTransactions)
                }
            }
        }
    }
}
