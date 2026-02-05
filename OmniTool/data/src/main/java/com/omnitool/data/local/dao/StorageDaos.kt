package com.omnitool.data.local.dao

import androidx.room.*
import com.omnitool.data.local.entity.ClipboardEntity
import com.omnitool.data.local.entity.VaultItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Clipboard History DAO
 */
@Dao
interface ClipboardDao {
    
    @Query("SELECT * FROM clipboard_history ORDER BY isPinned DESC, timestamp DESC")
    fun getAllClipboardEntries(): Flow<List<ClipboardEntity>>
    
    @Query("SELECT * FROM clipboard_history WHERE content LIKE '%' || :query || '%' ORDER BY isPinned DESC, timestamp DESC")
    fun searchClipboard(query: String): Flow<List<ClipboardEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: ClipboardEntity)
    
    @Update
    suspend fun updateEntry(entry: ClipboardEntity)
    
    @Delete
    suspend fun deleteEntry(entry: ClipboardEntity)
    
    @Query("DELETE FROM clipboard_history WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM clipboard_history WHERE isPinned = 0")
    suspend fun clearUnpinned()
    
    @Query("DELETE FROM clipboard_history")
    suspend fun clearAll()
    
    @Query("SELECT COUNT(*) FROM clipboard_history")
    suspend fun getCount(): Int
}

/**
 * Vault DAO for encrypted items
 */
@Dao
interface VaultDao {
    
    @Query("SELECT * FROM vault_items ORDER BY updatedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItemEntity>>
    
    @Query("SELECT * FROM vault_items WHERE category = :category ORDER BY updatedAt DESC")
    fun getItemsByCategory(category: String): Flow<List<VaultItemEntity>>
    
    @Query("SELECT * FROM vault_items WHERE id = :id")
    suspend fun getItemById(id: String): VaultItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VaultItemEntity)
    
    @Update
    suspend fun updateItem(item: VaultItemEntity)
    
    @Delete
    suspend fun deleteItem(item: VaultItemEntity)
    
    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("DELETE FROM vault_items")
    suspend fun clearAll()
}
