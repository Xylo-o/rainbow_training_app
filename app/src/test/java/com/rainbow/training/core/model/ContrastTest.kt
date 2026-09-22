package com.rainbow.training.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
    fun `jasne tla dostaja czarna cyfre, ciemne biala`() {
        assertEquals(Contrast.BLACK, Contrast.bestContentColor(StimulusColor.YELLOW.argb))
        assertEquals(Contrast.WHITE, Contrast.bestContentColor(StimulusColor.PURPLE.argb))
        assertEquals(Contrast.WHITE, Contrast.bestContentColor(StimulusColor.BLUE.argb))
    }

    @Test
    fun `kazdy kolor palety zapewnia cyfrze czytelny kontrast`() {
        StimulusColor.ALL.forEach { color ->
            val ratio = Contrast.ratio(color.argb, color.contentArgb)
            // 3.0 to prog WCAG AA dla duzego tekstu; cyfra na pelnym ekranie jest ogromna.
            assertTrue(
                "Kolor ${color.displayName} ma kontrast tylko $ratio",
                ratio >= 3.0,
            )
        }
    }
}
