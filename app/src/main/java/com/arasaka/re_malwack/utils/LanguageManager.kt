package com.arasaka.re_malwack.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

class LanguageManager(private val context: Context) {
    
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
        private const val PREFS_NAME = "language_settings"
        private const val PREFS_KEY = "selected_language"
        const val LANGUAGE_SYSTEM = "system"
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_GERMAN = "de"
        const val LANGUAGE_POLISH = "pl"
    }
    
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    val selectedLanguage: Flow<String> = context.languageDataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: LANGUAGE_SYSTEM
        }
    
    suspend fun setLanguage(languageCode: String) {
        context.languageDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
        
        sharedPrefs.edit().putString(PREFS_KEY, languageCode).apply()
    }
    
    fun getLanguageSync(): String {
        return sharedPrefs.getString(PREFS_KEY, LANGUAGE_SYSTEM) ?: LANGUAGE_SYSTEM
    }
    
    fun getAvailableLanguages(): List<Language> {
        return listOf(
            Language(LANGUAGE_SYSTEM, "System", "🌐"),
            Language(LANGUAGE_ENGLISH, "English", "🇺🇸"),
            Language(LANGUAGE_GERMAN, "Deutsch", "🇩🇪"),
            Language(LANGUAGE_POLISH, "Polski", "🇵🇱")
        )
    }
    
    fun updateContextLocale(context: Context, languageCode: String): Context {
        if (languageCode == LANGUAGE_SYSTEM) {
            return context
        }
        
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        return context.createConfigurationContext(configuration)
    }
}

data class Language(
    val code: String,
    val name: String,
    val flag: String
) 