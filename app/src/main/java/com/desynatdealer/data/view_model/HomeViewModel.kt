package com.desynatdealer.data.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.repo.HomeRepository
import com.desynatdealer.model.login.req.LoginResponse
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject


@HiltViewModel
open class HomeViewModel @Inject constructor(private val repo: HomeRepository) :
    ViewModel() {

    val categoryList = MutableLiveData<List<GetCategoryResponse.Data>>()
    val colorList = MutableLiveData<List<GetColorResponse.Data>>()
    val brandList = MutableLiveData<List<GetSellerBrandResponse.Data>>()
    val sizeList = MutableLiveData<List<GetSizeResponse.Data>>()
    val productList = MutableLiveData<List<GetAllProductResponse.Data.AllProductData>>()


    private val _subscriptionPlanHomeResponse =
        MutableLiveData<ApiResult<GetAllPlanResponse>?>()
    val subscriptionPlanHomeResponse: MutableLiveData<ApiResult<GetAllPlanResponse>?>
        get() = _subscriptionPlanHomeResponse

    fun getSubscriptionHomePlan() {
        viewModelScope.launch {
            _subscriptionPlanHomeResponse.value = ApiResult.Loading
            _subscriptionPlanHomeResponse.value = repo.getSubscriptionPlan()
        }
    }

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

    private val _getSellerInfoResponse =
        MutableLiveData<ApiResult<GetSellerInfo>?>()
    val getSellerInfoResponse: MutableLiveData<ApiResult<GetSellerInfo>?>
        get() = _getSellerInfoResponse

    fun getSellerInformation() {
        viewModelScope.launch {
            _getSellerInfoResponse.value = ApiResult.Loading
            _getSellerInfoResponse.value = repo.getSellerInfo()
        }
    }

    private val _categoryResponse =
        MutableLiveData<ApiResult<GetCategoryResponse>?>()
    val categoryResponse: MutableLiveData<ApiResult<GetCategoryResponse>?>
        get() = _categoryResponse

    fun getCategory() {
        viewModelScope.launch {
            _categoryResponse.value = ApiResult.Loading
            _categoryResponse.value = repo.getCategory()
        }
    }

    private val _subCategoryResponse =
        MutableLiveData<ApiResult<GetSubCategoryResponse>?>()
    val subCategoryResponse: MutableLiveData<ApiResult<GetSubCategoryResponse>?>
        get() = _subCategoryResponse

    fun getSubCategory(categoryId:String) {
        viewModelScope.launch {
            _subCategoryResponse.value = ApiResult.Loading
            _subCategoryResponse.value = repo.getSubCategory(categoryId)
        }
    }

    private val _brandResponse =
        MutableLiveData<ApiResult<GetSellerBrandResponse>?>()
    val brandResponse: MutableLiveData<ApiResult<GetSellerBrandResponse>?>
        get() = _brandResponse

    fun getBrand() {
        viewModelScope.launch {
            _brandResponse.value = ApiResult.Loading
            _brandResponse.value = repo.getBrand()
        }
    }

    private val _colorResponse =
        MutableLiveData<ApiResult<GetColorResponse>?>()
    val colorResponse: MutableLiveData<ApiResult<GetColorResponse>?>
        get() = _colorResponse

    fun getColor() {
        viewModelScope.launch {
            _colorResponse.value = ApiResult.Loading
            _colorResponse.value = repo.getColor()
        }
    }

    private val _sizeResponse =
        MutableLiveData<ApiResult<GetSizeResponse>?>()
    val sizeResponse: MutableLiveData<ApiResult<GetSizeResponse>?>
        get() = _sizeResponse

    fun getSize() {
        viewModelScope.launch {
            _sizeResponse.value = ApiResult.Loading
            _sizeResponse.value = repo.getSize()
        }
    }

    private val _getProductResponse =
        MutableLiveData<ApiResult<GetAllProductResponse>?>()
    val getProductResponse: MutableLiveData<ApiResult<GetAllProductResponse>?>
        get() = _getProductResponse

    fun getAllProduct(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getProductResponse.value = ApiResult.Loading
            _getProductResponse.value = repo.getAllProduct(map)
        }
    }

    private val _getProductDetailResponse =
        MutableLiveData<ApiResult<GetProductDetailResponse>?>()
    val getProductDetailResponse: MutableLiveData<ApiResult<GetProductDetailResponse>?>
        get() = _getProductDetailResponse

    fun getProductDetail(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getProductDetailResponse.value = ApiResult.Loading
            _getProductDetailResponse.value = repo.getProductDetail(map)
        }
    }

    private val _updateProductStatusResponse =
        MutableLiveData<ApiResult<GetProductDetailResponse>?>()
    val updateProductStatusResponse: MutableLiveData<ApiResult<GetProductDetailResponse>?>
        get() = _updateProductStatusResponse

    fun updateProductStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateProductStatusResponse.value = ApiResult.Loading
            _updateProductStatusResponse.value = repo.updateProductStatus(map)
        }
    }

    private val _deleteProductResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val deleteProductResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _deleteProductResponse

    fun deleteProduct(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _deleteProductResponse.value = ApiResult.Loading
            _deleteProductResponse.value = repo.deleteProduct(map)
        }
    }

    private val _storeOnOffResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val storeOnOffResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _storeOnOffResponse

    fun storeOnOff() {
        viewModelScope.launch {
            _storeOnOffResponse.value = ApiResult.Loading
            _storeOnOffResponse.value = repo.storeOnOff()
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

    private val _addProductResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val addProductResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _addProductResponse

    fun addProduct(
       request: AddProductRequest
    ) {
        viewModelScope.launch {
            _addProductResponse.value = ApiResult.Loading
            _addProductResponse.value = repo.addProduct(request)
        }
    }

    private val _editProductResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val editProductResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _editProductResponse

    fun editProduct(
        request: AddProductRequest
    ) {
        viewModelScope.launch {
            _editProductResponse.value = ApiResult.Loading
            _editProductResponse.value = repo.editProduct(request)
        }
    }

    private val _getInventoryProductResponse =
        MutableLiveData<ApiResult<GetAllProductResponse>?>()
    val getInventoryProductResponse: MutableLiveData<ApiResult<GetAllProductResponse>?>
        get() = _getInventoryProductResponse
    fun getAllInventoryProduct(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getInventoryProductResponse.value = ApiResult.Loading
            _getInventoryProductResponse.value = repo.getAllInventoryProduct(map)
        }
    }

    private val _getInventoryProductDetailResponse =
        MutableLiveData<ApiResult<GetProductDetailResponse>?>()
    val getInventoryProductDetailResponse: MutableLiveData<ApiResult<GetProductDetailResponse>?>
        get() = _getInventoryProductDetailResponse

    fun getInventoryProductDetail(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getInventoryProductDetailResponse.value = ApiResult.Loading
            _getInventoryProductDetailResponse.value = repo.getInventoryProductDetail(map)
        }
    }

    private val _updateInventoryProductStatusResponse =
        MutableLiveData<ApiResult<GetProductDetailResponse>?>()
    val updateInventoryProductStatusResponse: MutableLiveData<ApiResult<GetProductDetailResponse>?>
        get() = _updateInventoryProductStatusResponse

    fun updateInventoryProductStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateInventoryProductStatusResponse.value = ApiResult.Loading
            _updateInventoryProductStatusResponse.value = repo.updateInventoryProductStatus(map)
        }
    }

    private val _updateInventoryProductStockResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val updateInventoryProductStockResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _updateInventoryProductStockResponse

    fun updateInventoryProductStock(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateInventoryProductStockResponse.value = ApiResult.Loading
            _updateInventoryProductStockResponse.value = repo.updateInventoryProductStock(map)
        }
    }

    private val _updateAffiliateCommissionResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val updateAffiliateCommissionResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _updateAffiliateCommissionResponse

    fun updateAffiliateCommission(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateAffiliateCommissionResponse.value = ApiResult.Loading
            _updateAffiliateCommissionResponse.value = repo.updateAffiliateCommission(map)
        }
    }

    private val _updateStoreInfoResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val updateStoreInfoResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _updateStoreInfoResponse

    fun updateStoreInfo(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateStoreInfoResponse.value = ApiResult.Loading
            _updateStoreInfoResponse.value = repo.updateStoreInfo(map)
        }
    }

    private val _updateBankDetailResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val updateBankDetailResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _updateBankDetailResponse

    fun updateBankDetail(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateBankDetailResponse.value = ApiResult.Loading
            _updateBankDetailResponse.value = repo.updateBankDetail(map)
        }
    }


}