package net.adhikary.mrtbuddy.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.repository.TransactionRepository

class HistoryScreenViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state: MutableStateFlow<HistoryScreenState> =
        MutableStateFlow(HistoryScreenState())
    val state: StateFlow<HistoryScreenState> get() = _state.asStateFlow()

    private val _events: Channel<HistoryScreenEvent> = Channel(Channel.BUFFERED)
    val events: Flow<HistoryScreenEvent> get() = _events.receiveAsFlow()

    fun onAction(action: HistoryScreenAction) {
        when (action) {
            is HistoryScreenAction.OnInit -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    try {
                        val cards = transactionRepository.getAllCards()
                        val cardsWithBalance = cards.map { card ->
                            val balance = transactionRepository.getLatestBalanceByCardIdm(card.idm)
                            CardWithBalance(card, balance)
                        }
                        _state.update { it.copy(isLoading = false, cards = cardsWithBalance) }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false, error = e.message) }
                        _events.send(HistoryScreenEvent.Error(e.message ?: "Unknown Error"))
                    }
                }
            }

            is HistoryScreenAction.OnCardAdded -> {
                viewModelScope.launch {
                    onAction(HistoryScreenAction.OnInit)
                }
            }

            is HistoryScreenAction.RenameCard -> {
                viewModelScope.launch {
                    try {
                        transactionRepository.renameCard(action.cardIdm, action.newName)
                        _events.send(HistoryScreenEvent.RenameSuccess)
                        // Refresh the list
                        onAction(HistoryScreenAction.OnInit)
                    } catch (e: Exception) {
                        _events.send(
                            HistoryScreenEvent.Error(
                                e.message ?: "Failed to rename card"
                            )
                        )
                    }
                }
            }

            is HistoryScreenAction.DeleteCard -> {
                viewModelScope.launch {
                    try {
                        transactionRepository.deleteCard(action.cardIdm)
                        _events.send(HistoryScreenEvent.DeleteSuccess)
                        // Refresh the list
                        onAction(HistoryScreenAction.OnInit)
                    } catch (e: Exception) {
                        _events.send(
                            HistoryScreenEvent.Error(
                                e.message ?: "Failed to delete card"
                            )
                        )
                    }
                }
            }
        }
    }
}
