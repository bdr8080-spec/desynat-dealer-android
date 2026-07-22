package com.desynatdealer.ui.home.profile.response


import com.google.gson.annotations.SerializedName

data class GetStoryResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String? = "",
        @SerializedName("file")
        val `file`: String? = "",
        @SerializedName("thumbnail")
        val `thumbnail`: String? = "",
        @SerializedName("id")
        val id: String? = "",
        @SerializedName("is_seen")
        var isSeen: Int = 0,
        @SerializedName("seller_id")
        val sellerId: String? = "",
        @SerializedName("status")
        var status: String? = "",
        @SerializedName("title")
        val title: String? = "",
        @SerializedName("caption")
        val caption: String? = "",
        @SerializedName("type")
        val type: String? = "",
        @SerializedName("updated_at")

        val updatedAt: String? = "",
        @SerializedName("is_promotional")
        val isPromotional: String? = "",
        @SerializedName("start_date")
        val startDate: String? = "",
        @SerializedName("end_date")
        val endDate: String? = "",
        @SerializedName("total_days")
        val totalDays: String? = "",
        @SerializedName("amount")
        val amount: String? = "",
        @SerializedName("Admin_verify")
        val adminVerify: String? = "",
        @SerializedName("payment_status")
        val paymentStatus: String? = "",
        @SerializedName("transaction_id")
        val transactionId: String? = "",
        @SerializedName("reject_reason")
        val rejectReason: String? = ""
    )
}


data class GetStoryResponseNew(

    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<Data>
) {

    data class Data(
        @SerializedName("title")
        val title: String? = null,

        @SerializedName("highlights")
        val highlights: List<SellerHighlight>? = emptyList()
    ) {

        data class SellerHighlight(

            @SerializedName("id")
            val id: Int,

            @SerializedName("type")
            val type: Int,

            @SerializedName("seller_id")
            val sellerId: Int,

            @SerializedName("file")
            val file: String,

            @SerializedName("title")
            val title: String,

            @SerializedName("status")
            val status: Int,

            @SerializedName("thumbnail")
            val thumbnail: String?,

            @SerializedName("created_at")
            val createdAt: String,

            @SerializedName("updated_at")
            val updatedAt: String,

            @SerializedName("is_seen")
            var isSeen: Int = 0,

            @SerializedName("caption")
            val caption: String? = "",

            )
    }
}