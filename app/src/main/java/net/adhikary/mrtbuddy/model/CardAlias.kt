package net.adhikary.mrtbuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_aliases")
data class CardAlias(
    @PrimaryKey
    val cardSerialNumber: String,
    val alias: String,
    val lastBalance: Double? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    val lowBalanceThreshold: Double? = null,
    val notificationsEnabled: Boolean = false
)
