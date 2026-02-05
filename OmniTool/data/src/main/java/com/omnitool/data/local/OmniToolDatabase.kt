package com.omnitool.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omnitool.data.local.dao.FavoriteDao
import com.omnitool.data.local.dao.RecentToolDao
import com.omnitool.data.local.entity.FavoriteEntity
import com.omnitool.data.local.entity.RecentToolEntity

/**
 * OmniTool Room Database
 */
@Database(
    entities = [
        FavoriteEntity::class,
        RecentToolEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class OmniToolDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentToolDao(): RecentToolDao
    
    companion object {
        const val DATABASE_NAME = "omnitool_db"
    }
}
