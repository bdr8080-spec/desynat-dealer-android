package com.desynatdealer.ui.home.liveStreaming.response


import com.google.gson.annotations.SerializedName

data class GetAgoraTokenResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("uid")
    val uid: String
)