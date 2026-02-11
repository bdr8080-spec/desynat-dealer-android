package com.desynatdealer.ui.home.dealManagement.response


import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.google.gson.annotations.SerializedName

data class GetAllDealResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("enrolled")
        val enrolled: List<ListedDeal>,
        @SerializedName("expired_deal")
        val expiredDeal: List<ListedDeal>,
        @SerializedName("listed_deal")
        val listedDeal: List<ListedDeal>
    ) {
        data class ListedDeal(
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
            @SerializedName("seller_deal_count")
            val sellerDealCount: String,
            @SerializedName("seller_dealrecord")
            val sellerDealrecord: List<SellerDealrecord>,
            @SerializedName("status")
            val status: Int,
            @SerializedName("sub_category_id")
            val subCategoryId: List<String>,
            @SerializedName("to")
            val to: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class SellerDealrecord(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("deal_id")
                val dealId: Int,
                @SerializedName("discount")
                val discount: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("seller_id")
                val sellerId: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )
        }
    }
}