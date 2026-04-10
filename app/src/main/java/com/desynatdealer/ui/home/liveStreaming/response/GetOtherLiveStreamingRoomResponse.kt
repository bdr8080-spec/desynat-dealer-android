package com.desynatdealer.ui.home.liveStreaming.response


import com.google.gson.annotations.SerializedName

data class GetOtherLiveStreamingRoomResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
{
    data class Data(
        @SerializedName("admin_verify")
        val adminVerify: Int,
        @SerializedName("cancel_reason")
        val cancelReason: String,
        @SerializedName("channel_id")
        val channelId: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("liveToken")
        val liveToken: String,
        @SerializedName("order_id")
        val orderId: Int,
        @SerializedName("reject_reason")
        val rejectReason: String,
        @SerializedName("seller_details")
        val sellerDetails: SellerDetails,
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
        data class SellerDetails(
            @SerializedName("accept_terms")
            val acceptTerms: String,
            @SerializedName("admin_verify")
            val adminVerify: String,
            @SerializedName("affilate_commission")
            val affilateCommission: Any,
            @SerializedName("bank_information")
            val bankInformation: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("device_token")
            val deviceToken: String,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("fcm_token")
            val fcmToken: String,
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_block")
            val isBlock: Int,
            @SerializedName("lang")
            val lang: String,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("otp")
            val otp: String,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("profile_pic")
            val profilePic: String,
            @SerializedName("profile_qr")
            val profileQr: String,
            @SerializedName("qrSellerUrl")
            val qrSellerUrl: String,
            @SerializedName("reject_reason")
            val rejectReason: Any,
            @SerializedName("store_information")
            val storeInformation: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

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