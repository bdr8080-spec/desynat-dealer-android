package com.desynatdealer.ui.home.productManagement.response

import com.google.gson.annotations.SerializedName

data class AddProductRequest(
    @SerializedName("affiliated_product")
    val affiliatedProduct: String,
    @SerializedName("brand_id")
    val brandId: String,
    @SerializedName("cash_on_delivery")
    val cashOnDelivery: String,
    val delivery_time_id: String,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("customized_product")
    val customizedProduct: String,
    @SerializedName("description_ar")
    val descriptionAr: String,
    @SerializedName("description_en")
    val descriptionEn: String,
    @SerializedName("exchangable_days")
    val exchangableDays: String,
    @SerializedName("max_card_quantity")
    val maxCardQuantity: String,
    @SerializedName("min_card_quantity")
    val minCardQuantity: String,
    @SerializedName("other_ar")
    val otherAr: String?=null,
    @SerializedName("other_en")
    val otherEn: String?=null,
    @SerializedName("product_exchangable")
    val productExchangable: String,
    @SerializedName("product_name_ar")
    val productNameAr: String,
    @SerializedName("product_name_en")
    val productNameEn: String,
    @SerializedName("product_return")
    val productReturn: String,
    @SerializedName("product_spe_ar")
    val productSpeAr: List<String>,
    @SerializedName("product_spe_en")
    val productSpeEn: List<String>,
    @SerializedName("product_spe_feature_ar")
    val productSpeFeatureAr: List<String>,
    @SerializedName("product_spe_feature_en")
    val productSpeFeatureEn: List<String>,
    @SerializedName("ready_to_ship")
    val readyToShip: String,
    @SerializedName("return_days")
    val returnDays: String,
    @SerializedName("review_rating")
    val reviewRating: String,
    @SerializedName("subcategory_id")
    val subcategoryId: String,
    @SerializedName("upload_size_chart")
    val uploadSizeChart: String,
    @SerializedName("Products")
    val products: List<Product>,
    @SerializedName("product_id")
    val productId: String?=null,

    ){
    data class Product(
        @SerializedName("color_id")
        var colorId: String,
        @SerializedName("product_variation")
        val productVariation: ArrayList<ProductVariation>,
        @SerializedName("variant_image")
        val variantImage: ArrayList<String>,
        @SerializedName("product_id")
        val productId: String?=null,
    ) {
        data class ProductVariation(
            @SerializedName("actual_price")
            var actualPrice: String?=null,
            @SerializedName("bust")
            var bust: String?=null,
            @SerializedName("created_at")
            var createdAt: String?=null,
            @SerializedName("discount_type")
            var discountType: String,
            @SerializedName("discount_value")
            var discountValue: String,
            @SerializedName("final_price")
            var finalPrice: String,
            @SerializedName("hip")
            var hip: String?=null,
            @SerializedName("id")
            var id: String?=null,
            @SerializedName("length")
            var length: String?=null,
            @SerializedName("product_id")
            var productId: String?=null,
            @SerializedName("shoulder")
            var shoulder: String?=null,
            @SerializedName("size_id")
            var sizeId: String,
            @SerializedName("sku")
            var sku: String,
            @SerializedName("sku_threshold_value")
            var skuThresholdValue: String,
            @SerializedName("sleeve_length")
            var sleeveLength: String?=null,
            @SerializedName("status")
            var status: String?=null,
            @SerializedName("total_stock")
            var totalStock: String,
            @SerializedName("updated_at")
            var updatedAt: String?=null,
            @SerializedName("waist")
            var waist: String?=null
        )
    }
}