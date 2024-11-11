package net.adhikary.mrtbuddy.repository

import net.adhikary.mrtbuddy.dao.ScanDao
import net.adhikary.mrtbuddy.data.ScanEntity

class ScanRepository(private val scanDao: ScanDao) {
    suspend fun insertScan(scan: ScanEntity): Long = scanDao.insertScan(scan)
    suspend fun getScansByCardIdm(cardIdm: String): List<ScanEntity> = scanDao.getScansByCardIdm(cardIdm)
}
