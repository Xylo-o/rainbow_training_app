package com.rainbow.training.core.data

import com.rainbow.training.core.model.TrainingSettings
import kotlinx.coroutines.flow.Flow

/**
 * Zrodlo ustawien treningu. Interfejs, a nie konkretna klasa, zeby dalo sie podmienic
 * implementacje (np. na taka synchronizowana z kontem uzytkownika) bez ruszania UI.
 */
interface SettingsRepository {

    /** Aktualne ustawienia; emituje ponownie po kazdym zapisie. */
    val settings: Flow<TrainingSettings>

    suspend fun save(settings: TrainingSettings)
}
