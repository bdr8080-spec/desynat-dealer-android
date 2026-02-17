package com.desynatdealer.ui.home.liveCoverage.response


import com.google.gson.annotations.SerializedName

data class GetTimeSlotResponse(
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
        @SerializedName("country_info")
        val countryInfo: CountryInfo,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("date")
        val date: String,
        @SerializedName("from")
        var from: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("status")
        var status: Int,
        @SerializedName("to")
        var to: String,
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class CountryInfo(
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
}