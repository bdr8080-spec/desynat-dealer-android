package com.desynatdealer.controller

import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.model.create_account.CreateAccountRes
import com.desynatdealer.model.login.req.LoginReq
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.model.upload.UploadResponse
import com.desynatdealer.ui.home.response.CommonResponse
import com.desynatdealer.ui.home.response.CreateSubscriptionPlanResponse
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CommonApiService {

    @POST("api/seller/login")
    suspend fun login(@Body map: HashMap<String,Any>): Response<LoginResponse>


    @Multipart
    @POST("api/upload")
    suspend fun upload(
        @Part file: MultipartBody.Part?
    ): Response<UploadResponse>


    @POST("api/seller/create_account")
    suspend fun createAccount(@Body req: HashMap<String, String>): Response<CreateAccountRes>


    @POST("api/seller/verify_otp")
    suspend fun verifyOtp(@Body req: HashMap<String, String>): Response<Unit>


    @POST("api/seller/store_information")
    suspend fun storeInformation(@Body req: HashMap<String, String>): Response<Unit>


    @POST("api/seller/store_document_and_bank_details")
    suspend fun storeDocumentAndBankDetails(@Body loginReq: StoreDocumentAndBankDetailsReq): Response<Unit>

    @POST("api/seller/seller_store_submit_request")
    suspend fun sellerStoreSubmitRequest(@Body map: HashMap<String, String>): Response<Unit>


    @POST("api/seller/forgot_password")
    suspend fun forgotPassword(@Body map: HashMap<String, String>): Response<CreateAccountRes>


    @POST("api/seller/create_password")
    suspend fun changePassword(@Body map: HashMap<String, String>): Response<Unit>


    @POST("api/seller/resend_otp")
    suspend fun resendOtp(@Body map: HashMap<String, String>): Response<Unit>

    @GET(Constants.GET_COUNTRY)
    suspend fun getCountry(): Response<GetCountryResponse>

    @GET(Constants.LOGOUT)
    suspend fun logout(): Response<CommonResponse>

    @GET(Constants.GET_SUBSCRIPTION_PLAN)
    suspend fun getSubscriptionPlan(): Response<GetAllPlanResponse>

    @POST(Constants.GET_PROFILE)
    suspend fun getSellerInfo(): Response<GetSellerInfo>

    @POST(Constants.CREATE_SUBSCRIPTION_PLAN)
    suspend fun createSubscriptionPlan(
        @Body map: HashMap<String, Any>
    ): Response<CreateSubscriptionPlanResponse>

    @POST(Constants.BUY_SUBSCRIPTION_PLAN)
    suspend fun buySubscriptionPlan(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

}