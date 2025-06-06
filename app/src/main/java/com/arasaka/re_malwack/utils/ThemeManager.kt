package com.arasaka.re_malwack.utils

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arasaka.re_malwack.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

class ThemeManager(private val context: Context) {
    
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    
    val selectedTheme: Flow<ThemeMode> = context.themeDataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_MODE_KEY] ?: ThemeMode.LIGHT.name
            try {
                ThemeMode.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemeMode.LIGHT
            }
        }
    
    suspend fun setTheme(themeMode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }
    
    fun isMonetSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
    
    fun getAvailableThemes(): List<ThemeMode> {
        return if (isMonetSupported()) {
            listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.MONET, ThemeMode.CUSTOM)
        } else {
            listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.CUSTOM)
        }
    }
} 