package com.rainbow.training.core.model

/**
 * Komplet ustawien sesji treningowej. Niezmienny obiekt - kazda zmiana w UI tworzy nowa kopie,
 * ktora trafia do repozytorium i jest zapisywana na dysku.
 */
data class TrainingSettings(
    val mode: StimulusMode = StimulusMode.COLORS,
    val minIntervalMillis: Long = DEFAULT_MIN_INTERVAL_MILLIS,
    val maxIntervalMillis: Long = DEFAULT_MAX_INTERVAL_MILLIS,
    val sessionDurationMillis: Long = DEFAULT_SESSION_DURATION_MILLIS,
    val enabledColors: Set<StimulusColor> = StimulusColor.ALL.toSet(),
    val enabledDigits: Set<Int> = ALL_DIGITS,
) {

    /** Pierwszy napotkany problem z ustawieniami albo `null`, gdy wszystko jest poprawne. */
    val validationError: String?
        get() = when {
            enabledColors.size < MIN_ENABLED_COLORS ->
                "Wybierz co najmniej $MIN_ENABLED_COLORS kolory."

            mode.usesDigits && enabledDigits.size < MIN_ENABLED_DIGITS ->
                "Wybierz co najmniej $MIN_ENABLED_DIGITS cyfry."

            minIntervalMillis < MIN_INTERVAL_MILLIS ->
                "Minimalny odstęp to ${formatSeconds(MIN_INTERVAL_MILLIS)} s."

            maxIntervalMillis > MAX_INTERVAL_MILLIS ->
                "Maksymalny odstęp to ${formatSeconds(MAX_INTERVAL_MILLIS)} s."

            minIntervalMillis > maxIntervalMillis ->
                "Dolna granica odstępu nie może być większa od górnej."

            sessionDurationMillis < MIN_SESSION_DURATION_MILLIS ->
                "Sesja musi trwać co najmniej ${MIN_SESSION_DURATION_MILLIS / 1000} s."

            sessionDurationMillis > MAX_SESSION_DURATION_MILLIS ->
                "Sesja może trwać najwyżej ${MAX_SESSION_DURATION_MILLIS / 60_000} min."

            else -> null
        }

    val isValid: Boolean get() = validationError == null

    /** Ustawienia przyciete do dozwolonych zakresow - uzywane przy wczytywaniu z dysku. */
    fun sanitized(): TrainingSettings {
        val min = minIntervalMillis.coerceIn(MIN_INTERVAL_MILLIS, MAX_INTERVAL_MILLIS)
        val max = maxIntervalMillis.coerceIn(min, MAX_INTERVAL_MILLIS)
        return copy(
            minIntervalMillis = min,
            maxIntervalMillis = max,
            sessionDurationMillis = sessionDurationMillis
                .coerceIn(MIN_SESSION_DURATION_MILLIS, MAX_SESSION_DURATION_MILLIS),
            enabledColors = enabledColors.ifEmpty { StimulusColor.ALL.toSet() },
            enabledDigits = enabledDigits.filter { it in 0..9 }.toSet().ifEmpty { ALL_DIGITS },
        )
    }

    companion object {
        val ALL_DIGITS: Set<Int> = (0..9).toSet()

        const val MIN_INTERVAL_MILLIS = 100L
        const val MAX_INTERVAL_MILLIS = 10_000L
        const val DEFAULT_MIN_INTERVAL_MILLIS = 500L
        const val DEFAULT_MAX_INTERVAL_MILLIS = 3_000L

        const val MIN_SESSION_DURATION_MILLIS = 10_000L
        const val MAX_SESSION_DURATION_MILLIS = 60L * 60_000L
        const val DEFAULT_SESSION_DURATION_MILLIS = 2L * 60_000L

        const val MIN_ENABLED_COLORS = 2
        const val MIN_ENABLED_DIGITS = 2

        /** Dlugosci sesji oferowane jednym kliknieciem (30 s .. 10 min). */
        val SESSION_PRESETS_MILLIS: List<Long> = listOf(
            30_000L,
            60_000L,
            2L * 60_000L,
            5L * 60_000L,
            10L * 60_000L,
        )

        private fun formatSeconds(millis: Long): String {
            val tenths = (millis + 50) / 100
            return if (tenths % 10 == 0L) "${tenths / 10}" else "${tenths / 10},${tenths % 10}"
        }
    }
}
