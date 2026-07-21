package com.desynatdealer.ui.home.response

data class PaymentCompleteResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val Admin_verify: String,
        val amount: String,
        val created_at: String,
        val end_date: String,
        val `file`: String,
        val id: Int,
        val is_promotional: String,
        val payment_status: String,
        val reject_reason: Any,
        val seller_id: Int,
        val start_date: String,
        val status: Int,
        val thumbnail: Any,
        val total_days: String,
        val transaction_id: String,
        val type: Int,
        val updated_at: String
    )
}