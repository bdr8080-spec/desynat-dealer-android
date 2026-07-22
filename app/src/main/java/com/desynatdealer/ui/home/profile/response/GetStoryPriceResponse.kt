package com.desynatdealer.ui.home.profile.response


import com.google.gson.annotations.SerializedName

data class GetStoryPriceResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val country_id: Int,
        val country_info: CountryInfo,
        val created_at: String,
        val id: Int,
        val per_day_price: String,
        val total_amount: Int,
        val updated_at: String
    ) {
        data class CountryInfo(
            val created_at: String,
            val currency_code: String,
            val currency_code_ar: String,
            val currency_name: String,
            val currency_symbol: String,
            val dial_code: String,
            val exchange_rate_to_kwd: String,
            val flag: String,
            val flag1: String,
            val id: Int,
            val iso_code: String,
            val name: String,
            val name_ar: String,
            val updated_at: String
        )
    }
}