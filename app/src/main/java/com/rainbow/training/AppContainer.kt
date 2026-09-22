package com.rainbow.training

import android.content.Context
import com.rainbow.training.account.AnonymousUserSession
import com.rainbow.training.account.UserSession
import com.rainbow.training.core.data.DataStoreSettingsRepository
import com.rainbow.training.core.data.SettingsRepository

/**
 * Recznie skladany kontener zaleznosci - jedno miejsce, w ktorym powstaja obiekty zyjace
 * tak dlugo jak aplikacja. Przy dokladaniu kont czy reklam wystarczy podmienic implementacje
 * tutaj; ekrany widza wylacznie interfejsy.
 */
class AppContainer(context: Context) {

    val settingsRepository: SettingsRepository = DataStoreSettingsRepository(context)

    val userSession: UserSession = AnonymousUserSession
}
