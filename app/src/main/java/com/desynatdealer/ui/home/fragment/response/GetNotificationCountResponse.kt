package com.desynatdealer.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetNotificationCountResponse(
    @SerializedName("data")
    val `data`: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)