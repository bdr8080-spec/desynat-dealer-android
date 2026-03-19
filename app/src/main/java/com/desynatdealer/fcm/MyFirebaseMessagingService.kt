package com.desynatdealer.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.desynatdealer.fcm.NotificationInfoModel
import com.desynatdealer.CoreApplication
import com.desynatdealer.R
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.utils.SharedPreference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        SharedPreference.get(applicationContext).deviceToken = p0
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("onMessageReceived: ", remoteMessage?.data.toString())
        Log.e("onMessageReceived: ", remoteMessage?.notification.toString())
//        val obj = JSONObject(remoteMessage.data as Map<String, String>).toString()
//        val notificationData = Gson().fromJson(obj, NotificationInfoModel::class.java)
//        sendNotification(notificationData)
//        Log.e("onMessageReceived: data", notificationData.toString())
//
//        if (Constants.currentScreen=="OrderDetail"||Constants.currentScreen=="Order"){
//            EventBus.getDefault().post(notificationData)
//              /*  when(notificationData.title){
//
//                    "Confirmed"->{
//
//                    }
//                    "Preparing"->{
//
//                    }
//                    "Completed"->{
//
//                    }
//                    else ->{
//
//                    }
//                }*/
//        }

    }

    @SuppressLint("InvalidWakeLockTag")
    private fun sendNotification(notificationData: NotificationInfoModel) {

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("from", "push")
        intent.putExtra("notificationData", notificationData)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        var pendingIntent: PendingIntent? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        else {
            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val message = notificationData.body
        val title = getString(R.string.app_name)
        val channelId = getString(R.string.channel)
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_app)

        val mPowerManager = this.getSystemService(POWER_SERVICE) as PowerManager
        var wl: PowerManager.WakeLock? = null
        wl = mPowerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "tag"
        )
        wl?.acquire(1000)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_app)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setLargeIcon(largeIcon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val vibrate = longArrayOf(0, 100, 200, 300)
        notificationBuilder.setVibrate(vibrate)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.channel),
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = getString(R.string.channel_description)
            channel.setSound(defaultSoundUri, null)

            CoreApplication.notificationManager?.createNotificationChannel(channel)
        }
        val notificationId = System.currentTimeMillis().toInt()
        CoreApplication.notificationManager?.notify(notificationId, notificationBuilder.build())
    }
}