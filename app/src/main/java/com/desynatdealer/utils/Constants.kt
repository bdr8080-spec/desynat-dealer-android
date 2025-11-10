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
}