package com.desynatdriver.ui.home.cms.response


import com.google.gson.annotations.SerializedName

data class GetCmsResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("content_arabic")
        val contentArabic: String,
        @SerializedName("content_english")
        val contentEnglish: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("slug")
        val slug: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}