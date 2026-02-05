package com.omnitool.data.repository

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.omnitool.data.local.dao.VaultDao
import com.omnitool.data.local.entity.VaultItemEntity
import kotlinx.coroutines.flow.Flow
import java.security.KeyStore
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Base64

/**
 * Repository for Encrypted Vault operations
 * Uses Android KeyStore for secure encryption
 */
@Singleton
class VaultRepository @Inject constructor(
    private val vaultDao: VaultDao
) {
    
    companion object {
        private const val KEYSTORE_ALIAS = "omnitool_vault_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
    }
    
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }
    
    fun getAllItems(): Flow<List<VaultItemEntity>> = vaultDao.getAllVaultItems()
    
    fun getItemsByCategory(category: String): Flow<List<VaultItemEntity>> = 
        vaultDao.getItemsByCategory(category)
    
    suspend fun addItem(title: String, content: String, category: String): Boolean {
        return try {
            val encryptedContent = encrypt(content)
            val item = VaultItemEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                encryptedContent = encryptedContent,
                category = category,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            vaultDao.insertItem(item)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getDecryptedContent(id: String): String? {
        return try {
            val item = vaultDao.getItemById(id) ?: return null
            decrypt(item.encryptedContent)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateItem(id: String, title: String, content: String, category: String): Boolean {
        return try {
            val encryptedContent = encrypt(content)
            val existing = vaultDao.getItemById(id) ?: return false
            val updated = existing.copy(
                title = title,
                encryptedContent = encryptedContent,
                category = category,
                updatedAt = System.currentTimeMillis()
            )
            vaultDao.updateItem(updated)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteItem(id: String) {
        vaultDao.deleteById(id)
    }
    
    suspend fun clearAll() {
        vaultDao.clearAll()
    }
    
    // Encryption helpers
    private fun getOrCreateSecretKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }
        
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keySpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // Set to true for biometric
            .build()
        
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }
    
    private fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        
        // Combine IV + encrypted data
        val combined = iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }
    
    private fun decrypt(encryptedData: String): String {
        val combined = Base64.decode(encryptedData, Base64.NO_WRAP)
        
        // Extract IV and encrypted bytes
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encryptedBytes = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
        
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
