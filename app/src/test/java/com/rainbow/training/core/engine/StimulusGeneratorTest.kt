package com.rainbow.training.core.engine

import com.rainbow.training.core.model.Stimulus
import com.rainbow.training.core.model.StimulusColor
import com.rainbow.training.core.model.StimulusMode
import com.rainbow.training.core.model.TrainingSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class StimulusGeneratorTest {

    private fun settings(
        mode: StimulusMode = StimulusMode.COLORS,
        colors: Set<StimulusColor> = StimulusColor.ALL.toSet(),
        digits: Set<Int> = TrainingSettings.ALL_DIGITS,
        min: Long = 500,
        max: Long = 3_000,
    ) = TrainingSettings(
        mode = mode,
        enabledColors = colors,
        enabledDigits = digits,
        minIntervalMillis = min,
        maxIntervalMillis = max,
    )

    @Test
    fun `tryb kolorow nigdy nie pokazuje cyfry`() {
        val generator = StimulusGenerator(settings(mode = StimulusMode.COLORS), Random(1))
        repeat(200) {
            assertTrue(generator.nextStimulus() is Stimulus.ColorOnly)
        }
    }

    @Test
    fun `tryb cyfr zawsze pokazuje cyfre`() {
        val generator = StimulusGenerator(settings(mode = StimulusMode.DIGITS), Random(2))
        repeat(200) {
            assertTrue(generator.nextStimulus() is Stimulus.Digit)
        }
    }

    @Test
    fun `tryb mieszany produkuje oba rodzaje bodzcow`() {
        val generator = StimulusGenerator(settings(mode = StimulusMode.MIXED), Random(3))
        val kinds = List(300) { generator.nextStimulus() }
        assertTrue(kinds.any { it is Stimulus.ColorOnly })
        assertTrue(kinds.any { it is Stimulus.Digit })
    }

    @Test
    fun `losowane sa wylacznie wlaczone kolory i cyfry`() {
        val colors = setOf(StimulusColor.RED, StimulusColor.BLUE)
        val digits = setOf(3, 8)
        val generator = StimulusGenerator(
            settings(mode = StimulusMode.DIGITS, colors = colors, digits = digits),
            Random(4),
        )
        repeat(300) {
            val stimulus = generator.nextStimulus() as Stimulus.Digit
            assertTrue(stimulus.color in colors)
            assertTrue(stimulus.digit in digits)
        }
    }

    @Test
    fun `odstep miesci sie w zadanym zakresie i naprawde sie zmienia`() {
        val generator = StimulusGenerator(settings(min = 500, max = 3_000), Random(5))
        val intervals = List(500) { generator.nextIntervalMillis() }

        assertTrue(intervals.all { it in 500..3_000 })
        // Odstep ma byc losowy przy KAZDEJ zmianie, a nie staly przez cala sesje.
        assertTrue("Odstepy nie sa zroznicowane", intervals.distinct().size > 50)
    }

    @Test
    fun `staly zakres jednoelementowy zwraca zawsze te sama wartosc`() {
        val generator = StimulusGenerator(settings(min = 1_000, max = 1_000), Random(6))
        repeat(50) { assertEquals(1_000L, generator.nextIntervalMillis()) }
    }

    @Test
    fun `powtorzenia tego samego bodzca sa dozwolone`() {
        val generator = StimulusGenerator(
            settings(colors = setOf(StimulusColor.RED, StimulusColor.BLUE)),
            Random(7),
        )
        val sequence = List(400) { generator.nextStimulus() }
        val hasRepeat = sequence.zipWithNext().any { (a, b) -> a == b }
        assertTrue("Przy dwoch kolorach powtorka musi kiedys wypasc", hasRepeat)
    }
}
