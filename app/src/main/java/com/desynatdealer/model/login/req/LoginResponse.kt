package com.desynatdealer.model.login.req


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
{
    data class Data(
        @SerializedName("accept_terms")
        val acceptTerms: String,
        @SerializedName("accessToken")
        val accessToken: String,
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
        val deviceType: Int,
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
        @SerializedName("name_ar")
        val nameAr: String,
        @SerializedName("notification")
        val notification: Int,
        @SerializedName("otp")
        val otp: Any,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("staffaccessToken")
        val staffaccessToken: String,
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
        @SerializedName("store_information")
        val storeInformation: String,
        @SerializedName("subcription_plan")
        val subcriptionPlan: List<Any>,
        @SerializedName("subcription_plan_check")
        val subcriptionPlanCheck: String,
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
    }
}