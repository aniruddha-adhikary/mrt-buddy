package net.adhikary.mrtbuddy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.Transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.ZonedDateTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyReportsScreen(transactions: List<Transaction>) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val groupedTransactions = transactions.groupBy { transaction ->
        LocalDateTime.parse(transaction.timestamp, dateFormatter).let { "${it.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${it.year}" }
    }.toSortedMap(reverseOrder())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Monthly Transaction Reports",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            groupedTransactions.forEach { (month, monthTransactions) ->
                item {
                    Text(
                        text = month,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(monthTransactions.sortedByDescending { it.timestamp }) { transaction ->
                    TransactionCard(transaction = transaction)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = "Implemented by Irfan",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://irfanhasan.vercel.app/"))
                                context.startActivity(intent)
                            },
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionCard(transaction: Transaction) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalDateTime.parse(transaction.timestamp, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${transaction.balance} BDT",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    DetailRow("Transaction Type", transaction.transactionType)
                    DetailRow("Station", transaction.station ?: "N/A")
                    DetailRow("Card Balance", "${transaction.balance} BDT")
                    DetailRow("Card Type", "MRT/Rapid Pass")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
