package com.desynatdealer.ui.home.staffManagement.response


import com.google.gson.annotations.SerializedName

data class AddNewStaffRequest(
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("menu_id")
    val menuId: List<MenuId>,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name_ar")
    val nameAr: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("select_category")
    val selectCategory: String,
    @SerializedName("id")
    val id: String?=null
) {
    data class MenuId(
        @SerializedName("name")
        val title: String,
        @SerializedName("title_ar")
        val titleArabic: String,
        @SerializedName("can_action")
        var canAction: Int,
        @SerializedName("can_view")
        var canView: Int,
        @SerializedName("id")
        val id: String,
        @SerializedName("is_selected")
        var isSelected: Boolean = false
    )
}