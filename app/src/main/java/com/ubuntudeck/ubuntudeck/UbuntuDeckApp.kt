package com.ubuntudeck.ubuntudeck

import android.app.Application
import timber.log.Timber

class UbuntuDeckApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
