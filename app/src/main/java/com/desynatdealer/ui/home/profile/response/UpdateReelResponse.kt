package com.desynatdealer.ui.home.profile.response


import com.google.gson.annotations.SerializedName

data class UpdateReelResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String?="",
        @SerializedName("file")
        val `file`: String?="",
        @SerializedName("id")
        val id: String?="",
        @SerializedName("seller_id")
        val sellerId: String?="",
        @SerializedName("status")
        var status: String?="",
        @SerializedName("title")
        val title: String?="",
        @SerializedName("type")
        val type: String?="",
        @SerializedName("updated_at")
        val updatedAt: String?=""
    )
}