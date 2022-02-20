package com.noonlight.apps

import android.app.Application
import timber.log.Timber

class NoonlightApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Use Timber for logging.
        Timber.plant(Timber.DebugTree())
    }
}