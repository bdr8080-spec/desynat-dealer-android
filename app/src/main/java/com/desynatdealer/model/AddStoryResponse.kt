package com.desynatdealer.model

data class AddStoryResponse(
    val `data`: Data,
    val message: String,
    val status: String
) {
    data class Data(
        val amount: Int,
        val created_at: String,
        val end_date: String,
        val `file`: String,
        val id: Int,
        val is_promotional: Int,
        val seller_id: Int,
        val start_date: String,
        val thumbnail: Any,
        val total_days: Int,
        val type: Int,
        val updated_at: String
    )
}