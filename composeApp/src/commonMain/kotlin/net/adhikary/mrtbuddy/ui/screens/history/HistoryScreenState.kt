package net.adhikary.mrtbuddy.ui.screens.history

import net.adhikary.mrtbuddy.data.CardEntity

data class HistoryScreenState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val cards: List<CardWithBalance> = emptyList(),
    val error: String? = null,
    val lastUpdated: Long? = null,
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.LAST_USED,
    val selectedCard: String? = null,
    val showQuickActions: Boolean = false
)

data class CardWithBalance(
    val card: CardEntity,
    val balance: Int?,
    val isLowBalance: Boolean = false,
    val daysSinceLastScan: Int? = null,
    val isExpiringSoon: Boolean = false
)

enum class SortOrder {
    LAST_USED,
    ALPHABETICAL,
    BALANCE_HIGH_TO_LOW,
    BALANCE_LOW_TO_HIGH
}

sealed class CardStatus {
    object Active : CardStatus()
    object LowBalance : CardStatus()
    object Inactive : CardStatus()
    object ExpiringSoon : CardStatus()
}
