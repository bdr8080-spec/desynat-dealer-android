package com.desynatdealer.fcm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationInfoModel(
    val title: String? = null,
    val body: String? = null,
    val title_en: String? = null,
    val body_en: String? = null,
    val title_ar: String? = null,
    val body_ar: String? = null,
    val badge: Int? = 0,) : Parcelable