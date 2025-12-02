/*
package com.desynatdealer.ui.home.productManagement.response


import com.google.gson.annotations.SerializedName

data class GetAllProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("active_product_count")
        var activeProductCount: Int?=0,
        @SerializedName("all_product_count")
        val allProductCount: Int?=0,
        @SerializedName("all_product_data")
        val allProductData: List<AllProductData>,
       @SerializedName("active_product")
        val activeProduct: List<AllProductData>,
       @SerializedName("inactive_product")
        val inactiveProduct: List<AllProductData>,
        @SerializedName("inactive_product_count")
        var inactiveProductCount: Int?=0
    ) {
        data class AllProductData(
            @SerializedName("affiliated_product")
            val affiliatedProduct: String,
            @SerializedName("brand")
            val brand: GetSellerBrandResponse.Data,
            @SerializedName("brand_id")
            val brandId: String,
            @SerializedName("cash_on_delivery")
            val cashOnDelivery: String,
            @SerializedName("category")
            var category: GetCategoryResponse.Data,
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
            var productActiveStatus: Int,
            @SerializedName("product_exchangable")
            val productExchangable: String,
            @SerializedName("product_name_ar")
            val productNameAr: String,
            @SerializedName("product_name_en")
            val productNameEn: String,
            @SerializedName("product_return")
            val productReturn: String,
            @SerializedName("product_specifications")
            val productSpecifications: ArrayList<ProductSpecification>,
            @SerializedName("product")
            val product: ArrayList<Product>,
            @SerializedName("ready_to_ship")
            val readyToShip: String,
            @SerializedName("return_days")
            val returnDays: String,
            @SerializedName("review_rating")
            val reviewRating: String,
            @SerializedName("seller_id")
            val sellerId: String,
            @SerializedName("stock")
            val stock: String,
            @SerializedName("store_id")
            val storeId: String,
            @SerializedName("subcategory")
            var subcategory: GetSubCategoryResponse.Data,
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

            data class ProductSpecification(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("feature_ar")
                var featureAr: String,
                @SerializedName("feature_en")
                var featureEn: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("name_ar")
                var nameAr: String,
                @SerializedName("name_en")
                var nameEn: String,
                @SerializedName("product_id")
                val productId: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

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
    }
}*/


package com.desynatdealer.ui.home.productManagement.response


import com.google.gson.annotations.SerializedName

data class GetAllProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("active_product_count")
        var activeProductCount: Int?=0,
        @SerializedName("all_product_count")
        val allProductCount: Int?=0,
        @SerializedName("all_product_data")
        val allProductData: List<AllProductData>,
        @SerializedName("active_product")
        val activeProduct: List<AllProductData>,
        @SerializedName("inactive_product")
        val inactiveProduct: List<AllProductData>,
        @SerializedName("inactive_product_count")
        var inactiveProductCount: Int?=0
    ) {
        data class AllProductData(
            @SerializedName("affiliated_product")
            val affiliatedProduct: String,
            @SerializedName("brand")
            val brand: GetSellerBrandResponse.Data,
            @SerializedName("brand_id")
            val brandId: String,
            @SerializedName("cash_on_delivery")
            val cashOnDelivery: String,
            @SerializedName("category")
            var category: GetCategoryResponse.Data,
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
            var productActiveStatus: Int,
            @SerializedName("product_exchangable")
            val productExchangable: String,
            @SerializedName("product_name_ar")
            val productNameAr: String,
            @SerializedName("product_name_en")
            val productNameEn: String,
            @SerializedName("product_return")
            val productReturn: String,
            @SerializedName("product_specifications")
            val productSpecifications: ArrayList<ProductSpecification>,
            @SerializedName("product_variants")
            val productVariants: ArrayList<ProductVariant>,
            @SerializedName("ready_to_ship")
            val readyToShip: String,
            @SerializedName("return_days")
            val returnDays: String,
            @SerializedName("review_rating")
            val reviewRating: String,
            @SerializedName("seller_id")
            val sellerId: String,
            @SerializedName("stock")
            val stock: String,
            @SerializedName("store_id")
            val storeId: String,
            @SerializedName("subcategory")
            var subcategory: GetSubCategoryResponse.Data,
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

            data class ProductSpecification(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("feature_ar")
                var featureAr: String,
                @SerializedName("feature_en")
                var featureEn: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("name_ar")
                var nameAr: String,
                @SerializedName("name_en")
                var nameEn: String,
                @SerializedName("product_id")
                val productId: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class ProductVariant(
                @SerializedName("actual_price")
                val actualPrice: String?=null,
                @SerializedName("bust")
                val bust: String?=null,
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
    }
}
