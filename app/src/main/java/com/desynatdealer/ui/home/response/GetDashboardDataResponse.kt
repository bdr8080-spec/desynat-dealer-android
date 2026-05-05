package com.desynatdealer.ui.home.response


import com.google.gson.annotations.SerializedName

data class GetDashboardDataResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("order_analytics")
        val orderAnalytics: List<OrderAnalytic>,
        @SerializedName("total_active_products")
        val totalActiveProducts: Int,
        @SerializedName("total_active_staff")
        val totalActiveStaff: Int,
        @SerializedName("total_cancelled_orders")
        val totalCancelledOrders: Int,
        @SerializedName("total_completed_orders")
        val totalCompletedOrders: Int,
        @SerializedName("total_inactive_products")
        val totalInactiveProducts: Int,
        @SerializedName("total_inactive_staff")
        val totalInactiveStaff: Int,
        @SerializedName("total_new_orders")
        val totalNewOrders: Int,
        @SerializedName("total_ongoing_orders")
        val totalOngoingOrders: Int,
        @SerializedName("total_orders")
        val totalOrders: Int,
        @SerializedName("total_products")
        val totalProducts: Int,
        @SerializedName("total_revenue")
        val totalRevenue: String,
        @SerializedName("total_staff")
        val totalStaff: Int,
        @SerializedName("visitor_analytics")
        val visitorAnalytics: List<VisitorAnalytic>
    ) {
        data class OrderAnalytic(
            @SerializedName("month")
            val month: String,
            @SerializedName("orders")
            val orders: Int
        )

        data class VisitorAnalytic(
            @SerializedName("month")
            val month: String,
            @SerializedName("total_visitors")
            val totalVisitors: Int
        )
    }
}