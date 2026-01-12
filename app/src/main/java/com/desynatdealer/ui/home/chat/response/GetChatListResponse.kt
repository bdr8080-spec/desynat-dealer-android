package com.desynatdealer.ui.home.chat.response


import com.google.gson.annotations.SerializedName

data class GetChatListResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("action")
        val action: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("custom_details")
        val customDetails: String,
        @SerializedName("delivery_address")
        val deliveryAddress: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("last_chat")
        val lastChat: String,
        @SerializedName("last_chat_time")
        val lastChatTime: String,
        @SerializedName("price")
        val price: Double,
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("product_request")
        val productRequest: String,
        @SerializedName("product_variation")
        val productVariation: ProductVariation,
        @SerializedName("quantity")
        val quantity: String,
        @SerializedName("seller_details")
        val sellerDetails: SellerDetails,
        @SerializedName("seller_id")
        val sellerId: Int,
        @SerializedName("shipping")
        val shipping: Double,
        @SerializedName("tax")
        val tax: Double,
        @SerializedName("total_amount")
        val totalAmount: Double,
        @SerializedName("type")
        val type: String,
        @SerializedName("unique_id")
        val uniqueId: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_details")
        val userDetails: UserDetails,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("variation_id")
        val variationId: String
    ) {
        data class ProductVariation(
            @SerializedName("actual_price")
            val actualPrice: Double,
            @SerializedName("bust")
            val bust: Any,
            @SerializedName("color_id")
            val colorId: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("discount_type")
            val discountType: String,
            @SerializedName("discount_value")
            val discountValue: String,
            @SerializedName("final_price")
            val finalPrice: Double,
            @SerializedName("hip")
            val hip: Any,
            @SerializedName("id")
            val id: Int,
            @SerializedName("length")
            val length: Any,
            @SerializedName("parent_child")
            val parentChild: String,
            @SerializedName("product_details")
            val productDetails: ProductDetails,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("shoulder")
            val shoulder: Any,
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
            data class ProductDetails(
                @SerializedName("affiliated_commission")
                val affiliatedCommission: Any,
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
                val exchangableDays: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("max_card_quantity")
                val maxCardQuantity: String,
                @SerializedName("min_card_quantity")
                val minCardQuantity: String,
                @SerializedName("other_ar")
                val otherAr: String,
                @SerializedName("other_en")
                val otherEn: String,
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
        }

        data class SellerDetails(
            @SerializedName("accept_terms")
            val acceptTerms: String,
            @SerializedName("admin_verify")
            val adminVerify: String,
            @SerializedName("affilate_commission")
            val affilateCommission: Any,
            @SerializedName("bank_information")
            val bankInformation: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("device_token")
            val deviceToken: Any,
            @SerializedName("device_type")
            val deviceType: Any,
            @SerializedName("email")
            val email: String,
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
            @SerializedName("otp")
            val otp: String,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("profile_pic")
            val profilePic: String,
            @SerializedName("reject_reason")
            val rejectReason: Any,
            @SerializedName("store_information")
            val storeInformation: String,
            @SerializedName("updated_at")
            val updatedAt: String
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
}