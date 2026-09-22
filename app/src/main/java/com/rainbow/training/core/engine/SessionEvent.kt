package com.rainbow.training.core.engine

import com.rainbow.training.core.model.Stimulus

/** Zdarzenia emitowane przez [SessionEngine] w trakcie jednej sesji treningowej. */
sealed interface SessionEvent {

    /** Odliczanie przed startem; [secondsLeft] idzie 3, 2, 1. */
    data class Countdown(val secondsLeft: Int) : SessionEvent

    /** Pokaz [stimulus]; sesja trwa juz [elapsedMillis] z [totalMillis]. */
    data class Show(
        val stimulus: Stimulus,
        val elapsedMillis: Long,
        val totalMillis: Long,
    ) : SessionEvent

    /** Koniec sesji. */
    data class Finished(
        val durationMillis: Long,
        val stimulusCount: Int,
    ) : SessionEvent
}
