package com.rainbow.training

import android.app.Application

class RainbowApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Tu trafi inicjalizacja przyszlych SDK (reklamy, logowanie, analityka).
    }
}
