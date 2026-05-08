package com.desynatdealer.ui.home.productManagement.response


import com.google.gson.annotations.SerializedName

data class GetColorResponse(
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
        @SerializedName("hex_code")
        val hexCode: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("name_ar")
        val nameArabic: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}