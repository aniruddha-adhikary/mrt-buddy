package net.adhikary.mrtbuddy.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.MrtBuddyApplication
import net.adhikary.mrtbuddy.model.CardAlias
import net.adhikary.mrtbuddy.model.Transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TestDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Insert test data
        val cardAliasDao = (application as MrtBuddyApplication).database.cardAliasDao()
        lifecycleScope.launch {
            try {
                // Insert test card alias
                println("Inserting test card alias")
                cardAliasDao.insertAlias(CardAlias("test_card_id", "Test Card"))

                // Verify card alias insertion
                val alias = cardAliasDao.getAlias("test_card_id")
                println("Retrieved card alias: ${alias?.alias}")
            } catch (e: Exception) {
                println("Error inserting test data: ${e.message}")
                e.printStackTrace()
            }
        }

        // Create sample transactions
        val sampleTransactions = listOf(
            Transaction(
                fixedHeader = "TEST_HEADER",
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                balance = 100,
                fromStation = "Uttara North",
                toStation = "Agargaon",
                transactionType = "Entry",
                trailing = "TEST_TRAILING"
            ),
            Transaction(
                fixedHeader = "TEST_HEADER",
                timestamp = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                balance = 80,
                fromStation = "Agargaon",
                toStation = "Farmgate",
                transactionType = "Exit",
                trailing = "TEST_TRAILING"
            )
        )

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MonthlyReportsScreen(transactions = sampleTransactions)
                }
            }
        }
    }
}
