package com.desynatdealer.ui.home.response


import com.google.gson.annotations.SerializedName

data class GetSellerInfo(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("accept_terms")
        val acceptTerms: String,
        @SerializedName("admin_verify")
        val adminVerify: String,
        @SerializedName("bank_information")
        val bankInformation: String,
        @SerializedName("country_code")
        var countryCode: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("device_token")
        val deviceToken: String,
        @SerializedName("device_type")
        val deviceType: String,
        @SerializedName("email")
        var email: String,
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
        var mobile: String,
        @SerializedName("otp")
        val otp: String,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("owner_document")
        val ownerDocument: List<OwnerDocument>,
        @SerializedName("password")
        val password: String,
        @SerializedName("profile_pic")
        val profilePic: String,
        @SerializedName("reject_reason")
        val rejectReason: String,
        @SerializedName("affilate_commission")
        var affilateCommission: String,
        @SerializedName("seller_bank_details")
        val sellerBankDetails: SellerBankDetails,
        @SerializedName("store_document")
        val storeDocument: List<StoreDocument>,
        @SerializedName("store_info")
        val storeInfo: StoreInfo,
        @SerializedName("store_information")
        val storeInformation: String,
        @SerializedName("subcription_plan")
        val subcriptionPlan: List<Any>,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("app_notification")
        var appNotification: Int,
        @SerializedName("notification")
        var notification: Int
    ) {
        data class OwnerDocument(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("document_path")
            val documentPath: String,
            @SerializedName("document_type")
            val documentType: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class SellerBankDetails(
            @SerializedName("account_holder_name")
            val accountHolderName: String,
            @SerializedName("account_type")
            val accountType: String,
            @SerializedName("bank_name")
            val bankName: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("iban")
            val iban: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("swift")
            val swift: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upload_proof")
            val uploadProof: String,
            @SerializedName("user_id")
            val userId: String
        )

        data class StoreDocument(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("document_path")
            val documentPath: String,
            @SerializedName("document_type")
            val documentType: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class StoreInfo(
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
            @SerializedName("store_image")
            val storeImage: String,
            @SerializedName("store_mail_id")
            val storeMailId: String,
            @SerializedName("Store_Availability")
            var storeAvailability: Int,
            @SerializedName("store_name")
            val storeName: String,
            @SerializedName("store_phone")
            val storePhone: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}