package com.rainbow.training.core.engine

import com.rainbow.training.core.model.TrainingSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Przebieg jednej sesji wyrazony jako strumien zdarzen.
 *
 * Silnik nie wie nic o UI ani o Androidzie: zegar ([nowMillis]) i zrodlo losowosci sa
 * wstrzykiwane, wiec caly przebieg da sie przetestowac na wirtualnym czasie korutyn.
 *
 * Odstepy sa liczone wzgledem terminu (a nie przez proste sumowanie `delay`), dzieki czemu
 * drobne opoznienia nie kumuluja sie przez cala sesje i sesja konczy sie punktualnie.
 */
class SessionEngine(
    private val settings: TrainingSettings,
    private val random: Random = Random.Default,
    private val countdownSeconds: Int = DEFAULT_COUNTDOWN_SECONDS,
    private val nowMillis: () -> Long = { System.nanoTime() / 1_000_000 },
) {

    fun run(): Flow<SessionEvent> = flow {
        for (second in countdownSeconds downTo 1) {
            emit(SessionEvent.Countdown(second))
            delay(COUNTDOWN_STEP_MILLIS)
        }

        val generator = StimulusGenerator(settings, random)
        val total = settings.sessionDurationMillis
        val startedAt = nowMillis()
        val endsAt = startedAt + total
        var shown = 0

        while (true) {
            val now = nowMillis()
            val remaining = endsAt - now
            if (remaining < MIN_TAIL_MILLIS) break

            emit(SessionEvent.Show(generator.nextStimulus(), now - startedAt, total))
            shown++

            val interval = generator.nextIntervalMillis().coerceAtMost(remaining)
            val wakeAt = now + interval
            delay(wakeAt - nowMillis())
        }

        emit(SessionEvent.Finished(durationMillis = total, stimulusCount = shown))
    }

    companion object {
        const val DEFAULT_COUNTDOWN_SECONDS = 3
        const val COUNTDOWN_STEP_MILLIS = 1_000L

        /** Ponizej tego progu nie warto juz mrugac kolejnym bodzcem - konczymy sesje. */
        const val MIN_TAIL_MILLIS = 50L
    }
}
