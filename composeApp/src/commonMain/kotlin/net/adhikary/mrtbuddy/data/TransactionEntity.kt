package net.adhikary.mrtbuddy.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "transactions",
    primaryKeys = ["cardIdm", "fromStation", "toStation", "balance", "dateTime", "fixedHeader"],
    foreignKeys = [
        ForeignKey(
            entity = ScanEntity::class,
            parentColumns = ["scanId"],
            childColumns = ["scanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scanId"]),
        Index(value = ["order"]),
        // Optimized index for pagination queries (combined index)
        Index(value = ["cardIdm", "order"], name = "idx_cardIdm_order"),
        // Index for date-based queries
        Index(value = ["cardIdm", "dateTime"], name = "idx_cardIdm_dateTime")
    ]
)
data class TransactionEntity(
    val cardIdm: String,
    val scanId: Long,
    val fromStation: String,
    val toStation: String,
    val balance: Int,
    val dateTime: Long,
    val fixedHeader: String,
    val order: Int = 0
)

data class TransactionEntityWithAmount(
    val transactionEntity: TransactionEntity,
    val amount: Int?
)
