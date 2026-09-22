package com.rainbow.training.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ContrastTest {

    @Test
    fun `czern na bieli daje maksymalny kontrast 21`() {
        assertEquals(21.0, Contrast.ratio(Contrast.WHITE, Contrast.BLACK), 0.01)
    }

    @Test
    fun `kontrast jest symetryczny`() {
        val a = 0xFF1E88E5
        val b = 0xFFFDD835
        assertEquals(Contrast.ratio(a, b), Contrast.ratio(b, a), 1e-9)
    }

    @Test
    fun `cyfra dostaje barwe, a nie czern czy biel`() {
        StimulusColor.ALL.forEach { color ->
            assertNotEquals(color.displayName, Contrast.WHITE, color.contentArgb)
            assertNotEquals(color.displayName, Contrast.BLACK, color.contentArgb)
        }
    }

    @Test
    fun `barwa cyfry lezy po przeciwnej stronie kola barw`() {
        StimulusColor.ALL.forEach { color ->
            val distance = hueDistance(Contrast.hue(color.argb), Contrast.hue(color.contentArgb))
            assertTrue(
                "${color.displayName}: barwy dzieli $distance stopni zamiast ~180",
                distance >= 165.0,
            )
        }
    }

    @Test
    fun `kazda cyfra spelnia prog WCAG AA dla duzego tekstu`() {
        StimulusColor.ALL.forEach { color ->
            val ratio = Contrast.ratio(color.argb, color.contentArgb)
            assertTrue(
                "${color.displayName} ma kontrast cyfry tylko $ratio",
                ratio >= Contrast.MIN_DIGIT_RATIO,
            )
        }
    }

    @Test
    fun `obrys to czern albo biel i mocno odcina cyfre od tla`() {
        StimulusColor.ALL.forEach { color ->
            assertTrue(
                "${color.displayName}: obrys nie jest czernia ani biela",
                color.outlineArgb == Contrast.WHITE || color.outlineArgb == Contrast.BLACK,
            )
            val ratio = Contrast.ratio(color.argb, color.outlineArgb)
            assertTrue("${color.displayName}: obrys ma kontrast tylko $ratio", ratio >= 4.5)
        }
    }

    @Test
    fun `nienasycone tlo i tak dostaje nasycona cyfre`() {
        // Szarosc nie ma wlasnej barwy - dopelnienie musi i tak byc kolorowe, nie kolejna szaroscia.
        val digit = Contrast.complementary(0xFF808080)
        val (_, saturation, _) = Contrast.toHsl(digit)
        assertTrue("Cyfra na szarym tle ma nasycenie tylko $saturation", saturation >= 0.8)
    }

    @Test
    fun `konwersja przez HSL i z powrotem zachowuje kolor`() {
        val samples = listOf(
            0xFFE53935, 0xFF1E88E5, 0xFF00ACC1, 0xFFFDD835,
            0xFF808080, 0xFF000000, 0xFFFFFFFF,
        )
        samples.forEach { argb ->
            val (h, s, l) = Contrast.toHsl(argb)
            val roundTrip = Contrast.fromHsl(h, s, l)
            for (shift in listOf(16, 8, 0)) {
                val before = (argb shr shift) and 0xFF
                val after = (roundTrip shr shift) and 0xFF
                assertTrue(
                    "0x%08X -> 0x%08X rozjechalo sie na kanale $shift".format(argb, roundTrip),
                    abs(before - after) <= 1,
                )
            }
        }
    }

    /** Najkrotsza odleglosc katowa miedzy dwiema barwami (0..180 stopni). */
    private fun hueDistance(first: Double, second: Double): Double {
        val raw = abs(first - second) % 360.0
        return if (raw > 180.0) 360.0 - raw else raw
    }
}
