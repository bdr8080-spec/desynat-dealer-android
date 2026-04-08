package com.desynatdealer.ui.home.cms.response


import com.google.gson.annotations.SerializedName

data class GetAllRaisedTicketResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
{
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
            @SerializedName("description")
            val description: String,
            @SerializedName("file")
            val `file`: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("sender_details")
            val senderDetails: SenderDetails,
            @SerializedName("sender_id")
            val senderId: Int,
            @SerializedName("sender_type")
            val senderType: Int,
            @SerializedName("status")
            val status: Int,
            @SerializedName("ticket_id")
            val ticketId: String,
            @SerializedName("ticket_type_details")
            val ticketTypeDetails: TicketTypeDetails,
            @SerializedName("ticket_type_id")
            val ticketTypeId: Int,
            @SerializedName("title")
            val title: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class SenderDetails(
                @SerializedName("accessToken")
                val accessToken: String,
                @SerializedName("affiliated_userdetails")
                val affiliatedUserdetails: Any,
                @SerializedName("banner_view")
                val bannerView: String,
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("device_token")
                val deviceToken: Any,
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
                @SerializedName("lang")
                val lang: String,
                @SerializedName("let")
                val let: Any,
                @SerializedName("long")
                val long: Any,
                @SerializedName("mobile")
                val mobile: String,
                @SerializedName("otp")
                val otp: String,
                @SerializedName("otp_verify")
                val otpVerify: String,
                @SerializedName("profile_image")
                val profileImage: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class TicketTypeDetails(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("name")
                val name: String,
                @SerializedName("status")
                val status: Int,
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