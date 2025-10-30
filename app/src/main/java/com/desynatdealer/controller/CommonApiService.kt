package com.desynatdealer.controller

import com.desynatdealer.model.create_account.CreateAccountRes
import com.desynatdealer.model.login.req.LoginReq
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.model.upload.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CommonApiService {

    @POST("api/seller/login")
    suspend fun login(@Body loginReq: LoginReq): Response<Unit>


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

}