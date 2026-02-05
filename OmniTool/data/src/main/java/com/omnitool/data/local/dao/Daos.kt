package com.omnitool.data.local.dao

import androidx.room.*
import com.omnitool.data.local.entity.FavoriteEntity
import com.omnitool.data.local.entity.RecentToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY `order` ASC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE toolId = :toolId")
    suspend fun deleteFavoriteById(toolId: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE toolId = :toolId)")
    suspend fun isFavorite(toolId: String): Boolean
    
    @Query("UPDATE favorites SET `order` = :newOrder WHERE toolId = :toolId")
    suspend fun updateOrder(toolId: String, newOrder: Int)
}

@Dao
interface RecentToolDao {
    @Query("SELECT * FROM recent_tools ORDER BY lastUsedAt DESC LIMIT 8")
    fun getRecentTools(): Flow<List<RecentToolEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tool: RecentToolEntity)
    
    @Query("UPDATE recent_tools SET lastUsedAt = :timestamp, useCount = useCount + 1 WHERE toolId = :toolId")
    suspend fun updateUsage(toolId: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT * FROM recent_tools WHERE toolId = :toolId")
    suspend fun getByToolId(toolId: String): RecentToolEntity?
    
    @Query("DELETE FROM recent_tools")
    suspend fun clearAll()
}
