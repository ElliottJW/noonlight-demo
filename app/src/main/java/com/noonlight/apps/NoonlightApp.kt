package com.noonlight.apps

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NoonlightApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Use Timber for logging.
        Timber.plant(Timber.DebugTree())
    }
}