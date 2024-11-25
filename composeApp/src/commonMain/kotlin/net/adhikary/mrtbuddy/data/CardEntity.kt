package net.adhikary.mrtbuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val idm: String,
    val name: String?,
    val lastScanTime: Long? = null
)
