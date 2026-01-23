package com.desynatdealer.ui.home.dealManagement.response


import com.google.gson.annotations.SerializedName

data class GetDealDetailResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("added_products")
        val addedProducts: List<AddedProduct>,
        @SerializedName("deal_data")
        val dealData: DealData,
        @SerializedName("remaining_Product")
        val remainingProduct: List<RemainingProduct>
    ) {
        data class DealData(
            @SerializedName("category_details")
            val categoryDetails: CategoryDetails,
            @SerializedName("category_id")
            val categoryId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("discount")
            val discount: String,
            @SerializedName("from")
            val from: String,
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
            @SerializedName("sub_category_details")
            val subCategoryDetails: SubCategoryDetails,
            @SerializedName("sub_category_id")
            val subCategoryId: Int,
            @SerializedName("to")
            val to: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class CategoryDetails(
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

            data class SubCategoryDetails(
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

        data class RemainingProduct(
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
            val id: String,
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
            @SerializedName("subcategory")
            val subcategory: Subcategory,
            @SerializedName("subcategory_id")
            val subcategoryId: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upload_size_chart")
            val uploadSizeChart: String,
            @SerializedName("isAdded")
            var isAdded: Boolean = false
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

        data class AddedProduct(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("deal_id")
            val dealId: Int,
            @SerializedName("discount")
            val discount: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("product_details")
            val productDetails: RemainingProduct,
            @SerializedName("product_id")
            val productId: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}