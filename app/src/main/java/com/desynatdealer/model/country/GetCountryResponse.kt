package com.desynatdealer.model.country


import com.google.gson.annotations.SerializedName

data class GetCountryResponse(
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
        @SerializedName("currency_code")
        val currencyCode: String,
        @SerializedName("currency_name")
        val currencyName: String,
        @SerializedName("currency_symbol")
        val currencySymbol: String,
        @SerializedName("dial_code")
        val dialCode: String,
        @SerializedName("exchange_rate_to_kwd")
        val exchangeRateToKwd: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("flag1")
        val flag1: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("iso_code")
        val isoCode: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}