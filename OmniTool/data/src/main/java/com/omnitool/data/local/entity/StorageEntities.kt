package com.omnitool.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Clipboard history entry entity
 */
@Entity(tableName = "clipboard_history")
data class ClipboardEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val timestamp: Long,
    val isPinned: Boolean = false
)

/**
 * Encrypted vault item entity
 */
@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val encryptedContent: String,
    val category: String,
    val createdAt: Long,
    val updatedAt: Long
)
