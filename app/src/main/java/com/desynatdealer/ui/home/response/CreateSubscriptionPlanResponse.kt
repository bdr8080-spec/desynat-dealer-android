package com.desynatdealer.ui.home.response


import com.google.gson.annotations.SerializedName

data class CreateSubscriptionPlanResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("payment_method")
        val paymentMethod: String,
        @SerializedName("payment_status")
        val paymentStatus: String,
        @SerializedName("plan_end_date")
        val planEndDate: String,
        @SerializedName("plan_id")
        val planId: String,
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
    )
}