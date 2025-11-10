package com.desynatdealer.data.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.repo.HomeRepository
import com.desynatdealer.ui.home.response.CommonResponse
import com.desynatdealer.ui.home.response.CreateSubscriptionPlanResponse
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
open class HomeViewModel @Inject constructor(private val repo: HomeRepository) :
    ViewModel() {

    private val _subscriptionPlanResponse =
        MutableLiveData<ApiResult<GetAllPlanResponse>?>()
    val subscriptionPlanResponse: MutableLiveData<ApiResult<GetAllPlanResponse>?>
        get() = _subscriptionPlanResponse

    fun getSubscriptionPlan() {
        viewModelScope.launch {
            _subscriptionPlanResponse.value = ApiResult.Loading
            _subscriptionPlanResponse.value = repo.getSubscriptionPlan()
        }
    }


    private val _createSubscriptionPlanResponse =
        MutableLiveData<ApiResult<CreateSubscriptionPlanResponse>?>()
    val createSubscriptionPlanResponse: MutableLiveData<ApiResult<CreateSubscriptionPlanResponse>?>
        get() = _createSubscriptionPlanResponse

    fun createSubscriptionPlan(map:HashMap<String,Any>) {
        viewModelScope.launch {
            _createSubscriptionPlanResponse.value = ApiResult.Loading
            _createSubscriptionPlanResponse.value = repo.createSubscriptionPlan(map)
        }
    }

    private val _buySubscriptionPlanResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val buySubscriptionPlanResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _buySubscriptionPlanResponse

    fun buySubscriptionPlan(map:HashMap<String,Any>) {
        viewModelScope.launch {
            _buySubscriptionPlanResponse.value = ApiResult.Loading
            _buySubscriptionPlanResponse.value = repo.buySubscriptionPlan(map)
        }
    }

    private val _logoutResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val logoutResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _logoutResponse

    fun logout() {
        viewModelScope.launch {
            _logoutResponse.value = ApiResult.Loading
            _logoutResponse.value = repo.logout()
        }
    }

    private val _sellerInfoResponse =
        MutableLiveData<ApiResult<GetSellerInfo>?>()
    val sellerInfoResponse: MutableLiveData<ApiResult<GetSellerInfo>?>
        get() = _sellerInfoResponse

    fun getSellerInfo() {
        viewModelScope.launch {
            _sellerInfoResponse.value = ApiResult.Loading
            _sellerInfoResponse.value = repo.getSellerInfo()
        }
    }

}