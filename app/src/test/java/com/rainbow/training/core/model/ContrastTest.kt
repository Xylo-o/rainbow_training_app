package com.rainbow.training.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ContrastTest {

    /** Kolory majace wlasna barwe - biel i czern rzadza sie osobnymi regulami. */
    private val chromatic = StimulusColor.ALL - setOf(StimulusColor.WHITE, StimulusColor.BLACK)

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
    fun `barwne tlo dostaje cyfre w kolorze, a nie w czerni czy bieli`() {
        chromatic.forEach { color ->
            assertNotEquals(color.id, Contrast.WHITE, color.contentArgb)
            assertNotEquals(color.id, Contrast.BLACK, color.contentArgb)
        }
    }

    @Test
    fun `barwa cyfry lezy po przeciwnej stronie kola barw`() {
        chromatic.forEach { color ->
            val distance = hueDistance(Contrast.hue(color.argb), Contrast.hue(color.contentArgb))
            assertTrue(
                "${color.id}: barwy dzieli $distance stopni zamiast ~180",
                distance >= 165.0,
            )
        }
    }

    @Test
    fun `biel i czern dostaja swoje przeciwienstwo jasnosciowe`() {
        assertEquals(Contrast.BLACK, StimulusColor.WHITE.contentArgb)
        assertEquals(Contrast.WHITE, StimulusColor.BLACK.contentArgb)
    }

    @Test
    fun `na bieli i czerni obrys jest zbedny, na barwach konieczny`() {
        assertFalse(StimulusColor.WHITE.hasOutline)
        assertFalse(StimulusColor.BLACK.hasOutline)
        chromatic.forEach { color ->
            assertTrue("${color.id} powinien miec obrys", color.hasOutline)
        }
    }

    @Test
    fun `kazda cyfra spelnia prog WCAG AA dla duzego tekstu`() {
        StimulusColor.ALL.forEach { color ->
            val ratio = Contrast.ratio(color.argb, color.contentArgb)
            assertTrue(
                "${color.id} ma kontrast cyfry tylko $ratio",
                ratio >= Contrast.MIN_DIGIT_RATIO,
            )
        }
    }

    @Test
    fun `obrys to czern albo biel i mocno odcina cyfre od tla`() {
        StimulusColor.ALL.forEach { color ->
            assertTrue(
                "${color.id}: obrys nie jest czernia ani biela",
                color.outlineArgb == Contrast.WHITE || color.outlineArgb == Contrast.BLACK,
            )
            val ratio = Contrast.ratio(color.argb, color.outlineArgb)
            assertTrue("${color.id}: obrys ma kontrast tylko $ratio", ratio >= 4.5)
        }
    }

    @Test
    fun `szare tlo dostaje cyfre o maksymalnym kontrascie`() {
        // Szarosc nie ma barwy, wiec nie ma tez sensownego dopelnienia - liczy sie czysty kontrast.
        val digit = Contrast.complementary(0xFF808080)
        assertTrue(digit == Contrast.WHITE || digit == Contrast.BLACK)
    }

    @Test
    fun `konwersja przez HSL i z powrotem zachowuje kolor`() {
        val samples = listOf(
            0xFFE53935, 0xFF1E88E5, 0xFFFDD835, 0xFF43A047,
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
