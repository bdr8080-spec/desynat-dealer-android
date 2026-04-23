package com.desynatdealer.ui.home.staffManagement.response


import com.google.gson.annotations.SerializedName

data class GetSellerAllMenuResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("active_patterns")
        val activePatterns: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("icon")
        val icon: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("order_by")
        val orderBy: Int,
        @SerializedName("parent_id")
        val parentId: Any,
        @SerializedName("route")
        val route: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}