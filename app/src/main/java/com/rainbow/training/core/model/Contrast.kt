package com.rainbow.training.core.model

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Obliczenia kontrastu wg WCAG 2.1, w czystym Kotlinie (bez zaleznosci od Androida),
 * dzieki czemu daja sie pokryc zwyklymi testami jednostkowymi.
 *
 * Kolory reprezentujemy jako ARGB w [Long] (0xAARRGGBB).
 */
object Contrast {

    const val WHITE: Long = 0xFFFFFFFF
    const val BLACK: Long = 0xFF000000

    /** Wzgledna luminancja kanalu, po linearyzacji sRGB. */
    private fun linearize(channel8Bit: Int): Double {
        val s = channel8Bit / 255.0
        return if (s <= 0.03928) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
    }

    /** Wzgledna luminancja koloru w zakresie 0.0 (czarny) .. 1.0 (bialy). */
    fun relativeLuminance(argb: Long): Double {
        val r = ((argb shr 16) and 0xFF).toInt()
        val g = ((argb shr 8) and 0xFF).toInt()
        val b = (argb and 0xFF).toInt()
        return 0.2126 * linearize(r) + 0.7152 * linearize(g) + 0.0722 * linearize(b)
    }

    /** Wspolczynnik kontrastu miedzy dwoma kolorami, od 1.0 (brak) do 21.0 (czern vs biel). */
    fun ratio(first: Long, second: Long): Double {
        val a = relativeLuminance(first)
        val b = relativeLuminance(second)
        return (max(a, b) + 0.05) / (min(a, b) + 0.05)
    }

    /**
     * Kolor tresci (cyfry) dajacy najwyzszy mozliwy kontrast na podanym tle - czyli "negatyw"
     * jasnosciowy: na jasnym tle czarny, na ciemnym bialy.
     *
     * Gdyby kiedys zamiast czystej bieli/czerni potrzebny byl odcien dopelniajacy,
     * wystarczy podmienic tylko te funkcje - reszta aplikacji jej nie zaklada.
     */
    fun bestContentColor(backgroundArgb: Long): Long =
        if (ratio(backgroundArgb, WHITE) >= ratio(backgroundArgb, BLACK)) WHITE else BLACK
}
