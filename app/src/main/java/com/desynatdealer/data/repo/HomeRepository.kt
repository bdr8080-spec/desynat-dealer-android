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

    suspend fun getCategory(): ApiResult<GetCategoryResponse> {
        return try {
            networkCall(api.getCategory())
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getSubCategory(categoryId:String): ApiResult<GetSubCategoryResponse> {
        return try {
            networkCall(api.getSubCategory(categoryId))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getColor(): ApiResult<GetColorResponse> {
        return try {
            networkCall(api.getColor())
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getBrand(): ApiResult<GetSellerBrandResponse> {
        return try {
            networkCall(api.getBrand())
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun addProduct(request: AddProductRequest): ApiResult<CommonResponse> {
        return try {
            networkCall(api.addProduct(request))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun editProduct(request: AddProductRequest): ApiResult<CommonResponse> {
        return try {
            networkCall(api.editProduct(request))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getSize(): ApiResult<GetSizeResponse> {
        return try {
            networkCall(api.getSize())
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getAllProduct(map: HashMap<String, Any>): ApiResult<GetAllProductResponse> {
        return try {
            networkCall(api.getAllProduct(map))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getProductDetail(map: HashMap<String, Any>): ApiResult<GetProductDetailResponse> {
        return try {
            networkCall(api.getProductDetail(map))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun updateProductStatus(map: HashMap<String, Any>): ApiResult<GetProductDetailResponse> {
        return try {
            networkCall(api.updateProductStatus(map))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun deleteProduct(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.deleteProduct(map))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun storeOnOff(): ApiResult<CommonResponse> {
        return try {
            networkCall(api.storeOnOff())
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
}