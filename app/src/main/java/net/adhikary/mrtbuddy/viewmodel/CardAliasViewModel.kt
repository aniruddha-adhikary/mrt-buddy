package net.adhikary.mrtbuddy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.data.AppDatabase
import net.adhikary.mrtbuddy.model.CardAlias

class CardAliasViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val cardAliasDao = database.cardAliasDao()

    val allCards: Flow<List<CardAlias>> = cardAliasDao.getAllAliases()
    val lowBalanceCards: Flow<List<CardAlias>> = cardAliasDao.getLowBalanceCards()

    fun addCard(card: CardAlias) {
        viewModelScope.launch {
            cardAliasDao.insertOrUpdateAlias(card)
        }
    }

    fun updateCard(card: CardAlias) {
        viewModelScope.launch {
            cardAliasDao.insertOrUpdateAlias(card)
        }
    }

    fun deleteCard(card: CardAlias) {
        viewModelScope.launch {
            cardAliasDao.deleteAlias(card)
        }
    }

    suspend fun getCardBySerialNumber(serialNumber: String): CardAlias? {
        return cardAliasDao.getAliasBySerialNumber(serialNumber)
    }

    fun updateCardBalance(serialNumber: String, balance: Double) {
        viewModelScope.launch {
            cardAliasDao.updateBalance(serialNumber, balance)
        }
    }
}
