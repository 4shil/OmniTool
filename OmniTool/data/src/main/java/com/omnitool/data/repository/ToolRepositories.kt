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
    
    suspend fun addFavorite(toolId: String, order: Int) {
        val favorite = FavoriteEntity(
            toolId = toolId,
            order = order,
            addedAt = System.currentTimeMillis()
        )
        favoriteDao.insertFavorite(favorite)
    }
    
    suspend fun removeFavorite(toolId: String) {
        favoriteDao.deleteFavoriteById(toolId)
    }
    
    suspend fun toggleFavorite(toolId: String, order: Int) {
        if (isFavorite(toolId)) {
            removeFavorite(toolId)
        } else {
            addFavorite(toolId, order)
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
    
    fun getRecentTools(): Flow<List<RecentToolEntity>> = 
        recentToolDao.getRecentTools()
    
    suspend fun recordToolUsage(toolId: String) {
        val entity = RecentToolEntity(
            toolId = toolId,
            lastUsedAt = System.currentTimeMillis(),
            useCount = 1
        )
        recentToolDao.insertOrUpdate(entity)
    }
    
    suspend fun clearHistory() {
        recentToolDao.clearAll()
    }
}
