package com.desynatdealer

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.agora.AgoraManager
import com.google.android.libraries.places.api.Places
import com.google.firebase.messaging.FirebaseMessaging
import com.myfatoorah.sdk.enums.MFCountry
import com.myfatoorah.sdk.enums.MFEnvironment
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application() {

    companion object {
        @JvmStatic
        var instance: CoreApplication? = null
        var notificationManager: NotificationManager? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CRASH_LOG", "Uncaught: ${throwable.message}", throwable)
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.map_key))
        }

       /* MFSDK.init(
            getString(R.string.my_fatoorah_token),
            MFCountry.KUWAIT,
            MFEnvironment.LIVE
        ) */

        MFSDK.init(
            getString(R.string.fatoorah),
            MFCountry.KUWAIT,
            MFEnvironment.TEST
        )

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_ERROR", "Token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", token)
            SharedPreference.get(this).deviceToken = token
        }

//        AgoraManager.initialize(this, getString(R.string.agora_app_id))
    }
}