package com.company.inventaireit.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.company.inventaireit.data.entity.ScannedItemEntity
import com.company.inventaireit.data.entity.SessionEntity

@Dao
interface InventoryDao {

    // --- SESSIONS ---
    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): LiveData<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): SessionEntity?

    @Query("SELECT * FROM sessions WHERE status = 'IN_PROGRESS' LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    // --- ITEMS ---
    @Insert
    suspend fun insertItem(item: ScannedItemEntity): Long

    @Delete
    suspend fun deleteItem(item: ScannedItemEntity)

    @Query("SELECT * FROM scanned_items WHERE sessionId = :sessionId ORDER BY scannedAt DESC")
    fun getItemsBySession(sessionId: Long): LiveData<List<ScannedItemEntity>>

    @Query("SELECT * FROM scanned_items WHERE sessionId = :sessionId AND codeValue = :code LIMIT 1")
    suspend fun findItemByCode(sessionId: Long, code: String): ScannedItemEntity?

    @Query("SELECT COUNT(*) FROM scanned_items WHERE sessionId = :sessionId")
    fun getItemCount(sessionId: Long): LiveData<Int>

    @Query("DELETE FROM scanned_items WHERE sessionId = :sessionId")
    suspend fun deleteAllItemsInSession(sessionId: Long)
}