package com.desynatdealer.ui.home.liveStreaming.response


import com.google.gson.annotations.SerializedName

data class GetLiveUserCountResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("success")
    val success: Boolean
) {
    data class Data(
        @SerializedName("audience")
        val audience: List<Int>,
        @SerializedName("audience_total")
        val audienceTotal: Int,
        @SerializedName("broadcasters")
        val broadcasters: List<Int>,
        @SerializedName("channel_exist")
        val channelExist: Boolean,
        @SerializedName("mode")
        val mode: Int
    )
}