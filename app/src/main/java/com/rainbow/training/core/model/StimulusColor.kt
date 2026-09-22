package com.rainbow.training.core.model

/**
 * Paleta bodzcow: szesc barw rozlozonych po kole barw plus biel i czern.
 *
 * Kolory sa dobrane tak, by dalo sie je rozroznic jednym spojrzeniem i szybko nazwac na glos -
 * stad brak dwoch odcieni tej samej barwy (np. czerwieni i rozu, ktore przy krotkiej ekspozycji
 * latwo pomylic).
 *
 * [argb] trzymamy jako [Long], a nie jako `androidx.compose.ui.graphics.Color`, zeby model
 * pozostal niezalezny od warstwy UI (i dzialal w testach jednostkowych na czystej JVM).
 * Nazwy widoczne dla uzytkownika mieszkaja w zasobach - patrz `ui/Labels.kt`.
 */
enum class StimulusColor(
    val id: String,
    val argb: Long,
) {
    RED("red", 0xFFE53935),
    ORANGE("orange", 0xFFFB8C00),
    YELLOW("yellow", 0xFFFDD835),
    GREEN("green", 0xFF43A047),
    BLUE("blue", 0xFF1E88E5),
    PURPLE("purple", 0xFF8E24AA),
    WHITE("white", 0xFFFFFFFF),
    BLACK("black", 0xFF000000),
    ;

    /**
     * Kolor cyfry na tym tle: barwa z przeciwnej strony kola barw (negatyw barwny).
     * Dla bieli i czerni, ktore wlasnej barwy nie maja, negatywem jest druga skrajnosc jasnosci.
     *
     * Liczony raz, przy inicjalizacji enuma - w trakcie sesji odczytywany co klatke.
     */
    val contentArgb: Long = Contrast.complementary(argb)

    /**
     * Obrys cyfry - czern albo biel, zaleznie od tego, co mocniej odcina sie od tla.
     * To on odpowiada za ostra krawedz znaku i ratuje czytelnosc tam, gdzie barwa
     * dopelniajaca ma jasnosc zblizona do tla (np. niebieski i pomaranczowy).
     */
    val outlineArgb: Long = Contrast.bestContentColor(argb)

    /**
     * Czy obrys ma sens. Na bieli i czerni cyfra jest juz w kolorze obrysu, wiec rysowanie
     * go tylko pogrubialoby znak.
     */
    val hasOutline: Boolean = contentArgb != outlineArgb

    companion object {
        val ALL: List<StimulusColor> = entries

        fun fromId(id: String): StimulusColor? = entries.firstOrNull { it.id == id }
    }
}
