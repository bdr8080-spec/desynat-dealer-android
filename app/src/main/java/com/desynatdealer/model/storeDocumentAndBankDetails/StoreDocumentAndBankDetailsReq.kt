package com.desynatdealer.model.storeDocumentAndBankDetails

import com.google.gson.annotations.SerializedName

data class StoreDocumentAndBankDetailsReq(


    @SerializedName("user_id"             ) var userId            : String?           = null,
    @SerializedName("owner_document"      ) var ownerDocument     : ArrayList<String> = arrayListOf(),
    @SerializedName("store_document"      ) var storeDocument     : ArrayList<String> = arrayListOf(),
    @SerializedName("account_type"        ) var accountType       : String?           = null,
    @SerializedName("iban"                ) var iban              : String?           = null,
    @SerializedName("swift"               ) var swift             : String?           = null,
    @SerializedName("bank_name"           ) var bankName          : String?           = null,
    @SerializedName("account_holder_name" ) var accountHolderName : String?           = null,
    @SerializedName("upload_proof"        ) var uploadProof       : String?           = null

)
