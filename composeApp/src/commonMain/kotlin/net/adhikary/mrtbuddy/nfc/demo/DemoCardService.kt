package net.adhikary.mrtbuddy.nfc.demo

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.FelicaReader

/**
 * Debug-only feed that simulates a full card scan without hardware. It exposes the same
 * `cardState`/`cardReadResults` shape as `NFCManager` so `App.kt` can route demo scans through
 * the identical `MainScreenAction` path a physical scan uses (rendering + auto-save persistence).
 */
class DemoCardService {
    private val _cardState = MutableSharedFlow<CardState>(replay = 1)
    val cardState: SharedFlow<CardState> = _cardState

    private val _cardReadResults = MutableSharedFlow<CardReadResult?>(replay = 1)
    val cardReadResults: SharedFlow<CardReadResult?> = _cardReadResults

    suspend fun scanDemoCard(): CardReadResult {
        _cardState.emit(CardState.Reading)
        val result = FelicaReader(DemoCardTransceiver()).readTransactionHistory(DemoCards.BASE_YEAR)
        _cardReadResults.emit(result)
        val latestBalance = result.transactions.firstOrNull()?.balance
        if (latestBalance != null) {
            _cardState.emit(CardState.Balance(latestBalance))
        } else {
            _cardState.emit(CardState.Error("Demo balance not found"))
        }
        return result
    }
}
