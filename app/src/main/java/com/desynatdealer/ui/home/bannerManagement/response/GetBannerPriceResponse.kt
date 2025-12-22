package com.desynatdealer.ui.home.bannerManagement.response


import com.google.gson.annotations.SerializedName

data class GetBannerPriceResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("per_day_price")
        val perDayPrice: Double,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}