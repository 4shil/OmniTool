package com.omnitool.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omnitool.data.local.dao.ClipboardDao
import com.omnitool.data.local.dao.FavoriteDao
import com.omnitool.data.local.dao.RecentToolDao
import com.omnitool.data.local.dao.VaultDao
import com.omnitool.data.local.entity.ClipboardEntity
import com.omnitool.data.local.entity.FavoriteEntity
import com.omnitool.data.local.entity.RecentToolEntity
import com.omnitool.data.local.entity.VaultItemEntity

/**
 * OmniTool Room Database
 */
@Database(
    entities = [
        FavoriteEntity::class,
        RecentToolEntity::class,
        ClipboardEntity::class,
        VaultItemEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class OmniToolDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentToolDao(): RecentToolDao
    abstract fun clipboardDao(): ClipboardDao
    abstract fun vaultDao(): VaultDao
    
    companion object {
        const val DATABASE_NAME = "omnitool_db"
    }
}

