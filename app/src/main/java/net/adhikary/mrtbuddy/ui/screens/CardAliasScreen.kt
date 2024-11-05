package net.adhikary.mrtbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.adhikary.mrtbuddy.MrtBuddyApplication
import net.adhikary.mrtbuddy.viewmodel.CardAliasViewModel
import net.adhikary.mrtbuddy.viewmodel.CardAliasViewModelFactory
import net.adhikary.mrtbuddy.model.CardAlias
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardAliasScreen() {
    val cardAliasDao = MrtBuddyApplication.instance.database.cardAliasDao()
    val viewModel: CardAliasViewModel = viewModel(
        factory = CardAliasViewModelFactory(cardAliasDao)
    )

    var newCardId by remember { mutableStateOf("") }
    var newAlias by remember { mutableStateOf("") }
    val aliases by viewModel.allAliases.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Card Aliases") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Add new alias section
            OutlinedTextField(
                value = newCardId,
                onValueChange = { newCardId = it },
                label = { Text("Card ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newAlias,
                onValueChange = { newAlias = it },
                label = { Text("Alias") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (newCardId.isNotBlank() && newAlias.isNotBlank()) {
                        viewModel.addAlias(newCardId, newAlias)
                        newCardId = ""
                        newAlias = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Alias")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // List of existing aliases
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(aliases) { alias ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = alias.alias,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Card ID: ${alias.cardId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteAlias(alias) }) {
                                Text("×", style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }

            // Attribution text
            val uriHandler = LocalUriHandler.current
            Text(
                text = "Implemented by Irfan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { uriHandler.openUri("https://irfanhasan.vercel.app/") }
                    .padding(8.dp)
            )
        }
    }
}
