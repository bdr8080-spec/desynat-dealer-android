package com.desynatdealer.ui.home.cms.response


import com.google.gson.annotations.SerializedName

data class UpdateNotificationResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("accept_terms")
        val acceptTerms: String,
        @SerializedName("admin_verify")
        val adminVerify: String,
        @SerializedName("affilate_commission")
        val affilateCommission: Any,
        @SerializedName("app_notification")
        val appNotification: Int,
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
        @SerializedName("notification")
        val notification: Int,
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
}