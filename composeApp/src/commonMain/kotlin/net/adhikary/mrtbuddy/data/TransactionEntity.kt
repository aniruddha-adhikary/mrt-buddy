package net.adhikary.mrtbuddy.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "transactions",
    primaryKeys = ["cardIdm", "transactionId", "dateTime"],
    foreignKeys = [
        ForeignKey(
            entity = ScanEntity::class,
            parentColumns = ["scanId"],
            childColumns = ["scanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["scanId"])]
)
data class TransactionEntity(
    val cardIdm: String,
    val transactionId: String,
    val scanId: Long,
    val fromStation: String,
    val toStation: String,
    val balance: Int,
    val dateTime: Long
)
