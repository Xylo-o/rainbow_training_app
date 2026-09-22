package com.rainbow.training.core.model

/**
 * Pojedynczy bodziec pokazywany na pelnym ekranie.
 *
 * Kazdy bodziec ma kolor tla; wariant [Digit] dokłada cyfre rysowana kolorem [contentArgb].
 */
sealed interface Stimulus {

    val color: StimulusColor

    val backgroundArgb: Long get() = color.argb

    val contentArgb: Long get() = color.contentArgb

    /** Samo kolorowe tlo. */
    data class ColorOnly(override val color: StimulusColor) : Stimulus

    /** Cyfra [digit] (0-9) na kolorowym tle. */
    data class Digit(override val color: StimulusColor, val digit: Int) : Stimulus {
        init {
            require(digit in 0..9) { "Cyfra musi byc z zakresu 0..9, otrzymano $digit" }
        }
    }
}
