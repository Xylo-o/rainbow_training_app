package com.rainbow.training.monetization

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Zarezerwowane miejsce na baner reklamowy.
 *
 * Dzis nie rysuje nic i nie zajmuje miejsca - w aplikacji nie ma reklam. Gdy przyjdzie czas:
 *
 * 1. dodaj zaleznosc `com.google.android.gms:play-services-ads` i identyfikator wydawcy
 *    w `AndroidManifest.xml`,
 * 2. zainicjalizuj SDK w `RainbowApp.onCreate()`,
 * 3. podmien cialo tej funkcji na `AndroidView { AdView(it).apply { ... } }`.
 *
 * Sloty sa juz wywolywane w miejscach, w ktorych reklama nie przeszkodzi w treningu:
 * na ekranie ustawien i na ekranie podsumowania. Ekran sesji celowo ich nie ma.
 */
@Composable
fun AdBannerSlot(modifier: Modifier = Modifier) {
    // Pusto - brak reklam w tej wersji.
}
