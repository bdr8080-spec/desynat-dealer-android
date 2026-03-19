package com.desynatdealer.fcm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationInfoModel(
    val title: String? = "",
    val body: String? = "",
    val orderId: String? = "",
) : Parcelable