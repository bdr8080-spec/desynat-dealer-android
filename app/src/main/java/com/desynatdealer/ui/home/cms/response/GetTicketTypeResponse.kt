package com.desynatdealer.ui.home.cms.response


import com.google.gson.annotations.SerializedName

data class GetTicketTypeResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("name_arabic")
        val nameArabic: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}