package com.rainbow.training.core.engine

import com.rainbow.training.core.model.StimulusMode
import com.rainbow.training.core.model.TrainingSettings
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SessionEngineTest {

    private val settings = TrainingSettings(
        mode = StimulusMode.MIXED,
        minIntervalMillis = 500,
        maxIntervalMillis = 3_000,
        sessionDurationMillis = 60_000,
    )

    @Test
    fun `sesja zaczyna sie od odliczania 3 2 1`() = runTest {
        val events = engine().run().toList()
        val countdown = events.filterIsInstance<SessionEvent.Countdown>()

        assertEquals(listOf(3, 2, 1), countdown.map { it.secondsLeft })
    }

    @Test
    fun `sesja konczy sie dokladnie po zadanym czasie`() = runTest {
        val events = engine().run().toList()
        val finished = events.last() as SessionEvent.Finished

        assertEquals(settings.sessionDurationMillis, finished.durationMillis)
        assertEquals(
            events.count { it is SessionEvent.Show },
            finished.stimulusCount,
        )
    }

    @Test
    fun `bodzce mieszcza sie w czasie sesji i jest ich sensowna liczba`() = runTest {
        val events = engine().run().toList()
        val shows = events.filterIsInstance<SessionEvent.Show>()

        assertTrue("Sesja nie pokazala zadnego bodzca", shows.isNotEmpty())
        assertTrue(shows.all { it.elapsedMillis in 0 until settings.sessionDurationMillis })
        assertTrue(shows.all { it.totalMillis == settings.sessionDurationMillis })

        // 60 s przy odstepie 0,5-3,0 s to srednio ok. 34 bodzce - zostawiamy szeroki margines.
        assertTrue("Nieoczekiwana liczba bodzcow: ${shows.size}", shows.size in 20..120)
    }

    @Test
    fun `odstepy miedzy bodzcami nie sa stale`() = runTest {
        val shows = engine().run().toList().filterIsInstance<SessionEvent.Show>()
        val gaps = shows.map { it.elapsedMillis }.zipWithNext { a, b -> b - a }

        assertTrue("Brak zroznicowania odstepow", gaps.distinct().size > 5)
        // Ostatni odstep bywa przyciety do konca sesji, wiec sprawdzamy gorna granice.
        assertTrue(gaps.all { it <= settings.maxIntervalMillis })
    }

    @Test
    fun `krotka sesja tez sie konczy i nie zapetla`() = runTest {
        val short = settings.copy(
            sessionDurationMillis = TrainingSettings.MIN_SESSION_DURATION_MILLIS,
            minIntervalMillis = 100,
            maxIntervalMillis = 100,
        )
        val events = SessionEngine(
            settings = short,
            random = Random(11),
            nowMillis = { testScheduler.currentTime },
        ).run().toList()

        assertTrue(events.last() is SessionEvent.Finished)
        assertEquals(100, events.count { it is SessionEvent.Show })
    }

    /** Silnik na wirtualnym zegarze korutyn - testy biegna natychmiast, bez realnych opoznien. */
    private fun TestScope.engine() = SessionEngine(
        settings = settings,
        random = Random(42),
        nowMillis = { testScheduler.currentTime },
    )
}
