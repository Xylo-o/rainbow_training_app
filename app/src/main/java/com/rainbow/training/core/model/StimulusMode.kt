package com.rainbow.training.core.model

/** Tryb treningu - decyduje, jakie bodzce pojawiaja sie na ekranie. */
enum class StimulusMode(
    val id: String,
    val displayName: String,
    val description: String,
) {
    COLORS(
        id = "colors",
        displayName = "Kolory",
        description = "Cały ekran wypełnia jeden kolor z palety.",
    ),
    DIGITS(
        id = "digits",
        displayName = "Cyfry",
        description = "Duża cyfra na kolorowym tle, w maksymalnie kontrastowym kolorze.",
    ),
    MIXED(
        id = "mixed",
        displayName = "Mieszany",
        description = "Losowo przeplatane: raz sam kolor, raz kolor z cyfrą.",
    ),
    ;

    /** Czy ten tryb moze wyswietlic cyfre (a wiec czy wybor cyfr ma znaczenie). */
    val usesDigits: Boolean get() = this != COLORS

    companion object {
        fun fromId(id: String): StimulusMode? = entries.firstOrNull { it.id == id }
    }
}
