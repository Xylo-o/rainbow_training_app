package com.rainbow.training.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TrainingSettingsTest {

    @Test
    fun `domyslne ustawienia sa poprawne`() {
        val settings = TrainingSettings()
        assertNull(settings.validationError)
        assertTrue(settings.isValid)
        assertEquals(500L, settings.minIntervalMillis)
        assertEquals(3_000L, settings.maxIntervalMillis)
    }

    @Test
    fun `odwrocony zakres odstepow jest bledem`() {
        val settings = TrainingSettings(minIntervalMillis = 4_000, maxIntervalMillis = 1_000)
        assertFalse(settings.isValid)
        assertNotNull(settings.validationError)
    }

    @Test
    fun `za malo kolorow to blad`() {
        val settings = TrainingSettings(enabledColors = setOf(StimulusColor.RED))
        assertFalse(settings.isValid)
    }

    @Test
    fun `cyfry maja znaczenie tylko w trybach z cyframi`() {
        val digits = setOf(7)
        assertTrue(TrainingSettings(mode = StimulusMode.COLORS, enabledDigits = digits).isValid)
        assertFalse(TrainingSettings(mode = StimulusMode.DIGITS, enabledDigits = digits).isValid)
        assertFalse(TrainingSettings(mode = StimulusMode.MIXED, enabledDigits = digits).isValid)
    }

    @Test
    fun `sanitized przycina wartosci do dozwolonych zakresow`() {
        val sanitized = TrainingSettings(
            minIntervalMillis = 1,
            maxIntervalMillis = 999_999,
            sessionDurationMillis = 0,
            enabledColors = emptySet(),
            enabledDigits = emptySet(),
        ).sanitized()

        assertEquals(TrainingSettings.MIN_INTERVAL_MILLIS, sanitized.minIntervalMillis)
        assertEquals(TrainingSettings.MAX_INTERVAL_MILLIS, sanitized.maxIntervalMillis)
        assertEquals(TrainingSettings.MIN_SESSION_DURATION_MILLIS, sanitized.sessionDurationMillis)
        assertEquals(StimulusColor.ALL.toSet(), sanitized.enabledColors)
        assertEquals(TrainingSettings.ALL_DIGITS, sanitized.enabledDigits)
        assertTrue(sanitized.isValid)
    }

    @Test
    fun `sanitized nie pozwala by dolna granica przekroczyla gorna`() {
        val sanitized = TrainingSettings(
            minIntervalMillis = 8_000,
            maxIntervalMillis = 2_000,
        ).sanitized()

        assertTrue(sanitized.minIntervalMillis <= sanitized.maxIntervalMillis)
        assertTrue(sanitized.isValid)
    }
}
