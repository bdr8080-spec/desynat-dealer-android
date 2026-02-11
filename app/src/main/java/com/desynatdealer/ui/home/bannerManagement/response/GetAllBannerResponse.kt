package com.desynatdealer.ui.home.bannerManagement.response


import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.google.gson.annotations.SerializedName

data class GetAllBannerResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("Admin_verify")
        val adminVerify: String,
        @SerializedName("banner_position")
        val bannerPosition: String,
        @SerializedName("cancelled_reason")
        val cancelledReason: String,
        @SerializedName("category")
        val category: String,
        @SerializedName("category_details")
        val categoryDetails: GetCategoryResponse.Data,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("end_date")
        val endDate: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("payment_status")
        var paymentStatus: String,
        @SerializedName("price")
        val price: Double,
        @SerializedName("seller_id")
        val sellerId: Int,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("subcategory_details")
        val subcategoryDetails: GetSubCategoryResponse.Data,
        @SerializedName("subcatgory")
        val subcatgory: Any,
        @SerializedName("total_days")
        val totalDays: String,
        @SerializedName("transaction_id")
        var transactionId: Any,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("upload_banner")
        val uploadBanner: String
    )
}