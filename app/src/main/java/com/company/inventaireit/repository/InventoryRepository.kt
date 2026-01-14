package com.company.inventaireit.repository

import androidx.lifecycle.LiveData
import com.company.inventaireit.data.dao.InventoryDao
import com.company.inventaireit.data.entity.ScannedItemEntity
import com.company.inventaireit.data.entity.SessionEntity

class InventoryRepository(private val inventoryDao: InventoryDao) {

    val allSessions: LiveData<List<SessionEntity>> = inventoryDao.getAllSessions()

    suspend fun createSession(name: String, site: String, operator: String): Long {
        val session = SessionEntity(name = name, site = site, operatorName = operator)
        return inventoryDao.insertSession(session)
    }

    suspend fun getActiveSession(): SessionEntity? = inventoryDao.getActiveSession()

    fun getItemsForSession(sessionId: Long): LiveData<List<ScannedItemEntity>> =
        inventoryDao.getItemsBySession(sessionId)

    suspend fun insertItem(item: ScannedItemEntity) {
        val existing = inventoryDao.findItemByCode(item.sessionId, item.codeValue)
        val itemToInsert = if (existing != null) {
            item.copy(isDuplicate = true)
        } else {
            item
        }
        inventoryDao.insertItem(itemToInsert)
    }

    suspend fun updateSession(session: SessionEntity) {
        inventoryDao.updateSession(session)
    }
}