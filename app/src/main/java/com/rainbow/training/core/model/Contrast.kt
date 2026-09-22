package com.rainbow.training.core.model

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Praca na kolorach: kontrast wg WCAG 2.1 i barwy dopelniajace.
 *
 * Wszystko w czystym Kotlinie (bez zaleznosci od Androida), wiec daje sie pokryc zwyklymi
 * testami jednostkowymi. Kolory reprezentujemy jako ARGB w [Long] (0xAARRGGBB).
 */
object Contrast {

    const val WHITE: Long = 0xFFFFFFFF
    const val BLACK: Long = 0xFF000000

    /**
     * Minimalny kontrast cyfry wzgledem tla. 3.0 to prog WCAG AA dla duzego tekstu - a cyfra
     * zajmuje tu ponad polowe wysokosci ekranu, wiec jest wielokrotnie wieksza niz "duzy tekst".
     *
     * Celowo nie celujemy wyzej: barwa dopelniajaca ma z natury jasnosc zblizona do tla, wiec
     * kazdy wyzszy prog spycha ja ku czerni albo bieli i gubi to, o co chodzi - widoczny kolor.
     */
    const val MIN_DIGIT_RATIO = 3.0

    /** Barwa dopelniajaca ma byc nasycona, nawet jesli tlo takie nie jest. */
    private const val MIN_SATURATION = 0.85

    /** Ponizej tego nasycenia kolor uznajemy za pozbawiony barwy (biel, czern, szarosc). */
    private const val ACHROMATIC_SATURATION = 0.05

    /**
     * Kandydaci na jasnosc barwy dopelniajacej, od najbardziej soczystych (0.5) ku skrajnym.
     * Bierzemy pierwszego, ktory spelnia prog kontrastu - czyli najzywszy z czytelnych.
     */
    private val LIGHTNESS_CANDIDATES: List<Double> =
        (5..95 step 5).map { it / 100.0 }.sortedBy { abs(it - 0.5) }

    // ---------------------------------------------------------------- kontrast

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
     * Czern albo biel - to z dwojga, co mocniej odcina sie od podanego tla.
     * Uzywamy tego na obrys cyfry, ktory ma wyciac jej ksztalt z tla niezaleznie od barw.
     */
    fun bestContentColor(backgroundArgb: Long): Long =
        if (ratio(backgroundArgb, WHITE) >= ratio(backgroundArgb, BLACK)) WHITE else BLACK

    // ------------------------------------------------------------ barwa dopelniajaca

    /**
     * Barwa lezaca po przeciwnej stronie kola barw niz [backgroundArgb] - prawdziwy negatyw
     * barwny, a nie czern czy biel (czerwone tlo daje cyjanowa cyfre, zolte niebieska).
     *
     * Sama barwa jest wyznaczona jednoznacznie (odcien + 180 stopni); wolna reka zostaje przy
     * jasnosci, wiec szukamy najzywszego wariantu, ktory jeszcze spelnia [minRatio]. Gdy zaden
     * nie spelnia - a tak bywa, bo barwy dopelniajace potrafia miec niemal identyczna jasnosc -
     * oddajemy najlepszy z dostepnych i zostawiamy czytelnosc obrysowi.
     */
    fun complementary(backgroundArgb: Long, minRatio: Double = MIN_DIGIT_RATIO): Long {
        val (hue, saturation, _) = toHsl(backgroundArgb)

        // Biel, czern i szarosci nie maja wlasnej barwy, wiec obracanie odcienia o 180 stopni
        // dawaloby kolor wzięty z niczego. Ich prawdziwym negatywem jest druga skrajnosc
        // jasnosci: czern na bieli, biel na czerni.
        if (saturation < ACHROMATIC_SATURATION) return bestContentColor(backgroundArgb)

        val complementHue = (hue + 180.0) % 360.0
        val vividSaturation = max(saturation, MIN_SATURATION)

        var fallback = BLACK
        var fallbackRatio = 0.0

        for (lightness in LIGHTNESS_CANDIDATES) {
            val candidate = fromHsl(complementHue, vividSaturation, lightness)
            val candidateRatio = ratio(backgroundArgb, candidate)
            if (candidateRatio >= minRatio) return candidate
            if (candidateRatio > fallbackRatio) {
                fallback = candidate
                fallbackRatio = candidateRatio
            }
        }
        return fallback
    }

    /** Odcien koloru w stopniach (0..360). Przydatny w testach. */
    fun hue(argb: Long): Double = toHsl(argb).first

    // ------------------------------------------------------------------- HSL

    /** Zwraca (odcien 0..360, nasycenie 0..1, jasnosc 0..1). */
    fun toHsl(argb: Long): Triple<Double, Double, Double> {
        val r = ((argb shr 16) and 0xFF).toInt() / 255.0
        val g = ((argb shr 8) and 0xFF).toInt() / 255.0
        val b = (argb and 0xFF).toInt() / 255.0

        val maxChannel = maxOf(r, g, b)
        val minChannel = minOf(r, g, b)
        val lightness = (maxChannel + minChannel) / 2.0

        if (maxChannel == minChannel) return Triple(0.0, 0.0, lightness)

        val delta = maxChannel - minChannel
        val saturation = if (lightness > 0.5) {
            delta / (2.0 - maxChannel - minChannel)
        } else {
            delta / (maxChannel + minChannel)
        }
        val hue = when (maxChannel) {
            r -> (g - b) / delta + if (g < b) 6.0 else 0.0
            g -> (b - r) / delta + 2.0
            else -> (r - g) / delta + 4.0
        } * 60.0

        return Triple(hue, saturation, lightness)
    }

    fun fromHsl(hue: Double, saturation: Double, lightness: Double): Long {
        if (saturation == 0.0) {
            val gray = (lightness * 255).roundToInt().toLong().coerceIn(0L, 255L)
            return 0xFF000000L or (gray shl 16) or (gray shl 8) or gray
        }

        val q = if (lightness < 0.5) {
            lightness * (1.0 + saturation)
        } else {
            lightness + saturation - lightness * saturation
        }
        val p = 2.0 * lightness - q
        val normalizedHue = (((hue % 360.0) + 360.0) % 360.0) / 360.0

        val r = channelFromHue(p, q, normalizedHue + 1.0 / 3.0)
        val g = channelFromHue(p, q, normalizedHue)
        val b = channelFromHue(p, q, normalizedHue - 1.0 / 3.0)

        return 0xFF000000L or (r shl 16) or (g shl 8) or b
    }

    private fun channelFromHue(p: Double, q: Double, rawT: Double): Long {
        var t = rawT
        if (t < 0.0) t += 1.0
        if (t > 1.0) t -= 1.0
        val value = when {
            t < 1.0 / 6.0 -> p + (q - p) * 6.0 * t
            t < 1.0 / 2.0 -> q
            t < 2.0 / 3.0 -> p + (q - p) * (2.0 / 3.0 - t) * 6.0
            else -> p
        }
        return (value * 255).roundToInt().toLong().coerceIn(0L, 255L)
    }
}
