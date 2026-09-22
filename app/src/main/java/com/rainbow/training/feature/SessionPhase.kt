package com.rainbow.training.feature

import com.rainbow.training.core.model.Stimulus

/** Stan, w ktorym znajduje sie aplikacja: ustawienia, odliczanie, sesja albo podsumowanie. */
sealed interface SessionPhase {

    /** Ekran ustawien - sesja nie trwa. */
    data object Idle : SessionPhase

    /** Odliczanie 3-2-1 przed pierwszym bodzcem. */
    data class Countdown(val secondsLeft: Int) : SessionPhase

    /**
     * Trwa sesja. [startedAtMillis] jest w tej samej skali co
     * [com.rainbow.training.feature.monotonicNowMillis], dzieki czemu ekran moze plynnie
     * animowac pasek postepu bez dodatkowych zdarzen z modelu.
     */
    data class Running(
        val stimulus: Stimulus,
        val startedAtMillis: Long,
        val totalMillis: Long,
    ) : SessionPhase

    /** Sesja dobiegla konca. */
    data class Finished(
        val durationMillis: Long,
        val stimulusCount: Int,
    ) : SessionPhase
}

/** Monotoniczny zegar wspolny dla silnika sesji i dla animacji w UI. */
fun monotonicNowMillis(): Long = System.nanoTime() / 1_000_000
