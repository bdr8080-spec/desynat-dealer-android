package com.desynatdealer.ui.home.cms.response


import com.google.gson.annotations.SerializedName

data class GetContactResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("content_arabic")
        val contentArabic: ContentEnglish,
        @SerializedName("content_english")
        val contentEnglish: ContentEnglish,
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
    ) {
        data class ContentEnglish(
            @SerializedName("email")
            val email: String,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("whatsapp")
            val whatsapp: String
        )
    }
}