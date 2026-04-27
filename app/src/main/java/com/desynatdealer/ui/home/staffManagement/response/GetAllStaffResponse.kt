package com.desynatdealer.ui.home.staffManagement.response


import com.google.gson.annotations.SerializedName

data class GetAllStaffResponse(
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
            var isBlock: Int,
            @SerializedName("lang")
            val lang: Any,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("name_ar")
            val nameAr: String,
            @SerializedName("notification")
            val notification: Int,
            @SerializedName("otp")
            val otp: Any,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("parent_id")
            val parentId: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("profile_pic")
            val profilePic: String,
            @SerializedName("profile_qr")
            val profileQr: Any,
            @SerializedName("qrSellerUrl")
            val qrSellerUrl: Any,
            @SerializedName("reject_reason")
            val rejectReason: Any,
            @SerializedName("role__action")
            val roleAction: List<RoleAction>,
            @SerializedName("save_password")
            val savePassword: String,
            @SerializedName("select_category")
            val selectCategory: String,
            @SerializedName("seller_category")
            val sellerCategory: SellerCategory,
            @SerializedName("store_information")
            val storeInformation: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class RoleAction(
                @SerializedName("can_action")
                val canAction: Int,
                @SerializedName("can_view")
                val canView: Int,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("menu_id")
                val menuId: String,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("sellermenu")
                val sellermenu: List<Sellermenu>,
                @SerializedName("updated_at")
                val updatedAt: String
            ) {
                data class Sellermenu(
                    @SerializedName("active_patterns")
                    val activePatterns: String,
                    @SerializedName("created_at")
                    val createdAt: Any,
                    @SerializedName("icon")
                    val icon: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("order_by")
                    val orderBy: Int,
                    @SerializedName("parent_id")
                    val parentId: Any,
                    @SerializedName("route")
                    val route: String,
                    @SerializedName("status")
                    val status: Int,
                    @SerializedName("title")
                    val title: String,
                    @SerializedName("updated_at")
                    val updatedAt: Any
                )
            }

            data class SellerCategory(
                @SerializedName("created_at")
                val createdAt: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("name_ar")
                val nameAr: String,
                @SerializedName("name_en")
                val nameEn: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("updated_at")
                val updatedAt: Any
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