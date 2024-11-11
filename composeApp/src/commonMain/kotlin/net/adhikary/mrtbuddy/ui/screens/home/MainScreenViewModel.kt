package net.adhikary.mrtbuddy.ui.screens.home


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount

class MainScreenViewModel : ViewModel() {

    private val _state: MutableStateFlow<MainScreenState> =
        MutableStateFlow(MainScreenState())

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

            is MainScreenAction.UpdateTransactions -> {
                // here state has been copied over new state with new transactions
                // rest will not be updated
                // hence no ui will be redrawn
                val transactionsWithAmount = transactionMapper(action.transactions)
                _state.update {
                    it.copy(transaction = action.transactions , transactionWithAmount = transactionsWithAmount)
                }

            }
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