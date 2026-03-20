package com.desynatdealer.ui.home.mySubscriptionPlan.response


import com.google.gson.annotations.SerializedName

data class GetMyPlanResponse(
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
            @SerializedName("amount")
            val amount: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_expired")
            val isExpired: String,
            @SerializedName("online_transaction_id")
            val onlineTransactionId: Any,
            @SerializedName("payment_method")
            val paymentMethod: String,
            @SerializedName("payment_status")
            val paymentStatus: String,
            @SerializedName("plan_end_date")
            val planEndDate: String,
            @SerializedName("plan_id")
            val planId: Int,
            @SerializedName("plan_info")
            val planInfo: PlanInfo,
            @SerializedName("plan_start_date")
            val planStartDate: String,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("transaction_id")
            val transactionId: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class PlanInfo(
                @SerializedName("commission")
                val commission: Int,
                @SerializedName("commission_percentage")
                val commissionPercentage: Int,
                @SerializedName("country_id")
                val countryId: Int,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("discounted_price")
                val discountedPrice: Any,
                @SerializedName("duration")
                val duration: Int,
                @SerializedName("duration_type")
                val durationType: Int,
                @SerializedName("id")
                val id: Int,
                @SerializedName("new_duration")
                val newDuration: Any,
                @SerializedName("new_duration_type")
                val newDurationType: Any,
                @SerializedName("no_of_employee")
                val noOfEmployee: Int,
                @SerializedName("no_of_highlights")
                val noOfHighlights: Int,
                @SerializedName("no_of_products")
                val noOfProducts: Int,
                @SerializedName("no_of_videos")
                val noOfVideos: Int,
                @SerializedName("plan_name_arabic")
                val planNameArabic: String,
                @SerializedName("plan_name_english")
                val planNameEnglish: String,
                @SerializedName("price")
                val price: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("stories")
                val stories: Int,
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