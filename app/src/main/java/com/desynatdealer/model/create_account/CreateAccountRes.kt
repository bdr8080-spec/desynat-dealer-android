package com.desynatdealer.model.create_account

import com.google.gson.annotations.SerializedName

data class CreateAccountRes (

    @SerializedName("status"  ) var status  : String? = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : Data?   = Data()

)

data class Data (

    @SerializedName("profile_pic"  ) var profilePic  : String? = null,
    @SerializedName("first_name"   ) var firstName   : String? = null,
    @SerializedName("last_name"    ) var lastName    : String? = null,
    @SerializedName("country_code" ) var countryCode : String? = null,
    @SerializedName("mobile"       ) var mobile      : String? = null,
    @SerializedName("email"        ) var email       : String? = null,
    @SerializedName("password"     ) var password    : String? = null,
    @SerializedName("accept_terms" ) var acceptTerms : String? = null,
    @SerializedName("device_type"  ) var deviceType  : String? = null,
    @SerializedName("device_token" ) var deviceToken : String? = null,
    @SerializedName("otp"          ) var otp         : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("id"           ) var id          : Int?    = null

)