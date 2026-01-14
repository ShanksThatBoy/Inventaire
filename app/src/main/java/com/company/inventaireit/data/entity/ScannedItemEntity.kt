package com.company.inventaireit.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scanned_items",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId", "codeValue"])]
)
data class ScannedItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val codeValue: String,
    val scannedAt: Long = System.currentTimeMillis(),
    val site: String,
    val floor: String,
    val room: String,
    val operatorName: String,
    val note: String? = null,
    val isDuplicate: Boolean = false
)