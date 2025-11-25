package com.desynatdealer.ui.home.productManagement.response


import com.google.gson.annotations.SerializedName

data class AddProductRequest(
    @SerializedName("actual_price")
    val actualPrice: List<String>,
    @SerializedName("affiliated_product")
    val affiliatedProduct: String,
    @SerializedName("brand_id")
    val brandId: String,
    @SerializedName("bust")
    val bust: List<String>,
    @SerializedName("cash_on_delivery")
    val cashOnDelivery: String,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("color_id")
    val colorId: List<String>,
    @SerializedName("customized_product")
    val customizedProduct: String,
    @SerializedName("description_ar")
    val descriptionAr: String,
    @SerializedName("description_en")
    val descriptionEn: String,
    @SerializedName("discount_type")
    val discountType: List<String>,
    @SerializedName("discount_value")
    val discountValue: List<String>,
    @SerializedName("exchangable_days")
    val exchangableDays: String,
    @SerializedName("final_price")
    val finalPrice: List<String>,
    @SerializedName("hip")
    val hip: List<String>,
    @SerializedName("length")
    val length: List<String>,
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
    @SerializedName("shoulder")
    val shoulder: List<String>,
    @SerializedName("size_id")
    val sizeId: List<String>,
    @SerializedName("sku")
    val sku: List<String>,
    @SerializedName("sku_threshold_value")
    val skuThresholdValue: List<String>,
    @SerializedName("sleeve_length")
    val sleeveLength: List<String>,
    @SerializedName("subcategory_id")
    val subcategoryId: String,
    @SerializedName("total_stock")
    val totalStock: List<String>,
    @SerializedName("upload_size_chart")
    val uploadSizeChart: String,
    @SerializedName("variant_image")
    val variantImage: List<List<String>>,
    @SerializedName("waist")
    val waist: List<String>,
    @SerializedName("product_id")
    val productId: String?=null,

)