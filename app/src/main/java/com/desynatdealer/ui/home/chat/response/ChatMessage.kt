package com.desynatdealer.ui.home.chat.response

data class ChatMessage(
    var by: String? = null,
    var id: String? = null,
    var message: String? = null,
    var times: String? = null,
    var type: String? = null,
    var unique_id: String? = null,
    var user_id: String? = null,
    var read: Int? = 0
)