package net.adhikary.mrtbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.data.CardAliasDao
import net.adhikary.mrtbuddy.model.CardAlias

class CardAliasViewModel(private val cardAliasDao: CardAliasDao) : ViewModel() {
    val allAliases: Flow<List<CardAlias>> = cardAliasDao.getAllAliases()

    fun addAlias(cardId: String, alias: String) {
        viewModelScope.launch {
            cardAliasDao.insertAlias(CardAlias(cardId, alias))
        }
    }

    suspend fun getAlias(cardId: String): CardAlias? {
        return cardAliasDao.getAlias(cardId)
    }

    fun deleteAlias(cardAlias: CardAlias) {
        viewModelScope.launch {
            cardAliasDao.deleteAlias(cardAlias)
        }
    }

    fun clearAllAliases() {
        viewModelScope.launch {
            cardAliasDao.deleteAllAliases()
        }
    }
}

class CardAliasViewModelFactory(private val cardAliasDao: CardAliasDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardAliasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardAliasViewModel(cardAliasDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
