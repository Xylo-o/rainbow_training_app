package com.rainbow.training.core.model

/**
 * Tryb treningu - decyduje, jakie bodzce pojawiaja sie na ekranie.
 * Nazwy i opisy widoczne dla uzytkownika mieszkaja w zasobach - patrz `ui/Labels.kt`.
 */
enum class StimulusMode(val id: String) {
    COLORS("colors"),
    DIGITS("digits"),
    MIXED("mixed"),
    ;

    /** Czy ten tryb moze wyswietlic cyfre (a wiec czy wybor cyfr ma znaczenie). */
    val usesDigits: Boolean get() = this != COLORS

    companion object {
        fun fromId(id: String): StimulusMode? = entries.firstOrNull { it.id == id }
    }
}
