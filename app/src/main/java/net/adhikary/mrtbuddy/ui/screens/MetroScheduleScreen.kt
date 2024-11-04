package net.adhikary.mrtbuddy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.adhikary.mrtbuddy.model.MetroSchedule
import net.adhikary.mrtbuddy.model.TimeSlot
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetroScheduleScreen() {
    var selectedDay by remember { mutableStateOf(getCurrentDayOfWeek()) }
    val schedule = remember(selectedDay) { MetroSchedule.getScheduleForDay(selectedDay) }
    val currentTime = remember { LocalTime.now() }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metro Schedule") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                DaySelector(
                    selectedDay = selectedDay,
                    onDaySelected = { selectedDay = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Operating Hours",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("First Train: ${schedule.firstTrain.format(timeFormatter)}")
                            Text("Last Train: ${schedule.lastTrain.format(timeFormatter)}")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(schedule.timeSlots) { timeSlot ->
                TimeSlotCard(
                    timeSlot = timeSlot,
                    timeFormatter = timeFormatter,
                    isCurrentTimeSlot = currentTime in timeSlot.startTime..timeSlot.endTime
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Important Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedDay != DayOfWeek.FRIDAY) {
                            InfoRow(
                                text = "First two trains (7:10 AM & 7:20 AM) are MRT/Rapid Pass only"
                            )
                        }

                        InfoRow(
                            text = "After 9:13 PM, trains require MRT/Rapid Pass"
                        )

                        InfoRow(
                            text = if (selectedDay == DayOfWeek.FRIDAY) {
                                "Ticket sales: 3:30 PM - 8:50 PM"
                            } else {
                                "Ticket sales: 7:20 AM - 8:50 PM"
                            }
                        )

                        InfoRow(
                            text = "Ticket offices close at 8:50 PM"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DaySelector(
    selectedDay: DayOfWeek,
    onDaySelected: (DayOfWeek) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DayOfWeek.values().forEach { day ->
            FilterChip(
                selected = selectedDay == day,
                onClick = { onDaySelected(day) },
                label = {
                    Text(
                        day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    )
                }
            )
        }
    }
}

@Composable
private fun TimeSlotCard(
    timeSlot: TimeSlot,
    timeFormatter: DateTimeFormatter,
    isCurrentTimeSlot: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentTimeSlot) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = "${timeSlot.startTime.format(timeFormatter)} - ${timeSlot.endTime.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Every ${timeSlot.frequency} minutes",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun InfoRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(16.dp)
        )
        Text(text = text)
    }
}

private fun getCurrentDayOfWeek(): DayOfWeek {
    return DayOfWeek.from(java.time.LocalDate.now())
}
