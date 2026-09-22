package com.rainbow.training.core.engine

import com.rainbow.training.core.model.Stimulus
import com.rainbow.training.core.model.StimulusMode
import com.rainbow.training.core.model.TrainingSettings
import kotlin.random.Random

/**
 * Losuje kolejne bodzce i odstepy miedzy nimi.
 *
 * Losowanie jest bezpamieciowe - kazdy bodziec jest ciagniety niezaleznie, wiec ten sam kolor
 * lub ta sama cyfra moga wypasc dwa razy pod rzad (swiadomy wybor: pelna losowosc).
 *
 * [random] jest wstrzykiwany, dzieki czemu testy moga podac generator z ustalonym ziarnem.
 */
class StimulusGenerator(
    private val settings: TrainingSettings,
    private val random: Random = Random.Default,
) {

    private val colors = settings.enabledColors.sortedBy { it.ordinal }
    private val digits = settings.enabledDigits.sorted()

    init {
        require(colors.isNotEmpty()) { "Brak wlaczonych kolorow" }
        require(!settings.mode.usesDigits || digits.isNotEmpty()) { "Brak wlaczonych cyfr" }
    }

    /** Kolejny bodziec, zgodny z trybem z ustawien. */
    fun nextStimulus(): Stimulus {
        val color = colors[random.nextInt(colors.size)]
        val withDigit = when (settings.mode) {
            StimulusMode.COLORS -> false
            StimulusMode.DIGITS -> true
            StimulusMode.MIXED -> random.nextBoolean()
        }
        return if (withDigit) {
            Stimulus.Digit(color, digits[random.nextInt(digits.size)])
        } else {
            Stimulus.ColorOnly(color)
        }
    }

    /**
     * Kolejny odstep w milisekundach - losowany osobno dla kazdej zmiany, rownomiernie
     * w calym zakresie ustawionym przez uzytkownika (obie granice wlacznie).
     */
    fun nextIntervalMillis(): Long =
        random.nextLong(settings.minIntervalMillis, settings.maxIntervalMillis + 1)
}
