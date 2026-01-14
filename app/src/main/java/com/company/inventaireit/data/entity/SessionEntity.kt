package com.company.inventaireit.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val site: String,
    val operatorName: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val status: String = "IN_PROGRESS" // IN_PROGRESS, DONE
)