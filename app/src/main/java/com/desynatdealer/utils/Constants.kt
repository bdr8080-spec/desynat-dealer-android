package com.desynatdealer.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72/"

    var language=""
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=1
    var isLogout = false

    const val LOGOUT = "/api/seller/logout"
    const val GET_COUNTRY = "api/country"
    const val DASHBOARD = "/api/seller/seller-dashboard"
    const val GET_PROFILE = "/api/seller/seller-profile"
    const val GET_SUBSCRIPTION_PLAN = "api/seller/all-plans"
    const val CREATE_SUBSCRIPTION_PLAN = "/api/seller/create-subcription-plan-buy"
    const val BUY_SUBSCRIPTION_PLAN = "/api/seller/payment-subcription-plan"

    const val GET_CATEGORY = "/api/seller/category"
    const val GET_SUB_CATEGORY = "/api/seller/sub-category"
    const val GET_BRAND = "/api/seller/all-brand"
    const val GET_COLOR = "/api/seller/all-color"
    const val GET_SIZE = "/api/seller/all-size"

    const val STORE_ON_OFF = "/api/seller/store_on_off"
   // const val ADD_PRODUCT = "/api/seller/add-product"
    const val ADD_PRODUCT = "/api/seller/add-product1"
 //   const val UPDATE_PRODUCT = "/api/seller/edit-product"000000
    const val UPDATE_PRODUCT = "/api/seller/edit-product1"

    const val GET_ALL_PRODUCT = "/api/seller/all-product"
    const val PRODUCT_DETAIL = "/api/seller/single-product-details"
    const val PRODUCT_ACTIVE_INACTIVE = "/api/seller/product-status-active-inactive"
    const val DELETE_PRODUCT = "/api/seller/product-delete"
}