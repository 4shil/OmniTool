package com.omnitool.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val toolId: String,
    val order: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_tools")
data class RecentToolEntity(
    @PrimaryKey
    val toolId: String,
    val lastUsedAt: Long = System.currentTimeMillis(),
    val useCount: Int = 1
)
