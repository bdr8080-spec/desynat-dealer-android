package com.desynatdealer.ui.home.response


import com.google.gson.annotations.SerializedName

data class GetAllPlanResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("commission")
        val commission: Int,
        @SerializedName("commission_percentage")
        val commissionPercentage: Double,
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("duration")
        val duration: Int,
        @SerializedName("duration_type")
        val durationType: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("no_of_employee")
        val noOfEmployee: Int,
        @SerializedName("no_of_highlights")
        val noOfHighlights: Int,
        @SerializedName("no_of_products")
        val noOfProducts: Int,
        @SerializedName("no_of_videos")
        val noOfVideos: Int,
        @SerializedName("plan_name_arabic")
        val planNameArabic: String,
        @SerializedName("plan_name_english")
        val planNameEnglish: String,
        @SerializedName("price")
        val price: Double,
        @SerializedName("status")
        val status: Int,
        @SerializedName("stories")
        val stories: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("isPurchased")
        var isPurchased: Boolean=false
    )
}