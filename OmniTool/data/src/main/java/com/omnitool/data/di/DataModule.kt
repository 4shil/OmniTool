package com.omnitool.data.di

import android.content.Context
import androidx.room.Room
import com.omnitool.data.local.OmniToolDatabase
import com.omnitool.data.local.dao.ClipboardDao
import com.omnitool.data.local.dao.FavoriteDao
import com.omnitool.data.local.dao.RecentToolDao
import com.omnitool.data.local.dao.VaultDao
import com.omnitool.data.preferences.UserPreferencesManager
import com.omnitool.data.repository.ClipboardRepository
import com.omnitool.data.repository.FavoritesRepository
import com.omnitool.data.repository.RecentToolsRepository
import com.omnitool.data.repository.VaultRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI Module for Data Layer
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    // Database
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): OmniToolDatabase {
        return Room.databaseBuilder(
            context,
            OmniToolDatabase::class.java,
            OmniToolDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    // DAOs
    @Provides
    @Singleton
    fun provideFavoriteDao(database: OmniToolDatabase): FavoriteDao {
        return database.favoriteDao()
    }
    
    @Provides
    @Singleton
    fun provideRecentToolDao(database: OmniToolDatabase): RecentToolDao {
        return database.recentToolDao()
    }
    
    @Provides
    @Singleton
    fun provideClipboardDao(database: OmniToolDatabase): ClipboardDao {
        return database.clipboardDao()
    }
    
    @Provides
    @Singleton
    fun provideVaultDao(database: OmniToolDatabase): VaultDao {
        return database.vaultDao()
    }
    
    // Preferences
    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        @ApplicationContext context: Context
    ): UserPreferencesManager {
        return UserPreferencesManager(context)
    }
    
    // Repositories
    @Provides
    @Singleton
    fun provideFavoritesRepository(favoriteDao: FavoriteDao): FavoritesRepository {
        return FavoritesRepository(favoriteDao)
    }
    
    @Provides
    @Singleton
    fun provideRecentToolsRepository(recentToolDao: RecentToolDao): RecentToolsRepository {
        return RecentToolsRepository(recentToolDao)
    }
    
    @Provides
    @Singleton
    fun provideClipboardRepository(clipboardDao: ClipboardDao): ClipboardRepository {
        return ClipboardRepository(clipboardDao)
    }
    
    @Provides
    @Singleton
    fun provideVaultRepository(vaultDao: VaultDao): VaultRepository {
        return VaultRepository(vaultDao)
    }
}
