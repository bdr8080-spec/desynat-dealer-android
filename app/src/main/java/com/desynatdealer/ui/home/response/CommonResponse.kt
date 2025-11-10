package com.desynatdealer.ui.home.response


import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)