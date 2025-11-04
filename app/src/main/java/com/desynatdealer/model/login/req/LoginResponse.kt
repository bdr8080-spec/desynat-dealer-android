package com.desynatdealer.model.login.req


import com.google.gson.annotations.SerializedName

data class LoginResponse(
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
        @SerializedName("accessToken")
        val accessToken: String,
        @SerializedName("admin_verify")
        val adminVerify: String,
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
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("lang")
        val lang: Any,
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
        @SerializedName("reject_reason")
        val rejectReason: Any,
        @SerializedName("store_information")
        val storeInformation: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}