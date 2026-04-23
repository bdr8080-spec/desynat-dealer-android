package com.desynatdealer.ui.home.refundManagement.response


import com.google.gson.annotations.SerializedName

data class GetRefundOrderResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("first_page_url")
        val firstPageUrl: String,
        @SerializedName("from")
        val from: Int,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("last_page_url")
        val lastPageUrl: String,
        @SerializedName("links")
        val links: List<Link>,
        @SerializedName("next_page_url")
        val nextPageUrl: String,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("admin_commission")
            val adminCommission: String,
            @SerializedName("affiliate_admin_commission")
            val affiliateAdminCommission: String,
            @SerializedName("affiliate_commission")
            val affiliateCommission: String,
            @SerializedName("affiliate_id")
            val affiliateId: Any,
            @SerializedName("amount")
            val amount: String,
            @SerializedName("assign_date")
            val assignDate: Any,
            @SerializedName("assign_status")
            val assignStatus: String,
            @SerializedName("cancel_reason")
            val cancelReason: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("delivery_boy_id")
            val deliveryBoyId: Any,
            @SerializedName("delivery_status")
            val deliveryStatus: String,
            @SerializedName("driver_details")
            val driverDetails: Any,
            @SerializedName("exchange_new_size")
            val exchangeNewSize: Any,
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
            @SerializedName("order")
            val order: Order,
            @SerializedName("order_id")
            val orderId: Int,
            @SerializedName("order_status")
            val orderStatus: Any,
            @SerializedName("order_type")
            val orderType: String,
            @SerializedName("p_orderdetails_id")
            val pOrderdetailsId: String,
            @SerializedName("payment_status")
            val paymentStatus: String,
            @SerializedName("product_details")
            val productDetails: ProductDetails,
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
            @SerializedName("reference_link")
            val referenceLink: String,
            @SerializedName("return_image")
            val returnImage: String,
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
            val uniqueId: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("user_rating_review_provide")
            val userRatingReviewProvide: Any,
            @SerializedName("variants_id")
            val variantsId: Int,
            @SerializedName("variation_details")
            val variationDetails: VariationDetails
        ) {
            data class Order(
                @SerializedName("assign_date")
                val assignDate: Any,
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
                val discountMrp: String,
                @SerializedName("gift_card_details")
                val giftCardDetails: Any,
                @SerializedName("giftcard_amount")
                val giftcardAmount: String,
                @SerializedName("giftcard_id")
                val giftcardId: Any,
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
                @SerializedName("promocode_details")
                val promocodeDetails: Any,
                @SerializedName("promocode_id")
                val promocodeId: Any,
                @SerializedName("promocode_status")
                val promocodeStatus: String,
                @SerializedName("promocode_value")
                val promocodeValue: Any,
                @SerializedName("shipping_fees")
                val shippingFees: String,
                @SerializedName("tax")
                val tax: String,
                @SerializedName("total_amount")
                val totalAmount: String,
                @SerializedName("total_mrp")
                val totalMrp: String,
                @SerializedName("transaction_id")
                val transactionId: String,
                @SerializedName("unique_id")
                val uniqueId: Any,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_id")
                val userId: Int,
                @SerializedName("wallet_amount")
                val walletAmount: Any
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
                    val landmark: Any,
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
                    val streetNo: Any,
                    @SerializedName("updated_at")
                    val updatedAt: String,
                    @SerializedName("user_id")
                    val userId: Int
                )
            }

            data class ProductDetails(
                @SerializedName("affiliated_commission")
                val affiliatedCommission: String,
                @SerializedName("affiliated_product")
                val affiliatedProduct: String,
                @SerializedName("brand")
                val brand: Brand,
                @SerializedName("brand_id")
                val brandId: String,
                @SerializedName("cash_on_delivery")
                val cashOnDelivery: String,
                @SerializedName("category")
                val category: Category,
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
                val exchangableDays: String,
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
                val returnDays: String,
                @SerializedName("review_rating")
                val reviewRating: String,
                @SerializedName("seller_details")
                val sellerDetails: SellerDetails,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("store")
                val store: Store,
                @SerializedName("store_id")
                val storeId: String,
                @SerializedName("subcategory")
                val subcategory: Subcategory,
                @SerializedName("subcategory_id")
                val subcategoryId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("upload_size_chart")
                val uploadSizeChart: String
            ) {
                data class Brand(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("image")
                    val image: String,
                    @SerializedName("name_arabic")
                    val nameArabic: String,
                    @SerializedName("name_english")
                    val nameEnglish: String,
                    @SerializedName("status")
                    val status: Int,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )

                data class Category(
                    @SerializedName("color_code")
                    val colorCode: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("description")
                    val description: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("image")
                    val image: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("status")
                    val status: Int,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )

                data class SellerDetails(
                    @SerializedName("accept_terms")
                    val acceptTerms: String,
                    @SerializedName("admin_verify")
                    val adminVerify: String,
                    @SerializedName("affilate_commission")
                    val affilateCommission: Any,
                    @SerializedName("app_notification")
                    val appNotification: Int,
                    @SerializedName("bank_information")
                    val bankInformation: String,
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
                    @SerializedName("fcm_token")
                    val fcmToken: String,
                    @SerializedName("first_name")
                    val firstName: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("is_block")
                    val isBlock: Int,
                    @SerializedName("lang")
                    val lang: Any,
                    @SerializedName("last_name")
                    val lastName: String,
                    @SerializedName("mobile")
                    val mobile: String,
                    @SerializedName("name_ar")
                    val nameAr: Any,
                    @SerializedName("notification")
                    val notification: Int,
                    @SerializedName("otp")
                    val otp: String,
                    @SerializedName("otp_verify")
                    val otpVerify: String,
                    @SerializedName("parent_id")
                    val parentId: Any,
                    @SerializedName("password")
                    val password: String,
                    @SerializedName("profile_pic")
                    val profilePic: String,
                    @SerializedName("profile_qr")
                    val profileQr: String,
                    @SerializedName("qrSellerUrl")
                    val qrSellerUrl: String,
                    @SerializedName("reject_reason")
                    val rejectReason: Any,
                    @SerializedName("select_category")
                    val selectCategory: Any,
                    @SerializedName("store_information")
                    val storeInformation: String,
                    @SerializedName("type")
                    val type: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )

                data class Store(
                    @SerializedName("address_line1")
                    val addressLine1: String,
                    @SerializedName("address_line2")
                    val addressLine2: String,
                    @SerializedName("bio")
                    val bio: String,
                    @SerializedName("country")
                    val country: String,
                    @SerializedName("country_code")
                    val countryCode: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("let")
                    val let: String,
                    @SerializedName("long")
                    val long: String,
                    @SerializedName("seller_id")
                    val sellerId: Int,
                    @SerializedName("store_address")
                    val storeAddress: String,
                    @SerializedName("Store_Availability")
                    val storeAvailability: Int,
                    @SerializedName("store_image")
                    val storeImage: String,
                    @SerializedName("store_mail_id")
                    val storeMailId: String,
                    @SerializedName("store_name")
                    val storeName: String,
                    @SerializedName("store_phone")
                    val storePhone: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )

                data class Subcategory(
                    @SerializedName("category_id")
                    val categoryId: Int,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("image")
                    val image: String,
                    @SerializedName("name_arabic")
                    val nameArabic: String,
                    @SerializedName("name_english")
                    val nameEnglish: String,
                    @SerializedName("status")
                    val status: Int,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }

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
                val hip: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("length")
                val length: Any,
                @SerializedName("parent_child")
                val parentChild: String,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("shoulder")
                val shoulder: Any,
                @SerializedName("size")
                val size: Size,
                @SerializedName("size_id")
                val sizeId: String,
                @SerializedName("sku")
                val sku: String,
                @SerializedName("sku_threshold_value")
                val skuThresholdValue: Any,
                @SerializedName("sleeve_length")
                val sleeveLength: Any,
                @SerializedName("status")
                val status: String,
                @SerializedName("total_stock")
                val totalStock: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("variant_image")
                val variantImage: List<String>,
                @SerializedName("waist")
                val waist: Any
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

        data class Link(
            @SerializedName("active")
            val active: Boolean,
            @SerializedName("label")
            val label: String,
            @SerializedName("url")
            val url: String
        )
    }
}