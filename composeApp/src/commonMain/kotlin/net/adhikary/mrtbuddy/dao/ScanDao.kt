package net.adhikary.mrtbuddy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.adhikary.mrtbuddy.data.ScanEntity

@Dao
interface ScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity): Long

    @Query("SELECT * FROM scans WHERE cardIdm = :cardIdm")
    suspend fun getScansByCardIdm(cardIdm: String): List<ScanEntity>

    @Query("DELETE FROM scans WHERE cardIdm = :cardIdm")
    suspend fun deleteScansByCardIdm(cardIdm: String)
}
