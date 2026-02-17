package com.desynatdealer.ui.home.liveCoverage.response

import com.google.gson.annotations.SerializedName
data class GetLiveRequestResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val data: List<Data>,
        @SerializedName("first_page_url")
        val firstPageUrl: String,
        @SerializedName("from")
        val from: Int,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("last_page_url")
        val lastPageUrl: String,
        @SerializedName("links")
        val links: List<Link>,
        @SerializedName("next_page_url")
        val nextPageUrl: String,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: String,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("admin_verify")
            val adminVerify: String,
            @SerializedName("cancel_reason")
            val cancelReason: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("order_id")
            val orderId: String,
            @SerializedName("reject_reason")
            val rejectReason: String,
            @SerializedName("seller_id")
            val sellerId: String,
            @SerializedName("timeslot_details")
            val timeslotDetails: TimeslotDetails,
            @SerializedName("timeslot_id")
            val timeslotId: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class TimeslotDetails(
                @SerializedName("country_id")
                val countryId: String,
                @SerializedName("country_info")
                val countryInfo: CountryInfo,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("date")
                val date: String,
                @SerializedName("from")
                val from: String,
                @SerializedName("id")
                val id: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("to")
                val to: String,
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
                    val id: String,
                    @SerializedName("iso_code")
                    val isoCode: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }
        }

        data class Link(
            @SerializedName("active")
            val active: Boolean,
            @SerializedName("label")
            val label: String,
            @SerializedName("url")
            val url: String
        )
    }
}