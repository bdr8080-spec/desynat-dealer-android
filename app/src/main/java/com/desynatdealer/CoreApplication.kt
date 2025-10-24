package com.desynatdealer

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application(){
override fun onCreate() {
    super.onCreate()

    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, getString(R.string.map_key))
    }

}
}