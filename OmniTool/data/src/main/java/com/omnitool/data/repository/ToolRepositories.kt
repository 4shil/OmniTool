package com.omnitool.data.repository

import com.omnitool.data.local.dao.FavoriteDao
import com.omnitool.data.local.dao.RecentToolDao
import com.omnitool.data.local.entity.FavoriteEntity
import com.omnitool.data.local.entity.RecentToolEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Favorites management
 */
@Singleton
class FavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    
    fun getAllFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()
    
    suspend fun isFavorite(toolId: String): Boolean = favoriteDao.isFavorite(toolId)
    
    suspend fun addFavorite(toolId: String, toolName: String, toolIcon: String, accentColor: String) {
        val favorite = FavoriteEntity(
            toolId = toolId,
            toolName = toolName,
            toolIcon = toolIcon,
            accentColor = accentColor,
            addedAt = System.currentTimeMillis()
        )
        favoriteDao.insertFavorite(favorite)
    }
    
    suspend fun removeFavorite(toolId: String) {
        favoriteDao.deleteFavorite(toolId)
    }
    
    suspend fun toggleFavorite(toolId: String, toolName: String, toolIcon: String, accentColor: String) {
        if (isFavorite(toolId)) {
            removeFavorite(toolId)
        } else {
            addFavorite(toolId, toolName, toolIcon, accentColor)
        }
    }
}

/**
 * Repository for Recent Tools tracking
 */
@Singleton
class RecentToolsRepository @Inject constructor(
    private val recentToolDao: RecentToolDao
) {
    
    fun getRecentTools(limit: Int = 10): Flow<List<RecentToolEntity>> = 
        recentToolDao.getRecentTools(limit)
    
    suspend fun recordToolUsage(toolId: String, toolName: String, toolIcon: String, accentColor: String) {
        val entity = RecentToolEntity(
            toolId = toolId,
            toolName = toolName,
            toolIcon = toolIcon,
            accentColor = accentColor,
            lastUsedAt = System.currentTimeMillis(),
            usageCount = 1 // Will be incremented by REPLACE strategy
        )
        recentToolDao.insertOrUpdate(entity)
    }
    
    suspend fun clearHistory() {
        recentToolDao.clearAll()
    }
}
