package com.desynatdealer.ui.home.staffManagement.response


import com.google.gson.annotations.SerializedName

data class GetStaffCategoryResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: Any,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name_ar")
        val nameAr: String,
        @SerializedName("name_en")
        val nameEn: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: Any
    )
}