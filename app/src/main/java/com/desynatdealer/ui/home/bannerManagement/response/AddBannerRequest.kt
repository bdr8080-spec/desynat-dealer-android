package com.desynatdealer.ui.home.bannerManagement.response


import com.google.gson.annotations.SerializedName

data class AddBannerRequest(
    @SerializedName("banner_position") //1 homeTop 2 home Banner 3 category
    val bannerPosition: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("total_days")
    val totalDays: String,
    @SerializedName("upload_banner")
    val uploadBanner: String,
    @SerializedName("category")// only required when banner_position 3
    val category: String?=null,
    @SerializedName("subcatgory")//// only required when banner_position 3
    val subcatgory: String?=null
)