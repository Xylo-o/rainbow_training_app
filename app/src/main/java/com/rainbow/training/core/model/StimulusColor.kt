package com.rainbow.training.core.model

/**
 * Paleta bodzcow: 8 barw rozlozonych rownomiernie po kole barw, dobranych tak, by bylo je
 * latwo rozroznic i szybko nazwac na glos.
 *
 * [argb] trzymamy jako [Long], a nie jako `androidx.compose.ui.graphics.Color`, zeby model
 * pozostal niezalezny od warstwy UI (i dzialal w testach jednostkowych na czystej JVM).
 */
enum class StimulusColor(
    val id: String,
    val displayName: String,
    val argb: Long,
) {
    RED("red", "Czerwony", 0xFFE53935),
    ORANGE("orange", "Pomarańczowy", 0xFFFB8C00),
    YELLOW("yellow", "Żółty", 0xFFFDD835),
    GREEN("green", "Zielony", 0xFF43A047),
    TEAL("teal", "Turkusowy", 0xFF00ACC1),
    BLUE("blue", "Niebieski", 0xFF1E88E5),
    PURPLE("purple", "Fioletowy", 0xFF8E24AA),
    PINK("pink", "Różowy", 0xFFD81B60),
    ;

    /**
     * Kolor cyfry na tym tle: barwa z przeciwnej strony kola barw (negatyw barwny).
     * Liczony raz, przy inicjalizacji enuma - w trakcie sesji odczytywany co klatke.
     */
    val contentArgb: Long = Contrast.complementary(argb)

    /**
     * Obrys cyfry - czern albo biel, zaleznie od tego, co mocniej odcina sie od tla.
     * To on odpowiada za ostra krawedz znaku i ratuje czytelnosc tam, gdzie barwa
     * dopelniajaca ma jasnosc zblizona do tla (np. niebieski i pomaranczowy).
     */
    val outlineArgb: Long = Contrast.bestContentColor(argb)

    companion object {
        val ALL: List<StimulusColor> = entries

        fun fromId(id: String): StimulusColor? = entries.firstOrNull { it.id == id }
    }
}
