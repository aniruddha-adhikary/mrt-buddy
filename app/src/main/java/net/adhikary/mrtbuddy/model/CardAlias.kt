package net.adhikary.mrtbuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_aliases")
data class CardAlias(
    @PrimaryKey
    val cardId: String,
    var alias: String
)
