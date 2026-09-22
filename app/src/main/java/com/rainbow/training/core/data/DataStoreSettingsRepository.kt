package com.rainbow.training.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rainbow.training.core.model.StimulusColor
import com.rainbow.training.core.model.StimulusMode
import com.rainbow.training.core.model.TrainingSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "training_settings",
)

/** Zapis ustawien w Jetpack DataStore - przezywa restart aplikacji. */
class DataStoreSettingsRepository(context: Context) : SettingsRepository {

    private val dataStore = context.applicationContext.settingsDataStore

    override val settings: Flow<TrainingSettings> = dataStore.data
        .catch { throwable ->
            // Uszkodzony lub niedostepny plik nie moze wywrocic aplikacji - wracamy do domyslnych.
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences -> preferences.toSettings() }

    override suspend fun save(settings: TrainingSettings) {
        val sanitized = settings.sanitized()
        dataStore.edit { preferences ->
            preferences[Keys.MODE] = sanitized.mode.id
            preferences[Keys.MIN_INTERVAL] = sanitized.minIntervalMillis
            preferences[Keys.MAX_INTERVAL] = sanitized.maxIntervalMillis
            preferences[Keys.DURATION] = sanitized.sessionDurationMillis
            preferences[Keys.COLORS] = sanitized.enabledColors.map { it.id }.toSet()
            preferences[Keys.DIGITS] = sanitized.enabledDigits.map { it.toString() }.toSet()
        }
    }

    private fun Preferences.toSettings(): TrainingSettings {
        val defaults = TrainingSettings()
        return TrainingSettings(
            mode = this[Keys.MODE]?.let(StimulusMode::fromId) ?: defaults.mode,
            minIntervalMillis = this[Keys.MIN_INTERVAL] ?: defaults.minIntervalMillis,
            maxIntervalMillis = this[Keys.MAX_INTERVAL] ?: defaults.maxIntervalMillis,
            sessionDurationMillis = this[Keys.DURATION] ?: defaults.sessionDurationMillis,
            enabledColors = this[Keys.COLORS]
                ?.mapNotNull(StimulusColor::fromId)
                ?.toSet()
                ?: defaults.enabledColors,
            enabledDigits = this[Keys.DIGITS]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: defaults.enabledDigits,
        ).sanitized()
    }

    private object Keys {
        val MODE = stringPreferencesKey("mode")
        val MIN_INTERVAL = longPreferencesKey("min_interval_ms")
        val MAX_INTERVAL = longPreferencesKey("max_interval_ms")
        val DURATION = longPreferencesKey("session_duration_ms")
        val COLORS = stringSetPreferencesKey("enabled_colors")
        val DIGITS = stringSetPreferencesKey("enabled_digits")
    }
}
