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
    val selectCategory: String
) {
    data class MenuId(
        @SerializedName("can_action")
        val canAction: Int,
        @SerializedName("can_view")
        val canView: Int,
        @SerializedName("id")
        val id: Int
    )
}