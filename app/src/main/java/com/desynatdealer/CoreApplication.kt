package com.desynatdealer

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application() {

    companion object {
        @JvmStatic
        var instance: CoreApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this


        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.map_key))
        }

    }
}