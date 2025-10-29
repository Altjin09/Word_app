package com.example.words.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// extension
val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

class SettingsRepository(private val context: Context) {

    // 1 = Англи -> Монгол асууна
    // 2 = Монгол -> Англи асууна
    // 3 = Хоёуланг нь асууна (random тал)
    private val MODE_KEY = intPreferencesKey("question_mode")

    val modeFlow: Flow<Int> = context.settingsDataStore.data.map { prefs ->
        prefs[MODE_KEY] ?: 1
    }

    suspend fun setMode(mode: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[MODE_KEY] = mode
        }
    }
}
