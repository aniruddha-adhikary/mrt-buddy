package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.cancel
import mrtbuddy.composeapp.generated.resources.cardName
import mrtbuddy.composeapp.generated.resources.rename
import mrtbuddy.composeapp.generated.resources.renameCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun RenameDialog(
    currentName: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.renameCard)) },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text(stringResource(Res.string.cardName)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(newName)
                    onDismiss()
                }
            ) {
                Text(stringResource(Res.string.rename))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
