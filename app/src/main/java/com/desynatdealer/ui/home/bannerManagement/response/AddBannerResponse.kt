package com.desynatdealer.ui.home.bannerManagement.response


import com.google.gson.annotations.SerializedName

data class AddBannerResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("banner_position")
        val bannerPosition: String,
        @SerializedName("category")
        val category: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("end_date")
        val endDate: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("price")
        val price: String,
        @SerializedName("seller_id")
        val sellerId: Int,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("subcatgory")
        val subcatgory: String,
        @SerializedName("total_days")
        val totalDays: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("upload_banner")
        val uploadBanner: String,
        @SerializedName("transaction_id")
        val transactionId: String,
        @SerializedName("payment_status")
        val paymentStatus: String,
    )
}