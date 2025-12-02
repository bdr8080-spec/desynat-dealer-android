package com.desynatdealer.ui.home.productManagement.response


import com.google.gson.annotations.SerializedName

data class GetProductDetailResponse(
    @SerializedName("data")
    val `data`: GetAllProductResponse.Data.AllProductData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
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
        @SerializedName("product_specifications")
        val productSpecifications: List<ProductSpecification>,
        @SerializedName("product_variants")
        val productVariants: List<ProductVariant>,
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
    ) {
        data class ProductSpecification(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("feature_ar")
            val featureAr: String,
            @SerializedName("feature_en")
            val featureEn: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("name_ar")
            val nameAr: String,
            @SerializedName("name_en")
            val nameEn: String,
            @SerializedName("product_id")
            val productId: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class ProductVariant(
            @SerializedName("actual_price")
            val actualPrice: String,
            @SerializedName("bust")
            val bust: String?=null,
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
            val hip: String?=null,
            @SerializedName("id")
            val id: Int,
            @SerializedName("length")
            val length: String?=null,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("shoulder")
            val shoulder: String?=null,
            @SerializedName("size_id")
            val sizeId: String,
            @SerializedName("sku")
            val sku: String,
            @SerializedName("sku_threshold_value")
            val skuThresholdValue: String,
            @SerializedName("sleeve_length")
            val sleeveLength: String?=null,
            @SerializedName("status")
            val status: String,
            @SerializedName("total_stock")
            val totalStock: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("variant_image")
            val variantImage: List<String>,
            @SerializedName("waist")
            val waist: String?=null
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
        }
    }
}