package com.rainbow.training.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Motyw aplikacji - celowo tylko ciemny.
 *
 * Interfejs jest neutralnym, ciemnym tlem dla wlasciwej tresci: pelnoekranowych bodzcow.
 * Dzieki temu kolory z palety nie konkuruja z kolorami UI, a oczy nie meczaza sie miedzy sesjami.
 */
private val RainbowDarkColors = darkColorScheme(
    primary = Color(0xFF69D2FF),
    onPrimary = Color(0xFF00202E),
    primaryContainer = Color(0xFF004C69),
    onPrimaryContainer = Color(0xFFBFE9FF),
    secondary = Color(0xFFB9C8D6),
    onSecondary = Color(0xFF23323E),
    background = Color(0xFF0E0F13),
    onBackground = Color(0xFFE4E6EB),
    surface = Color(0xFF14161C),
    onSurface = Color(0xFFE4E6EB),
    surfaceVariant = Color(0xFF232834),
    onSurfaceVariant = Color(0xFFB6BDCA),
    outline = Color(0xFF515867),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun RainbowTrainingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RainbowDarkColors,
        content = content,
    )
}
