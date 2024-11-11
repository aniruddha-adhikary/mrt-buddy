package net.adhikary.mrtbuddy.repository

import kotlinx.coroutines.flow.Flow
import net.adhikary.mrtbuddy.dao.DemoDao
import net.adhikary.mrtbuddy.data.DemoLocal

class DemoRepository(private val demoDao: DemoDao) {
    suspend fun insert(item: DemoLocal) = demoDao.insert(item)
    suspend fun count(): Int = demoDao.count()
    fun getAllAsFlow(): Flow<List<DemoLocal>> = demoDao.getAllAsFlow()
    suspend fun getAll(): List<DemoLocal> = demoDao.getAll()
}
