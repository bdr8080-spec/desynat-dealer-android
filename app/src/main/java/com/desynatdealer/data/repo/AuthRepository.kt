package com.desynatdealer.data.repo

import com.desynatdealer.controller.ApiResult
import com.desynatdealer.controller.CommonApiService
import com.desynatdealer.controller.networkCall
import com.desynatdealer.model.create_account.CreateAccountRes
import com.desynatdealer.model.login.req.LoginReq
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.model.upload.UploadResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthRepository @Inject constructor(private val api: CommonApiService) {

    suspend fun login(emailPhone: String, password: String): ApiResult<Unit> {
        return try {
            networkCall(api.login(LoginReq(emailPhone, password)))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun upload(
        file: MultipartBody.Part?,
    ): ApiResult<UploadResponse> {
        return try {
            networkCall(api.upload(file))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun createAccount(
        req: HashMap<String, String>
    ): ApiResult<CreateAccountRes> {
        return try {
            networkCall(api.createAccount(req))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun verifyOtp(
        req: HashMap<String, String>
    ): ApiResult<Unit> {
        return try {
            networkCall(api.verifyOtp(req))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun storeInformation(
        req: HashMap<String, String>
    ): ApiResult<Unit> {
        return try {
            networkCall(api.storeInformation(req))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun storeDocumentAndBankDetails(req: StoreDocumentAndBankDetailsReq): ApiResult<Unit>  {
        return try {
            networkCall(api.storeDocumentAndBankDetails(req))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }
}