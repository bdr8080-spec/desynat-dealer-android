package com.desynatdealer.ui.home.orderManagement.response

import com.google.gson.annotations.SerializedName

data class GetAllOrderResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("All_order")
        val allOrder: List<AllOrder>,
        @SerializedName("ongoing_order")
        val ongoingOrder: List<AllOrder>,
        @SerializedName("past_order")
        val pastOrder: List<AllOrder>
    ) {
        data class AllOrder(
            @SerializedName("admin_commission")
            val adminCommission: Double,
            @SerializedName("order_status")
            val orderStatus: String,
            @SerializedName("amount")
            val amount: Double,
            @SerializedName("assign_date")
            val assignDate: String,
            @SerializedName("assign_status")
            val assignStatus: String,
            @SerializedName("cancel_reason")
            val cancelReason: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("delivery_boy_id")
            val deliveryBoyId: String,
            @SerializedName("delivery_status")
            val deliveryStatus: String,
            @SerializedName("exchange_new_size")
            val exchangeNewSize: String,
            @SerializedName("exchange_old_size")
            val exchangeOldSize: String,
            @SerializedName("exchange_reason")
            val exchangeReason: String,
            @SerializedName("giftcard_amount")
            val giftcardAmount: String,
            @SerializedName("giftcard_id")
            val giftcardId: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_picked")
            val isPicked: String,
            @SerializedName("not_delivered_reason")
            val notDeliveredReason: String,
            @SerializedName("order")
            val order: Order,
            @SerializedName("order_id")
            val orderId: Int,
            @SerializedName("payment_status")
            val paymentStatus: String,
            @SerializedName("product_details")
            val productDetails: ProductDetails,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("promocode_id")
            val promocodeId: String,
            @SerializedName("promocode_value")
            val promocodeValue: String,
            @SerializedName("quantity")
            val quantity: String,
            @SerializedName("r_e_type")
            val r_e_type: String,
            @SerializedName("p_orderdetails_id")
            val p_orderdetails_id: String,
            @SerializedName("r_e_approve")
            val rEApprove: String,
            @SerializedName("exchange_order_details")
            val exchangeOrderDetails: ExchangeOrderDetails,
            @SerializedName("return_reason")
            val returnReason: String,
            @SerializedName("return_type")
            val returnType: String,
            @SerializedName("seller_id")
            val sellerId: String,
            @SerializedName("shipping_fees")
            val shippingFees: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("tax")
            val tax: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_details")
            val userDetails: UserDetails,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("variants_id")
            val variantsId: Int,
            @SerializedName("variation_details")
            val variationDetails: VariationDetails
        ) {
            data class ExchangeOrderDetails(
                @SerializedName("admin_commission")
                val adminCommission: String,
                @SerializedName("amount")
                val amount: String,
                @SerializedName("assign_date")
                val assignDate: String,
                @SerializedName("assign_status")
                val assignStatus: String,
                @SerializedName("cancel_reason")
                val cancelReason: Any,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: String,
                @SerializedName("delivery_status")
                val deliveryStatus: String,
                @SerializedName("exchange_new_size")
                val exchangeNewSize: String,
                @SerializedName("exchange_old_size")
                val exchangeOldSize: Any,
                @SerializedName("exchange_reason")
                val exchangeReason: Any,
                @SerializedName("giftcard_amount")
                val giftcardAmount: Any,
                @SerializedName("giftcard_id")
                val giftcardId: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_picked")
                val isPicked: String,
                @SerializedName("not_delivered_reason")
                val notDeliveredReason: Any,
                @SerializedName("order_id")
                val orderId: Int,
                @SerializedName("order_status")
                val orderStatus: Any,
                @SerializedName("p_orderdetails_id")
                val pOrderdetailsId: String,
                @SerializedName("payment_status")
                val paymentStatus: String,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("promocode_id")
                val promocodeId: Any,
                @SerializedName("promocode_value")
                val promocodeValue: Any,
                @SerializedName("quantity")
                val quantity: Int,
                @SerializedName("r_e_approve")
                val rEApprove: String,
                @SerializedName("r_e_type")
                val rEType: String,
                @SerializedName("return_image")
                val returnImage: List<Any>,
                @SerializedName("return_reason")
                val returnReason: String,
                @SerializedName("return_type")
                val returnType: String,
                @SerializedName("returntostore")
                val returntostore: String,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("shipping_fees")
                val shippingFees: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("tax")
                val tax: String,
                @SerializedName("unique_id")
                val uniqueId: Any,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_id")
                val userId: Int,
                @SerializedName("variants_id")
                val variantsId: Int
            )
            data class Order(
                @SerializedName("assign_date")
                val assignDate: String,
                @SerializedName("assign_status")
                val assignStatus: String,
                @SerializedName("cancel_reason")
                val cancelReason: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_address")
                val deliveryAddress: Int,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: String,
                @SerializedName("delivery_details")
                val deliveryDetails: DeliveryDetails,
                @SerializedName("delivery_status")
                val deliveryStatus: String,
                @SerializedName("discount_mrp")
                val discountMrp: Double,
                @SerializedName("giftcard_amount")
                val giftcardAmount: String,
                @SerializedName("giftcard_id")
                val giftcardId: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("order_id")
                val orderId: String,
                @SerializedName("order_type")
                val orderType: String,
                @SerializedName("payment_method")
                val paymentMethod: String,
                @SerializedName("payment_status")
                val paymentStatus: String,
                @SerializedName("promocode_id")
                val promocodeId: String,
                @SerializedName("promocode_status")
                val promocodeStatus: String,
                @SerializedName("promocode_value")
                val promocodeValue: Double,
                @SerializedName("shipping_fees")
                val shippingFees: Double,
                @SerializedName("tax")
                val tax: Double,
                @SerializedName("total_amount")
                val totalAmount: Double,
                @SerializedName("total_mrp")
                val totalMrp: Double,
                @SerializedName("transaction_id")
                val transactionId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_details")
                val userDetails: UserDetails,
                @SerializedName("user_id")
                val userId: Int,
                @SerializedName("wallet_amount")
                val walletAmount: Double
            ) {
                data class DeliveryDetails(
                    @SerializedName("address")
                    val address: String,
                    @SerializedName("city")
                    val city: String,
                    @SerializedName("country_code")
                    val countryCode: String,
                    @SerializedName("country_name")
                    val countryName: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("house_no")
                    val houseNo: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("landmark")
                    val landmark: String,
                    @SerializedName("latitude")
                    val latitude: String,
                    @SerializedName("longitude")
                    val longitude: String,
                    @SerializedName("mobile")
                    val mobile: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("postal_code")
                    val postalCode: String,
                    @SerializedName("save_address_as")
                    val saveAddressAs: Int,
                    @SerializedName("state")
                    val state: String,
                    @SerializedName("status")
                    val status: Int,
                    @SerializedName("street_no")
                    val streetNo: String,
                    @SerializedName("updated_at")
                    val updatedAt: String,
                    @SerializedName("user_id")
                    val userId: Int
                )

                data class UserDetails(
                    @SerializedName("country_code")
                    val countryCode: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("device_token")
                    val deviceToken: Any,
                    @SerializedName("device_type")
                    val deviceType: String,
                    @SerializedName("email")
                    val email: String,
                    @SerializedName("email_verified_at")
                    val emailVerifiedAt: String,
                    @SerializedName("full_name")
                    val fullName: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("let")
                    val let: Double,
                    @SerializedName("long")
                    val long: Double,
                    @SerializedName("mobile")
                    val mobile: String,
                    @SerializedName("otp")
                    val otp: String,
                    @SerializedName("otp_verify")
                    val otpVerify: String,
                    @SerializedName("profile_image")
                    val profileImage: String,
                    @SerializedName("status")
                    val status: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }

            data class ProductDetails(
                @SerializedName("affiliated_commission")
                val affiliatedCommission: String,
                @SerializedName("affiliated_product")
                val affiliatedProduct: String,
                @SerializedName("brand_id")
                val brandId: String,
                @SerializedName("cash_on_delivery")
                val cashOnDelivery: String,
                @SerializedName("category_id")
                val categoryId: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("customized_product")
                val customizedProduct: String,
                @SerializedName("description_ar")
                val descriptionAr: String,
                @SerializedName("description_en")
                val descriptionEn: String,
                @SerializedName("exchangable_days")
                val exchangableDays: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("max_card_quantity")
                val maxCardQuantity: String,
                @SerializedName("min_card_quantity")
                val minCardQuantity: String,
                @SerializedName("other_ar")
                val otherAr: Any,
                @SerializedName("other_en")
                val otherEn: Any,
                @SerializedName("product_active_status")
                val productActiveStatus: Int,
                @SerializedName("product_exchangable")
                val productExchangable: String,
                @SerializedName("product_name_ar")
                val productNameAr: String,
                @SerializedName("product_name_en")
                val productNameEn: String,
                @SerializedName("product_return")
                val productReturn: String,
                @SerializedName("ready_to_ship")
                val readyToShip: String,
                @SerializedName("return_days")
                val returnDays: Any,
                @SerializedName("review_rating")
                val reviewRating: String,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("store_id")
                val storeId: String,
                @SerializedName("subcategory_id")
                val subcategoryId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("upload_size_chart")
                val uploadSizeChart: String
            )

            data class UserDetails(
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("device_token")
                val deviceToken: Any,
                @SerializedName("device_type")
                val deviceType: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("email_verified_at")
                val emailVerifiedAt: Any,
                @SerializedName("full_name")
                val fullName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("let")
                val let: Any,
                @SerializedName("long")
                val long: Any,
                @SerializedName("mobile")
                val mobile: String,
                @SerializedName("otp")
                val otp: Any,
                @SerializedName("otp_verify")
                val otpVerify: String,
                @SerializedName("profile_image")
                val profileImage: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class VariationDetails(
                @SerializedName("actual_price")
                val actualPrice: String,
                @SerializedName("bust")
                val bust: Any,
                @SerializedName("color")
                val color: Color,
                @SerializedName("color_id")
                val colorId: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("discount_type")
                val discountType: String,
                @SerializedName("discount_value")
                val discountValue: String,
                @SerializedName("final_price")
                val finalPrice: String,
                @SerializedName("hip")
                val hip: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("length")
                val length: String,
                @SerializedName("parent_child")
                val parentChild: String,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("shoulder")
                val shoulder: String,
                @SerializedName("size")
                val size: Size,
                @SerializedName("size_id")
                val sizeId: String,
                @SerializedName("sku")
                val sku: String,
                @SerializedName("sku_threshold_value")
                val skuThresholdValue: String,
                @SerializedName("sleeve_length")
                val sleeveLength: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("total_stock")
                val totalStock: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("variant_image")
                val variantImage: List<String>,
                @SerializedName("waist")
                val waist: String
            ) {
                data class Color(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("hex_code")
                    val hexCode: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )

                data class Size(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }
        }

    }
}