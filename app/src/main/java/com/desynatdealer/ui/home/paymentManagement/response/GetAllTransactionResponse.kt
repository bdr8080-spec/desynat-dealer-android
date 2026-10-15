package com.desynatdealer.ui.home.paymentManagement.response

import com.google.gson.annotations.SerializedName

data class GetAllTransactionResponse(
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
        @SerializedName("transactions")
        val transactions: Transactions? = null
    ) {
        data class Transactions(
            @SerializedName("current_page")
            val currentPage: Int? = null,
            @SerializedName("data")
            val `data`: List<TransactionData>? = null,
            @SerializedName("last_page")
            val lastPage: Int? = null,
            @SerializedName("per_page")
            val perPage: Int? = null,
            @SerializedName("total")
            val total: Int? = null
        ) {
            data class TransactionData(
                @SerializedName("id")
                val id: Int? = null,
                @SerializedName("transaction_type")
                val transactionType: Int? = null,
                @SerializedName("transaction_label")
                val transactionLabel: String? = null,
                @SerializedName("reference_id")
                val referenceId: String? = null,
                @SerializedName("order_id")
                val orderId: Int? = null,
                @SerializedName("customer_name")
                val customerName: String? = null,
                @SerializedName("transaction_id")
                val transactionId: String? = null,
                @SerializedName("amount")
                val amount: String? = null,
                @SerializedName("commission")
                val commission: String? = null,
                @SerializedName("payment_method")
                val paymentMethod: String? = null,
                @SerializedName("transaction_date")
                val transactionDate: String? = null
            )
        }
    }
}