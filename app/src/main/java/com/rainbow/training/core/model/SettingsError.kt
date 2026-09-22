package com.rainbow.training.core.model

/**
 * Powod, dla ktorego ustawien nie da sie uruchomic.
 *
 * Model zwraca przyczyne, a nie gotowy komunikat - dzieki temu warstwa `core/` nie zna
 * zasobow Androida ani jezyka interfejsu, a tlumaczenie zostaje po stronie UI.
 */
enum class SettingsError {
    TOO_FEW_COLORS,
    TOO_FEW_DIGITS,
    INTERVAL_TOO_SHORT,
    INTERVAL_TOO_LONG,
    INTERVAL_REVERSED,
    SESSION_TOO_SHORT,
    SESSION_TOO_LONG,
}
