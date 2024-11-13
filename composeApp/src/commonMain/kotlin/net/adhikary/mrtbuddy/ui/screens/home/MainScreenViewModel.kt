package net.adhikary.mrtbuddy.ui.screens.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.repository.TransactionRepository
import kotlinx.coroutines.flow.collect
import net.adhikary.mrtbuddy.repository.SettingsRepository

class MainScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val initialState: MainScreenState,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private var autoSaveEnabled: Boolean = true

    private val _state: MutableStateFlow<MainScreenState> = MutableStateFlow(initialState)

    init {
        viewModelScope.launch {
            settingsRepository.autoSaveEnabled.collect { isEnabled ->
                autoSaveEnabled = isEnabled
            }
        }
    }

    val state: StateFlow<MainScreenState> get() = _state.asStateFlow()

    private val _events: Channel<MainScreenEvent> = Channel(Channel.BUFFERED)
    val events: Flow<MainScreenEvent> get() = _events.receiveAsFlow()

    fun onAction(action: MainScreenAction) {
        when (action) {


            is MainScreenAction.OnInit -> {
                // nfc manager has  a composable function that returns a NfcManager
                // we will trigger a event to start the scanner


            }

            is MainScreenAction.UpdateCardState -> {

                // here state has been copied over new state
                _state.update {
                    it.copy(cardState = action.newState)
                }

            }

            is MainScreenAction.UpdateCardReadResult -> {
                if (autoSaveEnabled) {
                    saveCardReadResult(action.cardReadResult)
                }
                viewModelScope.launch {
                    val card = transactionRepository.getCardByIdm(action.cardReadResult.idm)
                    val transactionsWithAmount = transactionMapper(action.cardReadResult.transactions)
                    _state.update {
                        it.copy(
                            cardIdm = action.cardReadResult.idm,
                            cardName = card?.name,
                            transaction = action.cardReadResult.transactions,
                            transactionWithAmount = transactionsWithAmount
                        )
                    }
                }
            }
        }
    }

    private fun saveCardReadResult(result: CardReadResult) {
        viewModelScope.launch {
            transactionRepository.saveCardReadResult(result)
        }
    }

    private fun transactionMapper(transactions: List<Transaction>): List<TransactionWithAmount> {
     return   transactions.mapIndexed { index, transaction ->
            val amount = if (index + 1 < transactions.size) {
                transaction.balance - transactions[index + 1].balance
            } else {
                null
            }
            TransactionWithAmount(
                transaction = transaction,
                amount = amount
            )
        }
    }


}
