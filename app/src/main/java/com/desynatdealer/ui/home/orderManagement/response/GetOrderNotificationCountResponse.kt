package com.desynatdealer.ui.home.orderManagement.response


import com.google.gson.annotations.SerializedName

data class GetOrderNotificationCountResponse(
    @SerializedName("data")
    val `data`: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)