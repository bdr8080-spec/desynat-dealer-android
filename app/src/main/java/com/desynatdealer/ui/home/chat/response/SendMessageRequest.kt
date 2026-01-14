package com.desynatdealer.ui.home.chat.response

data class SendMessageRequest(
    val id: String,
    val by: String,
    val message: String,
    val type: Int,
    val unique_id: String,
    val times: String,
    val user_id: Long,
    val read: Int
)