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
import com.google.gson.Gson
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import kotlin.collections.get
import kotlin.inc

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        SharedPreference.get(applicationContext).deviceToken = p0
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e("onMessageReceived: ", remoteMessage?.data.toString())
        Log.e("onMessageReceived: ", remoteMessage?.notification.toString())

        Log.e("onMessageReceived", "From: ${remoteMessage.from}")
        Log.e("onMessageReceived", "MessageId: ${remoteMessage.messageId}")
        Log.e("onMessageReceived", "SentTime: ${remoteMessage.sentTime}")
        Log.e("onMessageReceived", "TTL: ${remoteMessage.ttl}")
        Log.e("onMessageReceived", "CollapseKey: ${remoteMessage.collapseKey}")

        if (remoteMessage.data.isNotEmpty()) {
            val obj = JSONObject(remoteMessage.data as Map<String, String>).toString()
            val notificationData = Gson().fromJson(obj, NotificationInfoModel::class.java)
            // sendNotification(notificationData)
            sendNotification(notificationData?.title_en?:notificationData?.title.toString(),notificationData?.body_en?:notificationData?.body.toString(),notificationData?.badge?:0,notificationData?.title_ar?:notificationData?.title.toString(),notificationData?.body_ar?:notificationData?.body.toString())
//            if (Constants.currentScreen=="home"||Constants.currentScreen=="notification") {
//                EventBus.getDefault().post(notificationData?.title.toString())
//            }
        }
        else{
            remoteMessage.notification?.let {
                Log.e("onMessageReceived", "Notification Title: ${it.title}")
                Log.e("onMessageReceived", "Notification Body: ${it.body}")
                Log.e("onMessageReceived", "Notification Tag: ${it.tag}")
                Log.e("onMessageReceived", "Notification ChannelId: ${it.channelId}")
                Log.e("onMessageReceived", "Notification ClickAction: ${it.clickAction}")
                var badgeCount = SharedPreference.get(applicationContext).badgeCount
                badgeCount++
                sendNotification(it.title.toString(),it.body.toString(), badge = badgeCount )

//                if (Constants.currentScreen=="home"||Constants.currentScreen=="notification") {
//                    EventBus.getDefault().post(it.title.toString())
//                }
            }

        }

    }

    @SuppressLint("InvalidWakeLockTag")
    private fun sendNotification(titleText: String, body: String,badge: Int,titleAr: String?="",bodyAr: String?="") {
        //   private fun sendNotification(notificationData: NotificationInfoModel) {
        var  badgeCount = badge


        SharedPreference.get(applicationContext).badgeCount = badgeCount

        //   private fun sendNotification(notificationData: NotificationInfoModel) {

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("from", "push")
        //   intent.putExtra("notificationData", notificationData)
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

        val message = if(SharedPreference.get(applicationContext).language=="ar") bodyAr?:body else body
        val title = if(SharedPreference.get(applicationContext).language=="ar") titleAr?:titleText else titleText

//        val message = notificationData.body
//        val title = notificationData.title
        //  val title = getString(R.string.app_name)
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
            .setNumber(badgeCount)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
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
            channel.setShowBadge(true)
            CoreApplication.notificationManager?.createNotificationChannel(channel)
        }
        //  val notificationId = System.currentTimeMillis().toInt()
        val notificationId = 1
        val notification = notificationBuilder.build()
        notification.number = badgeCount
        CoreApplication.notificationManager?.notify(notificationId, notification)
        ShortcutBadger.applyCount(applicationContext, badgeCount)
    }
}