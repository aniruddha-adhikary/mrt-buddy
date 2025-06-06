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
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.repository.TransactionRepository
import kotlinx.coroutines.flow.collect
import net.adhikary.mrtbuddy.changeLang
import net.adhikary.mrtbuddy.repository.SettingsRepository
import kotlin.io.println

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
        viewModelScope.launch {
            settingsRepository.currentLanguage.collect { language ->
                _state.update { it.copy(currentLanguage = language) }
            }
        }
    }

    val state: StateFlow<MainScreenState> get() = _state.asStateFlow()

    private val _events: Channel<MainScreenEvent> = Channel(Channel.BUFFERED)
    val events: Flow<MainScreenEvent> get() = _events.receiveAsFlow()

    fun onAction(action: MainScreenAction) {
        when (action) {


            is MainScreenAction.OnInit -> {
                viewModelScope.launch {
                    val savedLanguage = settingsRepository.currentLanguage.value
                    changeLang(savedLanguage)
                    _state.update { it.copy(currentLanguage = savedLanguage) }
                }
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
                    // Determine the current balance from the transactions.
                    // If transactions are empty, or for some other reason balance cannot be determined,
                    // consider a sensible fallback or how an error should be propagated.
                    // For now, let's assume transactions list is not empty for a successful read.
                    val currentBalance = action.cardReadResult.transactions.firstOrNull()?.balance
                                        ?: run {
                                            // Fallback: if transactions are empty, try to keep existing balance if state is Balance, else 0.
                                            // This part might need more robust error handling or domain logic.
                                            val existingBalance = (_state.value.cardState as? CardState.Balance)?.amount ?: 0
                                            // Optionally, log a warning here if transactions were expected but empty.
                                            println("Warning: CardReadResult transactions empty. IDM: ${action.cardReadResult.idm}. Using fallback balance: $existingBalance")
                                            existingBalance
                                        }

                    _state.update { currentState ->
                        currentState.copy(
                            cardState = CardState.Balance(currentBalance), // Set CardState.Balance here
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
