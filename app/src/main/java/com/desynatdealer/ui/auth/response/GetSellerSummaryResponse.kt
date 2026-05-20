package com.desynatdealer.ui.auth.response


import com.google.gson.annotations.SerializedName

data class GetSellerSummaryResponse(
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
        @SerializedName("deleted_at")
        val deletedAt: Any,
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
        val lang: String,
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
        @SerializedName("owner_document")
        val ownerDocument: List<OwnerDocument>,
        @SerializedName("parent_id")
        val parentId: Any,
        @SerializedName("password")
        val password: String,
        @SerializedName("profile_pic")
        val profilePic: String,
        @SerializedName("profile_qr")
        val profileQr: Any,
        @SerializedName("qrSellerUrl")
        val qrSellerUrl: Any,
        @SerializedName("reject_reason")
        val rejectReason: String,
        @SerializedName("save_password")
        val savePassword: Any,
        @SerializedName("select_category")
        val selectCategory: Any,
        @SerializedName("seller_bank_details")
        val sellerBankDetails: SellerBankDetails,
        @SerializedName("store_document")
        val storeDocument: List<StoreDocument>,
        @SerializedName("store_info")
        val storeInfo: StoreInfo,
        @SerializedName("store_information")
        val storeInformation: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String
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
            @SerializedName("account_no")
            val accountNo: Any,
            @SerializedName("account_type")
            val accountType: String,
            @SerializedName("bank_name")
            val bankName: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("default_account")
            val defaultAccount: String,
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
    }
}