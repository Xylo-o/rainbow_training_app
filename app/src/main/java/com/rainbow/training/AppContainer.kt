package com.rainbow.training

import android.content.Context
import com.rainbow.training.account.AnonymousUserSession
import com.rainbow.training.account.UserSession
import com.rainbow.training.core.data.DataStoreSettingsRepository
import com.rainbow.training.core.data.SettingsRepository
import com.rainbow.training.monetization.AdHost
import com.rainbow.training.monetization.NoAds

/**
 * Recznie skladany kontener zaleznosci - jedno miejsce, w ktorym powstaja obiekty zyjace
 * tak dlugo jak aplikacja. Przy dokladaniu kont czy reklam wystarczy podmienic implementacje
 * tutaj; ekrany widza wylacznie interfejsy.
 */
class AppContainer(context: Context) {

    val settingsRepository: SettingsRepository = DataStoreSettingsRepository(context)

    val userSession: UserSession = AnonymousUserSession

    /** Podmien na implementacje z AdMob, gdy przyjdzie czas na reklamy. */
    val adHost: AdHost = NoAds
}
