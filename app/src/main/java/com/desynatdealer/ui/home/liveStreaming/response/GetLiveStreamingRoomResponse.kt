package com.desynatdealer.ui.home.liveStreaming.response


import com.google.gson.annotations.SerializedName

data class GetLiveStreamingRoomResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_requests")
        val currentRequests: List<CurrentRequest>,
        @SerializedName("future_requests")
        val futureRequests: List<CurrentRequest>
    ) {
        data class CurrentRequest(
            @SerializedName("admin_verify")
            val adminVerify: Int,
            @SerializedName("cancel_reason")
            val cancelReason: String,
            @SerializedName("channel_id")
            val channelId: String,
            @SerializedName("liveToken")
            val liveToken: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("order_id")
            val orderId: Int,
            @SerializedName("reject_reason")
            val rejectReason: String,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("timeslot_details")
            val timeslotDetails: TimeslotDetails,
            @SerializedName("timeslot_id")
            val timeslotId: Int,
            @SerializedName("uid")
            val uid: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class TimeslotDetails(
                @SerializedName("country_id")
                val countryId: Int,
                @SerializedName("country_info")
                val countryInfo: CountryInfo,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("date")
                val date: String,
                @SerializedName("from")
                val from: String,
                @SerializedName("id")
                val id: Int,
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
    }
}