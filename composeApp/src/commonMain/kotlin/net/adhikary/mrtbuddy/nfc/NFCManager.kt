package net.adhikary.mrtbuddy.nfc

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.Transaction

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class NFCManager() {
    val cardState: SharedFlow<CardState>
    val transactions: SharedFlow<List<Transaction>>

    @Composable
    fun startScan()

    fun stopScan()

    fun isEnabled(): Boolean
    fun isSupported(): Boolean
}

@Composable
expect fun getNFCManager(): NFCManager