package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun RenameDialog(
    currentName: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Card") },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Card Name") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(newName)
                    onDismiss()
                }
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
