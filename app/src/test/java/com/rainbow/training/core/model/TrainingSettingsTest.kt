package com.rainbow.training.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        assertEquals(SettingsError.INTERVAL_REVERSED, settings.validationError)
    }

    @Test
    fun `za malo kolorow to blad`() {
        val settings = TrainingSettings(enabledColors = setOf(StimulusColor.RED))
        assertEquals(SettingsError.TOO_FEW_COLORS, settings.validationError)
    }

    @Test
    fun `za dluga sesja to blad`() {
        val settings = TrainingSettings(sessionDurationMillis = 5L * 60L * 60_000L)
        assertEquals(SettingsError.SESSION_TOO_LONG, settings.validationError)
    }

    @Test
    fun `cyfry maja znaczenie tylko w trybach z cyframi`() {
        val digits = setOf(7)
        assertTrue(TrainingSettings(mode = StimulusMode.COLORS, enabledDigits = digits).isValid)
        assertEquals(
            SettingsError.TOO_FEW_DIGITS,
            TrainingSettings(mode = StimulusMode.DIGITS, enabledDigits = digits).validationError,
        )
        assertEquals(
            SettingsError.TOO_FEW_DIGITS,
            TrainingSettings(mode = StimulusMode.MIXED, enabledDigits = digits).validationError,
        )
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
    fun `sanitized odbudowuje palete, gdy zapis niesie za malo kolorow`() {
        // Tak wyglada wczytanie ustawien zapisanych przez starsza wersje z inna paleta:
        // nieznane identyfikatory odpadaja i zostaje ich za malo, zeby wystartowac.
        val sanitized = TrainingSettings(enabledColors = setOf(StimulusColor.RED)).sanitized()

        assertEquals(StimulusColor.ALL.toSet(), sanitized.enabledColors)
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

    @Test
    fun `niepoprawne ustawienia nie sa poprawne`() {
        assertFalse(TrainingSettings(enabledColors = emptySet()).isValid)
    }
}
