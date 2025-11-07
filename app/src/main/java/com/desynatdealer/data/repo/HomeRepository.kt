package com.desynatdealer.data.repo

import com.desynatdealer.controller.ApiResult
import com.desynatdealer.controller.CommonApiService
import com.desynatdealer.controller.networkCall
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.model.create_account.CreateAccountRes
import com.desynatdealer.model.login.req.LoginReq
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.model.upload.UploadResponse
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class HomeRepository @Inject constructor(private val api: CommonApiService) {

    suspend fun getSubscriptionPlan(): ApiResult<GetAllPlanResponse> {
        return try {
            networkCall(api.getSubscriptionPlan())
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }
}