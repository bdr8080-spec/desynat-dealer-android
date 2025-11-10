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
import com.desynatdealer.ui.home.response.CommonResponse
import com.desynatdealer.ui.home.response.CreateSubscriptionPlanResponse
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
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

    suspend fun createSubscriptionPlan(map:HashMap<String,Any>): ApiResult<CreateSubscriptionPlanResponse> {
        return try {
            networkCall(api.createSubscriptionPlan(map))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun buySubscriptionPlan(map:HashMap<String,Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.buySubscriptionPlan(map))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }


    suspend fun logout(): ApiResult<CommonResponse> {
        return try {
            networkCall(api.logout())
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getSellerInfo(): ApiResult<GetSellerInfo> {
        return try {
            networkCall(api.getSellerInfo())
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }
}