package com.desynatdealer.model.upload

import com.google.gson.annotations.SerializedName

data class UploadResponse(

    @SerializedName("status"  ) var status  : String? = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Data?   = Data()

)

data class Data (

    @SerializedName("url" ) var url : String? = null

)