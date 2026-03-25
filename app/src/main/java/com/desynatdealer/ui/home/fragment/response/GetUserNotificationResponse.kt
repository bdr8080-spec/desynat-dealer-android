package com.desynatdealer.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetUserNotificationResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<Data>,
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
        val nextPageUrl: Any,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("description_arabic")
            val descriptionArabic: String,
            @SerializedName("description_english")
            val descriptionEnglish: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_read")
            var isRead: Int,
            @SerializedName("notification_type")
            val notificationType: Any,
            @SerializedName("receiver_details")
            val receiverDetails: ReceiverDetails,
            @SerializedName("receiver_id")
            val receiverId: Int,
            @SerializedName("receiver_type")
            val receiverType: Int,
            @SerializedName("sender_details")
            val senderDetails: SenderDetails,
            @SerializedName("sender_id")
            val senderId: Int,
            @SerializedName("sender_type")
            val senderType: Int,
            @SerializedName("title_arabic")
            val titleArabic: String,
            @SerializedName("title_english")
            val titleEnglish: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class ReceiverDetails(
                @SerializedName("accept_terms")
                val acceptTerms: String,
                @SerializedName("admin_verify")
                val adminVerify: String,
                @SerializedName("affilate_commission")
                val affilateCommission: String,
                @SerializedName("bank_information")
                val bankInformation: String,
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("device_token")
                val deviceToken: Any,
                @SerializedName("device_type")
                val deviceType: Any,
                @SerializedName("email")
                val email: String,
                @SerializedName("fcm_token")
                val fcmToken: Any,
                @SerializedName("first_name")
                val firstName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_block")
                val isBlock: Int,
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

            data class SenderDetails(
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("device_type")
                val deviceType: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("email_verified_at")
                val emailVerifiedAt: Any,
                @SerializedName("fcm_token")
                val fcmToken: String,
                @SerializedName("full_name")
                val fullName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("mobile")
                val mobile: String,
                @SerializedName("otp")
                val otp: Any,
                @SerializedName("otp_verify")
                val otpVerify: String,
                @SerializedName("profile_pic")
                val profilePic: Any,
                @SerializedName("updated_at")
                val updatedAt: String
            )
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