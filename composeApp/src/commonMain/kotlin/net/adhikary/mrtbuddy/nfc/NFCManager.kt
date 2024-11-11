package net.adhikary.mrtbuddy.nfc

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class NFCManager() {
    val cardState: StateFlow<CardState>
    val transactions: StateFlow<List<Transaction>>

@Composable
    fun startScan()

    fun stopScan()

    fun isEnabled(): Boolean
    fun isSupported(): Boolean
}

@Composable
expect fun getNFCManager(): NFCManager