package com.rainbow.training.monetization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Punkt wpiecia reklam. Dzis nie ma ich w aplikacji i [NoAds] nie rysuje niczego.
 *
 * Miejsca wywolania sa juz na swoich miejscach, wiec wlaczenie reklam nie bedzie wymagalo
 * ruszania ekranow - wystarczy dostarczyc inna implementacje w
 * [com.rainbow.training.AppContainer]:
 *
 * 1. dodaj `com.google.android.gms:play-services-ads` i identyfikator wydawcy w manifescie,
 * 2. zainicjalizuj SDK w `RainbowApp.onCreate()`,
 * 3. napisz `AdMobAdHost` implementujacy ten interfejs.
 *
 * Ekran sesji celowo nie wola zadnej z tych metod - w trakcie treningu nic nie ma prawa
 * pojawic sie na ekranie.
 */
interface AdHost {

    /** Baner u dolu ekranu ustawien i podsumowania. */
    @Composable
    fun Banner(modifier: Modifier)

    /**
     * Wolane raz po kazdej ukonczonej sesji, zanim pokaze sie podsumowanie.
     * Implementacja z reklamami pokazuje tu pelnoekranowa reklame co
     * [INTERSTITIAL_EVERY_N_SESSIONS] sesji - nigdy w trakcie treningu.
     */
    fun onSessionCompleted()

    companion object {
        /** Co ile ukonczonych sesji wypada pelnoekranowa reklama. */
        const val INTERSTITIAL_EVERY_N_SESSIONS = 3
    }
}

/** Domyslna implementacja: aplikacja bez reklam. */
object NoAds : AdHost {

    @Composable
    override fun Banner(modifier: Modifier) = Unit

    override fun onSessionCompleted() = Unit
}

/** Dostep do [AdHost] z dowolnego miejsca w drzewie Compose, bez przekazywania przez parametry. */
val LocalAdHost = staticCompositionLocalOf<AdHost> { NoAds }
