package net.adhikary.mrtbuddy.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(
    tableName = "scans",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["idm"],
            childColumns = ["cardIdm"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardIdm"])]
)
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val scanId: Long = 0,
    val cardIdm: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)
