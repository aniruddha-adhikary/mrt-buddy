package net.adhikary.mrtbuddy.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.adhikary.mrtbuddy.data.DemoLocal

@Dao
interface DemoDao {
    @Insert
    suspend fun insert(item: DemoLocal)

    @Query("SELECT count(*) FROM DemoLocal")
    suspend fun count(): Int

    @Query("SELECT * FROM DemoLocal")
     fun getAllAsFlow(): Flow<List<DemoLocal>> // we can do flow for larger read of data

    @Query("SELECT * FROM DemoLocal")
    suspend  fun getAll(): List<DemoLocal>
}