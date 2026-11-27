package com.yesmin.ai

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "yesmin_settings")

class SettingsStore(private val context: Context) {
    private val apiKeyKey = stringPreferencesKey("gemini_api_key")
    private val modelKey = stringPreferencesKey("gemini_model")
    suspend fun getApiKey(): String = context.dataStore.data.first()[apiKeyKey] ?: ""
    suspend fun getModel(): String = context.dataStore.data.first()[modelKey] ?: "gemini-2.5-flash"
    suspend fun save(key: String, model: String) {
        context.dataStore.edit {
            it[apiKeyKey] = key
            it[modelKey] = model
        }
    }
}
