package net.adhikary.mrtbuddy.ui.screens.history

import net.adhikary.mrtbuddy.data.CardEntity

data class HistoryScreenState(
    val isLoading: Boolean = false,
    val cards: List<CardWithBalance> = emptyList(),
    val error: String? = null
)

data class CardWithBalance(
    val card: CardEntity,
    val balance: Int?
)
