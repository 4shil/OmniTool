package com.omnitool.data.repository

import com.omnitool.data.local.dao.ClipboardDao
import com.omnitool.data.local.entity.ClipboardEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Clipboard History operations
 */
@Singleton
class ClipboardRepository @Inject constructor(
    private val clipboardDao: ClipboardDao
) {
    
    fun getAllEntries(): Flow<List<ClipboardEntity>> = clipboardDao.getAllClipboardEntries()
    
    fun searchEntries(query: String): Flow<List<ClipboardEntity>> = clipboardDao.searchClipboard(query)
    
    suspend fun addEntry(content: String) {
        if (content.isBlank()) return
        
        val entry = ClipboardEntity(
            id = UUID.randomUUID().toString(),
            content = content,
            timestamp = System.currentTimeMillis(),
            isPinned = false
        )
        clipboardDao.insertEntry(entry)
    }
    
    suspend fun togglePin(entry: ClipboardEntity) {
        clipboardDao.updateEntry(entry.copy(isPinned = !entry.isPinned))
    }
    
    suspend fun deleteEntry(id: String) {
        clipboardDao.deleteById(id)
    }
    
    suspend fun clearUnpinned() {
        clipboardDao.clearUnpinned()
    }
    
    suspend fun clearAll() {
        clipboardDao.clearAll()
    }
}
