package com.omnitool.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "omnitool_preferences")

/**
 * User Preferences Manager using DataStore
 * Handles all app settings and preferences
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private object PreferenceKeys {
        // Appearance
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val FONT_SIZE = stringPreferencesKey("font_size")
        
        // Behavior
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val AUTO_COPY = booleanPreferencesKey("auto_copy")
        val CONFIRM_CLEAR = booleanPreferencesKey("confirm_clear")
        
        // Privacy
        val BIOMETRIC_VAULT = booleanPreferencesKey("biometric_vault")
        val CLIPBOARD_HISTORY_ENABLED = booleanPreferencesKey("clipboard_history_enabled")
        val CLIPBOARD_HISTORY_LIMIT = intPreferencesKey("clipboard_history_limit")
        
        // App State
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LAST_USED_TOOL = stringPreferencesKey("last_used_tool")
        val APP_OPEN_COUNT = intPreferencesKey("app_open_count")
        
        // Pro Features
        val IS_PRO_USER = booleanPreferencesKey("is_pro_user")
    }
    
    val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                themeMode = ThemeMode.valueOf(
                    preferences[PreferenceKeys.THEME_MODE] ?: ThemeMode.DARK.name
                ),
                dynamicColors = preferences[PreferenceKeys.DYNAMIC_COLORS] ?: false,
                fontSize = FontSize.valueOf(
                    preferences[PreferenceKeys.FONT_SIZE] ?: FontSize.MEDIUM.name
                ),
                hapticFeedback = preferences[PreferenceKeys.HAPTIC_FEEDBACK] ?: true,
                soundEffects = preferences[PreferenceKeys.SOUND_EFFECTS] ?: false,
                autoCopy = preferences[PreferenceKeys.AUTO_COPY] ?: false,
                confirmClear = preferences[PreferenceKeys.CONFIRM_CLEAR] ?: true,
                biometricVault = preferences[PreferenceKeys.BIOMETRIC_VAULT] ?: true,
                clipboardHistoryEnabled = preferences[PreferenceKeys.CLIPBOARD_HISTORY_ENABLED] ?: true,
                clipboardHistoryLimit = preferences[PreferenceKeys.CLIPBOARD_HISTORY_LIMIT] ?: 100,
                onboardingCompleted = preferences[PreferenceKeys.ONBOARDING_COMPLETED] ?: false,
                lastUsedTool = preferences[PreferenceKeys.LAST_USED_TOOL],
                appOpenCount = preferences[PreferenceKeys.APP_OPEN_COUNT] ?: 0,
                isProUser = preferences[PreferenceKeys.IS_PRO_USER] ?: false
            )
        }
    
    // Theme settings
    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferenceKeys.THEME_MODE] = mode.name }
    }
    
    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.DYNAMIC_COLORS] = enabled }
    }
    
    suspend fun setFontSize(size: FontSize) {
        context.dataStore.edit { it[PreferenceKeys.FONT_SIZE] = size.name }
    }
    
    // Behavior settings
    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.HAPTIC_FEEDBACK] = enabled }
    }
    
    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.SOUND_EFFECTS] = enabled }
    }
    
    suspend fun setAutoCopy(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.AUTO_COPY] = enabled }
    }
    
    suspend fun setConfirmClear(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.CONFIRM_CLEAR] = enabled }
    }
    
    // Privacy settings
    suspend fun setBiometricVault(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.BIOMETRIC_VAULT] = enabled }
    }
    
    suspend fun setClipboardHistoryEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.CLIPBOARD_HISTORY_ENABLED] = enabled }
    }
    
    suspend fun setClipboardHistoryLimit(limit: Int) {
        context.dataStore.edit { it[PreferenceKeys.CLIPBOARD_HISTORY_LIMIT] = limit }
    }
    
    // App state
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.ONBOARDING_COMPLETED] = completed }
    }
    
    suspend fun setLastUsedTool(toolRoute: String) {
        context.dataStore.edit { it[PreferenceKeys.LAST_USED_TOOL] = toolRoute }
    }
    
    suspend fun incrementAppOpenCount() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferenceKeys.APP_OPEN_COUNT] ?: 0
            preferences[PreferenceKeys.APP_OPEN_COUNT] = current + 1
        }
    }
    
    // Pro features
    suspend fun setProUser(isPro: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.IS_PRO_USER] = isPro }
    }
}

/**
 * User preferences data class
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val dynamicColors: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val hapticFeedback: Boolean = true,
    val soundEffects: Boolean = false,
    val autoCopy: Boolean = false,
    val confirmClear: Boolean = true,
    val biometricVault: Boolean = true,
    val clipboardHistoryEnabled: Boolean = true,
    val clipboardHistoryLimit: Int = 100,
    val onboardingCompleted: Boolean = false,
    val lastUsedTool: String? = null,
    val appOpenCount: Int = 0,
    val isProUser: Boolean = false
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class FontSize {
    SMALL, MEDIUM, LARGE
}
