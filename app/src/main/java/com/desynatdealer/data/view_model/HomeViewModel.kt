package com.desynatdealer.data.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.repo.HomeRepository
import com.desynatdealer.ui.home.response.GetAllPlanResponse
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

}