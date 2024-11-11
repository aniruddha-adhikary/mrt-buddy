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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.dao.TransactionDao
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.data.ScanEntity
import net.adhikary.mrtbuddy.data.TransactionEntity
import net.adhikary.mrtbuddy.model.CardReadResult
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.model.TransactionWithAmount

class MainScreenViewModel(
    private val cardDao: CardDao,
    private val scanDao: ScanDao,
    private val transactionDao: TransactionDao
) : ViewModel() {

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

            is MainScreenAction.UpdateCardReadResult -> {
                saveCardReadResult(action.cardReadResult)
                val transactionsWithAmount = transactionMapper(action.cardReadResult.transactions)
                _state.update {
                    it.copy(
                        cardIdm = action.cardReadResult.idm,
                        transaction = action.cardReadResult.transactions,
                        transactionWithAmount = transactionsWithAmount
                    )
                }
            }
        }
    }

    private fun generateTransactionId(txn: Transaction): String {
        return "${txn.fixedHeader}_${txn.fromStation}_${txn.toStation}_${txn.balance}_${txn.timestamp}"
    }

    private fun saveCardReadResult(result: CardReadResult) {
        viewModelScope.launch {
            val cardEntity = CardEntity(idm = result.idm, name = null)
            cardDao.insertCard(cardEntity)

            val scanEntity = ScanEntity(cardIdm = result.idm)
            val scanId = scanDao.insertScan(scanEntity)

            val transactionEntities = result.transactions.map { txn ->
                TransactionEntity(
                    cardIdm = result.idm,
                    transactionId = generateTransactionId(txn),
                    scanId = scanId,
                    fromStation = txn.fromStation,
                    toStation = txn.toStation,
                    balance = txn.balance,
                    dateTime = txn.timestamp.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                )
            }

            transactionDao.insertTransactions(transactionEntities)
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
