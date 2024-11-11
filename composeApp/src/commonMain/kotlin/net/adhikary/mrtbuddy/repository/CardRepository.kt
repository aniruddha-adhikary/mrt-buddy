package net.adhikary.mrtbuddy.repository

import net.adhikary.mrtbuddy.dao.CardDao
import net.adhikary.mrtbuddy.data.CardEntity

class CardRepository(private val cardDao: CardDao) {
    suspend fun insertCard(card: CardEntity) = cardDao.insertCard(card)
    suspend fun getCardByIdm(idm: String): CardEntity? = cardDao.getCardByIdm(idm)
}
