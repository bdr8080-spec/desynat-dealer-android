package com.desynatdealer.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72"

    var language=""
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=1
    var isLogout = false

    const val LOGOUT = "/api/seller/logout"
    const val GET_COUNTRY = "/api/country"
    const val DASHBOARD = "/api/seller/seller-dashboard"
    const val GET_PROFILE = "/api/seller/seller-profile"
    const val GET_SUBSCRIPTION_PLAN = "/api/seller/all-plans"
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

    const val ALL_INVENTORY_PRODUCT = "/api/seller/all-Inventory-product"
    const val SINGLE_INVENTORY_DETAIL = "/api/seller/single-product-details"
    const val UPDATE_INVENTORY_STOCK = "/api/seller/update-stock-inventory"
    const val UPDATE_AFFILIATE_COMMISSION = "/api/seller/update-affilate-commission"
    const val UPDATE_STORE_INFO = "/api/seller/update-store-information"
    const val UPDATE_STORE_BANK_DETAIL = "/api/seller/update-store-bank-details"

    const val GET_BANNER_PRICE = "/api/seller/get-seller-banner-price"
    const val ADD_BANNER = "/api/seller/add-banner"
    const val GET_ALL_BANNER = "/api/seller/all-banners"
    const val BANNER_PAYMENT = "/api/seller/complete-banner-payment"

    const val GET_ALL_ORDER = "/api/seller/all-orders"
    const val GET_SINGLE_ORDER_DETAIL = "/api/seller/single-orders-data"
    const val UPDATE_SINGLE_ORDER_STATUS = "/api/seller/update-single-order-status"
    const val SEND_MESSAGE = "/api/send-message-in-chats"
    const val ALL_CHAT = "/api/seller/all-chats-records"
    const val SINGLE_CHAT = "/api/seller/single-chats-records"
    const val MODIFY_SINGLE_CHAT = "/api/seller/Modify-single-chats-records"
    const val UPDATE_RETURN_EXCHANGE_ORDER_STATUS = "/api/seller/update-order-exchange-return-status"

    const val GET_All_DEAL = "/api/seller/view-deals"
    const val GET_DEAL_DETAIL = "/api/seller/view-single-deals"
    const val ADD_PRODUCT_IN_DEAL = "/api/seller/Add-products-in-deal-inside"
    const val REMOVE_PRODUCT_IN_DEAL = "/api/seller/remove-products-in-deal"
    const val SHOW_ALL_PRODUCT_DEAL = "/api/seller/show-allproduct-deal-catgeory-subCatgory"

}