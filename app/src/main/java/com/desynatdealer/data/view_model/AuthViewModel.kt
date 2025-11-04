package com.desynatdealer.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.repo.AuthRepository
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.model.create_account.CreateAccountRes
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.model.upload.UploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
open class AuthViewModel @Inject constructor(private val repo: AuthRepository) :
    ViewModel() {

    private val _loginResponse =
        MutableLiveData<ApiResult<LoginResponse>?>()
    val loginResponse: MutableLiveData<ApiResult<LoginResponse>?>
        get() = _loginResponse

    fun login(map: HashMap<String,Any>) {
        viewModelScope.launch {
            viewModelScope.launch {
                _loginResponse.value = ApiResult.Loading
                _loginResponse.value = repo.login(map)
            }
        }
    }


    private val _uploadResponse =
        MutableLiveData<ApiResult<UploadResponse>?>()
    val uploadResponse: MutableLiveData<ApiResult<UploadResponse>?>
        get() = _uploadResponse

    fun upload(
        file: MultipartBody.Part?,
    ) {
        viewModelScope.launch {
            _uploadResponse.value = ApiResult.Loading
            _uploadResponse.value = repo.upload(file)
        }
    }

    private val _createAccountResponse =
        MutableLiveData<ApiResult<CreateAccountRes>?>()
    val createAccountResponse: MutableLiveData<ApiResult<CreateAccountRes>?>
        get() = _createAccountResponse

    fun createAccount(
        req: HashMap<String, String>,
    ) {
        viewModelScope.launch {
            _createAccountResponse.value = ApiResult.Loading
            _createAccountResponse.value = repo.createAccount(req)
        }
    }


    private val _verifyOtpResponse =
        MutableLiveData<ApiResult<Unit>?>()
    val verifyOtpResponse: MutableLiveData<ApiResult<Unit>?>
        get() = _verifyOtpResponse

    fun verifyOtp(req: HashMap<String, String>) {
        viewModelScope.launch {
            _verifyOtpResponse.value = ApiResult.Loading
            _verifyOtpResponse.value = repo.verifyOtp(req)
        }
    }


    private val _storeInformationResponse =
        MutableLiveData<ApiResult<Unit>?>()
    val storeInformationResponse: MutableLiveData<ApiResult<Unit>?>
        get() = _storeInformationResponse

    fun storeInformation(req: HashMap<String, String>) {
        viewModelScope.launch {
            _storeInformationResponse.value = ApiResult.Loading
            _storeInformationResponse.value = repo.storeInformation(req)
        }
    }


    private val _storeDocumentAndBankDetailsResponse =
        MutableLiveData<ApiResult<Unit>?>()
    val storeDocumentAndBankDetailsResponse: MutableLiveData<ApiResult<Unit>?>
        get() = _storeDocumentAndBankDetailsResponse

    fun storeDocumentAndBankDetails(req: StoreDocumentAndBankDetailsReq) {
        viewModelScope.launch {
            _storeDocumentAndBankDetailsResponse.value = ApiResult.Loading
            _storeDocumentAndBankDetailsResponse.value = repo.storeDocumentAndBankDetails(req)
        }
    }


    private val _sellerStoreSubmitInfoResponse =
        MutableLiveData<ApiResult<Unit>?>()
    val sellerStoreSubmitInfoResponse: MutableLiveData<ApiResult<Unit>?>
        get() = _sellerStoreSubmitInfoResponse

    fun sellerStoreSubmitRequest(req: HashMap<String, String>) {
        viewModelScope.launch {
            _sellerStoreSubmitInfoResponse.value = ApiResult.Loading
            _sellerStoreSubmitInfoResponse.value = repo.sellerStoreSubmitRequest(req)
        }
    }

    private val _forgotPasswordResponse =
        MutableLiveData<ApiResult<CreateAccountRes>?>()
    val forgotPasswordResponse: MutableLiveData<ApiResult<CreateAccountRes>?>
        get() = _forgotPasswordResponse

    fun forgotPassword(req: HashMap<String, String>) {
        viewModelScope.launch {
            _forgotPasswordResponse.value = ApiResult.Loading
            _forgotPasswordResponse.value = repo.forgotPassword(req)
        }
    }


    private val _changePasswordResponse =
        MutableLiveData<ApiResult<Unit>?>()
    val changePasswordResponse: MutableLiveData<ApiResult<Unit>?>
        get() = _changePasswordResponse

    fun changePassword(req: HashMap<String, String>) {
        viewModelScope.launch {
            _changePasswordResponse.value = ApiResult.Loading
            _changePasswordResponse.value = repo.changePassword(req)
        }
    }


    private val _resendOtpResponse =
        MutableLiveData<ApiResult<Unit>?>()
    val resendOtpResponse: MutableLiveData<ApiResult<Unit>?>
        get() = _resendOtpResponse

    fun resendOtp(map: HashMap<String, String>) {
        viewModelScope.launch {
            _resendOtpResponse.value = ApiResult.Loading
            _resendOtpResponse.value = repo.resendOtp(map)
        }
    }

    private val _countryResponse =
        MutableLiveData<ApiResult<GetCountryResponse>?>()
    val countryResponse: MutableLiveData<ApiResult<GetCountryResponse>?>
        get() = _countryResponse

    fun getCountry() {
        viewModelScope.launch {
            _countryResponse.value = ApiResult.Loading
            _countryResponse.value = repo.getCountry()
        }
    }

}