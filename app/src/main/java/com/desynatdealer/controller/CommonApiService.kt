package com.desynatdealer.controller

import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.model.create_account.CreateAccountRes
import com.desynatdealer.model.login.req.LoginReq
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.model.upload.UploadResponse
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetProductDetailResponse
import com.desynatdealer.ui.home.productManagement.response.GetSellerBrandResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.desynatdealer.ui.home.response.CommonResponse
import com.desynatdealer.ui.home.response.CreateSubscriptionPlanResponse
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

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

    @GET(Constants.GET_CATEGORY)
    suspend fun getCategory(): Response<GetCategoryResponse>

    @GET(Constants.GET_SUB_CATEGORY)
    suspend fun getSubCategory(
        @Query ("category_id") categoryId:String
    ): Response<GetSubCategoryResponse>

    @GET(Constants.GET_BRAND)
    suspend fun getBrand(): Response<GetSellerBrandResponse>

    @GET(Constants.GET_COLOR)
    suspend fun getColor(): Response<GetColorResponse>

    @GET(Constants.GET_SIZE)
    suspend fun getSize(): Response<GetSizeResponse>

    @GET(Constants.STORE_ON_OFF)
    suspend fun storeOnOff(): Response<CommonResponse>

    @POST(Constants.GET_ALL_PRODUCT)
    suspend fun getAllProduct(@Body map: HashMap<String, Any>): Response<GetAllProductResponse>

    @POST(Constants.ADD_PRODUCT)
    suspend fun addProduct(@Body request : AddProductRequest): Response<CommonResponse>

    @POST(Constants.UPDATE_PRODUCT)
    suspend fun editProduct(@Body request : AddProductRequest): Response<CommonResponse>

    @POST(Constants.PRODUCT_DETAIL)
    suspend fun getProductDetail(@Body map: HashMap<String, Any>): Response<GetProductDetailResponse>

    @PUT(Constants.PRODUCT_ACTIVE_INACTIVE)
    suspend fun updateProductStatus(@Body map: HashMap<String, Any>): Response<GetProductDetailResponse>

 //   @DELETE(Constants.DELETE_PRODUCT)
    @HTTP(method = "DELETE", path = Constants.DELETE_PRODUCT, hasBody = true)

    suspend fun deleteProduct(@Body map: HashMap<String, Any>): Response<CommonResponse>
}