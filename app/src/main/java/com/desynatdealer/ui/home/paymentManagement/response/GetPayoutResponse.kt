package com.desynatdealer.ui.home.paymentManagement.response

import com.google.gson.annotations.SerializedName

data class GetPayoutResponse(
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val `data`: Data? = null
) {
    data class Data(
        @SerializedName("currency_code")
        val currencyCode: String? = null,
        @SerializedName("currency_symbol")
        val currencySymbol: String? = null,
        @SerializedName("total_revenue")
        val totalRevenue: String? = null,
        @SerializedName("total_commission")
        val totalCommission: String? = null,
        @SerializedName("total_earning")
        val totalEarning: String? = null,
        @SerializedName("total_paid")
        val totalPaid: String? = null,
        @SerializedName("pending_amount")
        val pendingAmount: String? = null,
        @SerializedName("last_payout")
        val lastPayout: String? = null,
        @SerializedName("last_payout_date")
        val lastPayoutDate: String? = null,
        @SerializedName("from_date")
        val fromDate: String? = null,
        @SerializedName("to_date")
        val toDate: String? = null,
        @SerializedName("payouts")
        val payouts: Payouts? = null,
        @SerializedName("payout_history")
        val payoutHistory: PayoutHistory? = null
    ) {
        data class Payouts(
            @SerializedName("current_page")
            val currentPage: Int? = null,
            @SerializedName("data")
            val `data`: List<PayoutData>? = null,
            @SerializedName("last_page")
            val lastPage: Int? = null,
            @SerializedName("per_page")
            val perPage: Int? = null,
            @SerializedName("total")
            val total: Int? = null
        ) {
            data class PayoutData(
                @SerializedName("id")
                val id: Int? = null,
                @SerializedName("request_id")
                val requestId: String? = null,
                @SerializedName("amount")
                val amount: String? = null,
                @SerializedName("transaction_id")
                val transactionId: String? = null,
                @SerializedName("payment_status")
                val paymentStatus: String? = null,
                @SerializedName("payment_receive_date")
                val paymentReceiveDate: String? = null,
                @SerializedName("payout_date")
                val payoutDate: String? = null,
                @SerializedName("updated_at")
                val updatedAt: String? = null,
                @SerializedName("from_date")
                val fromDate: String? = null,
                @SerializedName("to_date")
                val toDate: String? = null,
                @SerializedName("commission")
                val commission: String? = null,
                // Fallbacks / legacy fields
                @SerializedName("order_number")
                val orderNumber: String? = null,
                @SerializedName("order_id")
                val orderId: Int? = null,
                @SerializedName("customer_name")
                val customerName: String? = null,
                @SerializedName("delivery_status")
                val deliveryStatus: String? = null,
                @SerializedName("order_date")
                val orderDate: String? = null,
                @SerializedName("date")
                val date: String? = null
            )
        }

        data class PayoutHistory(
            @SerializedName("current_page")
            val currentPage: Int? = null,
            @SerializedName("data")
            val `data`: List<PayoutHistoryData>? = null,
            @SerializedName("last_page")
            val lastPage: Int? = null,
            @SerializedName("per_page")
            val perPage: Int? = null,
            @SerializedName("total")
            val total: Int? = null
        ) {
            data class PayoutHistoryData(
                @SerializedName("id")
                val id: Int? = null,
                @SerializedName("request_id")
                val requestId: String? = null,
                @SerializedName("amount")
                val amount: String? = null,
                @SerializedName("transaction_id")
                val transactionId: String? = null,
                @SerializedName("payment_status")
                val paymentStatus: String? = null,
                @SerializedName("payout_date")
                val payoutDate: String? = null,
                @SerializedName("from_date")
                val fromDate: String? = null,
                @SerializedName("to_date")
                val toDate: String? = null,
                @SerializedName("commission")
                val commission: String? = null,
                @SerializedName("date")
                val date: String? = null
            )
        }
    }
}
