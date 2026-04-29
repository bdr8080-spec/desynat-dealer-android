package com.desynatdealer.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetNotificationCountResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("chat_unread_count")
        val chatUnreadCount: Int,
        @SerializedName("notification_count")
        val notificationCount: Int
    )
}