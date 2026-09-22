package com.rainbow.training.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rainbow.training.AppContainer
import com.rainbow.training.core.data.SettingsRepository
import com.rainbow.training.core.engine.SessionEngine
import com.rainbow.training.core.engine.SessionEvent
import com.rainbow.training.core.model.TrainingSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Trzyma ustawienia edytowane przez uzytkownika i steruje przebiegiem sesji.
 *
 * Sesja zyje w [viewModelScope], wiec przezywa obrot ekranu, a jest sprzatana razem z ekranem.
 */
class TrainingViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _settings = MutableStateFlow<TrainingSettings?>(null)

    /** `null` dopoki ustawienia nie zostana wczytane z dysku. */
    val settings: StateFlow<TrainingSettings?> = _settings.asStateFlow()

    private val _phase = MutableStateFlow<SessionPhase>(SessionPhase.Idle)
    val phase: StateFlow<SessionPhase> = _phase.asStateFlow()

    private val _events = MutableSharedFlow<TrainingEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<TrainingEvent> = _events.asSharedFlow()

    private var sessionJob: Job? = null

    init {
        viewModelScope.launch {
            _settings.value = settingsRepository.settings.first()
        }
    }

    /** Zmiana ustawien z ekranu konfiguracji; zapis na dysk nastepuje przy starcie sesji. */
    fun updateSettings(transform: (TrainingSettings) -> TrainingSettings) {
        val current = _settings.value ?: return
        _settings.value = transform(current)
    }

    /** Startuje sesje, o ile ustawienia sa poprawne. */
    fun startSession() {
        val settings = _settings.value?.takeIf { it.isValid }?.sanitized() ?: return
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            settingsRepository.save(settings)
            var startedAt = 0L
            SessionEngine(settings).run().collect { event ->
                when (event) {
                    is SessionEvent.Countdown ->
                        _phase.value = SessionPhase.Countdown(event.secondsLeft)

                    is SessionEvent.Show -> {
                        if (startedAt == 0L) startedAt = monotonicNowMillis() - event.elapsedMillis
                        _phase.value = SessionPhase.Running(
                            stimulus = event.stimulus,
                            startedAtMillis = startedAt,
                            totalMillis = event.totalMillis,
                        )
                    }

                    is SessionEvent.Finished -> {
                        _phase.value = SessionPhase.Finished(
                            durationMillis = event.durationMillis,
                            stimulusCount = event.stimulusCount,
                        )
                        _events.tryEmit(TrainingEvent.SessionFinished)
                    }
                }
            }
        }
    }

    /** Przerwanie sesji w trakcie (przycisk "wstecz") - wracamy do ustawien. */
    fun abortSession() {
        sessionJob?.cancel()
        sessionJob = null
        _phase.value = SessionPhase.Idle
    }

    /** Z ekranu podsumowania: "Powtórz". */
    fun repeatSession() = startSession()

    /** Z ekranu podsumowania: "Zmień ustawienia". */
    fun backToSetup() {
        _phase.value = SessionPhase.Idle
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { TrainingViewModel(container.settingsRepository) }
        }
    }
}
