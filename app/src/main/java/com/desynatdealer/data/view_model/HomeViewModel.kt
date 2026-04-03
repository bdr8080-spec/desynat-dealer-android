package com.desynatdealer.data.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.repo.HomeRepository
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.model.upload.UploadResponse
import com.desynatdealer.ui.home.bannerManagement.response.AddBannerRequest
import com.desynatdealer.ui.home.bannerManagement.response.AddBannerResponse
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.ui.home.bannerManagement.response.GetBannerPriceResponse
import com.desynatdealer.ui.home.chat.response.ChatDetailResponse
import com.desynatdealer.ui.home.chat.response.GetChatListResponse
import com.desynatdealer.ui.home.chat.response.ModifySingleChatResponse
import com.desynatdealer.ui.home.dealManagement.response.GetAllDealResponse
import com.desynatdealer.ui.home.dealManagement.response.GetDealAllProductResponse
import com.desynatdealer.ui.home.dealManagement.response.GetDealDetailResponse
import com.desynatdealer.ui.home.fragment.response.GetNotificationCountResponse
import com.desynatdealer.ui.home.fragment.response.GetUserNotificationResponse
import com.desynatdealer.ui.home.liveCoverage.response.GetLiveRequestResponse
import com.desynatdealer.ui.home.liveCoverage.response.GetTimeSlotResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetAgoraTokenResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetLiveStreamingRoomResponse
import com.desynatdealer.ui.home.mySubscriptionPlan.response.GetMyPlanResponse
import com.desynatdealer.ui.home.mySubscriptionPlan.response.RenewSubscriptionPlanResponse
import com.desynatdealer.ui.home.orderManagement.response.GetAllOrderResponse
import com.desynatdealer.ui.home.orderManagement.response.GetSingleOrderResponse
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetProductDetailResponse
import com.desynatdealer.ui.home.productManagement.response.GetSellerBrandResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.ui.home.profile.response.UpdateReelResponse
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

    private val _changeNumberResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val changeNumberResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _changeNumberResponse

    fun changeNumber(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _changeNumberResponse.value = ApiResult.Loading
            _changeNumberResponse.value = repo.changeNumber(map)
        }
    }

    private val _otpVerificationResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val otpVerificationResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _otpVerificationResponse

    fun otpVerification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _otpVerificationResponse.value = ApiResult.Loading
            _otpVerificationResponse.value = repo.otpVerification(map)
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

    private val _addBannerResponse =
        MutableLiveData<ApiResult<AddBannerResponse>?>()
    val addBannerResponse: MutableLiveData<ApiResult<AddBannerResponse>?>
        get() = _addBannerResponse

    fun addBanner(request: AddBannerRequest) {
        viewModelScope.launch {
            _addBannerResponse.value = ApiResult.Loading
            _addBannerResponse.value = repo.addBanner(request)
        }
    }

    private val _getBannerResponse =
        MutableLiveData<ApiResult<GetAllBannerResponse>?>()
    val getBannerResponse: MutableLiveData<ApiResult<GetAllBannerResponse>?>
        get() = _getBannerResponse

    fun getBanner(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getBannerResponse.value = ApiResult.Loading
            _getBannerResponse.value = repo.getBanner(map)
        }
    }

    private val _getBannerPriceResponse =
        MutableLiveData<ApiResult<GetBannerPriceResponse>?>()
    val getBannerPriceResponse: MutableLiveData<ApiResult<GetBannerPriceResponse>?>
        get() = _getBannerPriceResponse

    fun getBannerPrice(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getBannerPriceResponse.value = ApiResult.Loading
            _getBannerPriceResponse.value = repo.getBannerPrice(map)
        }
    }

    private val _bannerPaymentResponse =
        MutableLiveData<ApiResult<AddBannerResponse>?>()
    val bannerPaymentResponse: MutableLiveData<ApiResult<AddBannerResponse>?>
        get() = _bannerPaymentResponse

    fun bannerPayment(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _bannerPaymentResponse.value = ApiResult.Loading
            _bannerPaymentResponse.value = repo.bannerPayment(map)
        }
    }

    private val _getOrderResponse =
        MutableLiveData<ApiResult<GetAllOrderResponse>?>()
    val getOrderResponse: MutableLiveData<ApiResult<GetAllOrderResponse>?>
        get() = _getOrderResponse

    fun getOrder(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getOrderResponse.value = ApiResult.Loading
            _getOrderResponse.value = repo.getOrder(map)
        }
    }

    private val _getOrderDetailResponse =
        MutableLiveData<ApiResult<GetSingleOrderResponse>?>()
    val getOrderDetailResponse: MutableLiveData<ApiResult<GetSingleOrderResponse>?>
        get() = _getOrderDetailResponse

    fun getOrderDetail(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getOrderDetailResponse.value = ApiResult.Loading
            _getOrderDetailResponse.value = repo.getOrderDetail(map)
        }
    }

    private val _updateOrderStatusResponse =
        MutableLiveData<ApiResult<GetSingleOrderResponse>?>()
    val updateOrderStatusResponse: MutableLiveData<ApiResult<GetSingleOrderResponse>?>
        get() = _updateOrderStatusResponse

    fun updateOrderStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateOrderStatusResponse.value = ApiResult.Loading
            _updateOrderStatusResponse.value = repo.updateOrderStatus(map)
        }
    }

    private val _updateOrderExchangeReturnStatusResponse =
        MutableLiveData<ApiResult<GetSingleOrderResponse>?>()
    val updateOrderExchangeReturnStatusResponse: MutableLiveData<ApiResult<GetSingleOrderResponse>?>
        get() = _updateOrderExchangeReturnStatusResponse

    fun updateOrderExchangeReturnStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateOrderExchangeReturnStatusResponse.value = ApiResult.Loading
            _updateOrderExchangeReturnStatusResponse.value = repo.updateOrderExchangeReturnStatus(map)
        }
    }

    private val _getAllChatListResponse =
        MutableLiveData<ApiResult<GetChatListResponse>?>()
    val getAllChatListResponse: MutableLiveData<ApiResult<GetChatListResponse>?>
        get() = _getAllChatListResponse

    fun getAllChatList(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getAllChatListResponse.value = ApiResult.Loading
            _getAllChatListResponse.value = repo.getAllChatList(map)
        }
    }

    private val _getChatDetailResponse =
        MutableLiveData<ApiResult<ChatDetailResponse>?>()
    val getChatDetailResponse: MutableLiveData<ApiResult<ChatDetailResponse>?>
        get() = _getChatDetailResponse

    fun getChatDetail(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getChatDetailResponse.value = ApiResult.Loading
            _getChatDetailResponse.value = repo.getChatDetail(map)
        }
    }

    private val _modifySingleChatResponse =
        MutableLiveData<ApiResult<ModifySingleChatResponse>?>()
    val modifySingleChatResponse: MutableLiveData<ApiResult<ModifySingleChatResponse>?>
        get() = _modifySingleChatResponse

    fun modifySingleChat(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _modifySingleChatResponse.value = ApiResult.Loading
            _modifySingleChatResponse.value = repo.modifySingleChat(map)
        }
    }

    private val _getDealResponse =
        MutableLiveData<ApiResult<GetAllDealResponse>?>()
    val getDealResponse: MutableLiveData<ApiResult<GetAllDealResponse>?>
        get() = _getDealResponse

    fun getDeal(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getDealResponse.value = ApiResult.Loading
            _getDealResponse.value = repo.getDeal(map)
        }
    }

    private val _getDealDetailResponse =
        MutableLiveData<ApiResult<GetDealDetailResponse>?>()
    val getDealDetailResponse: MutableLiveData<ApiResult<GetDealDetailResponse>?>
        get() = _getDealDetailResponse

    fun getDealDetail(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getDealDetailResponse.value = ApiResult.Loading
            _getDealDetailResponse.value = repo.getDealDetail(map)
        }
    }

 private val _addProductInDealResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val addProductInDealResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _addProductInDealResponse

    fun addProductInDeal(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _addProductInDealResponse.value = ApiResult.Loading
            _addProductInDealResponse.value = repo.addProductInDeal(map)
        }
    }

 private val _removeProductInDealResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val removeProductInDealResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _removeProductInDealResponse

    fun removeProductInDeal(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _removeProductInDealResponse.value = ApiResult.Loading
            _removeProductInDealResponse.value = repo.removeProductInDeal(map)
        }
    }
 private val _showProductInDealCategorySubcategoryResponse =
        MutableLiveData<ApiResult<GetDealAllProductResponse>?>()
    val showProductInDealCategorySubcategoryResponse: MutableLiveData<ApiResult<GetDealAllProductResponse>?>
        get() = _showProductInDealCategorySubcategoryResponse

    fun showProductInDealCategorySubcategory(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _showProductInDealCategorySubcategoryResponse.value = ApiResult.Loading
            _showProductInDealCategorySubcategoryResponse.value = repo.showProductInDealCategorySubcategory(map)
        }
    }

    private val _getTimeSlotResponse =
        MutableLiveData<ApiResult<GetTimeSlotResponse>?>()
    val getTimeSlotResponse: MutableLiveData<ApiResult<GetTimeSlotResponse>?>
        get() = _getTimeSlotResponse

    fun getTimeSlot(date: String) {
        viewModelScope.launch {
            _getTimeSlotResponse.value = ApiResult.Loading
            _getTimeSlotResponse.value = repo.getTimeSlot(date)
        }
    }

 private val _getLiveRequestResponse =
        MutableLiveData<ApiResult<GetLiveRequestResponse>?>()
    val getLiveRequestResponse: MutableLiveData<ApiResult<GetLiveRequestResponse>?>
        get() = _getLiveRequestResponse

    fun getLiveRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getLiveRequestResponse.value = ApiResult.Loading
            _getLiveRequestResponse.value = repo.getLiveRequest(map)
        }
    }

    private val _cancelLiveRequestResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val cancelLiveRequestResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _cancelLiveRequestResponse

    fun cancelLiveRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _cancelLiveRequestResponse.value = ApiResult.Loading
            _cancelLiveRequestResponse.value = repo.cancelLiveRequest(map)
        }
    }
    private val _sendLiveRequestResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val sendLiveRequestResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _sendLiveRequestResponse

    fun sendLiveRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _sendLiveRequestResponse.value = ApiResult.Loading
            _sendLiveRequestResponse.value = repo.sendLiveRequest(map)
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

    private val _getNotificationListResponse =
        MutableLiveData<ApiResult<GetUserNotificationResponse>?>()
    val getNotificationListResponse: MutableLiveData<ApiResult<GetUserNotificationResponse>?>
        get() = _getNotificationListResponse

    fun getNotificationList(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getNotificationListResponse.value = ApiResult.Loading
            _getNotificationListResponse.value = repo.getNotificationList(map)
        }
    }

    private val _getNotificationCountResponse =
        MutableLiveData<ApiResult<GetNotificationCountResponse>?>()
    val getNotificationCountResponse: MutableLiveData<ApiResult<GetNotificationCountResponse>?>
        get() = _getNotificationCountResponse

    fun getNotificationCount() {
        viewModelScope.launch {
            _getNotificationCountResponse.value = ApiResult.Loading
            _getNotificationCountResponse.value = repo.getNotificationCount()
        }
    }


    private val _readNotificationResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val readNotificationResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _readNotificationResponse

    fun readNotification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _readNotificationResponse.value = ApiResult.Loading
            _readNotificationResponse.value = repo.readNotification(map)
        }
    }

    private val _readAllNotificationResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val readAllNotificationResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _readAllNotificationResponse

    fun readAllNotification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _readAllNotificationResponse.value = ApiResult.Loading
            _readAllNotificationResponse.value = repo.readAllNotification(map)
        }
    }

    private val _renewSubscriptionPlanResponse =
        MutableLiveData<ApiResult<RenewSubscriptionPlanResponse>?>()
    val renewSubscriptionPlanResponse: MutableLiveData<ApiResult<RenewSubscriptionPlanResponse>?>
        get() = _renewSubscriptionPlanResponse

    fun renewSubscriptionPlan(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _renewSubscriptionPlanResponse.value = ApiResult.Loading
            _renewSubscriptionPlanResponse.value = repo.renewSubscriptionPlan(map)
        }
    }

   private val _getMyPlanResponse =
        MutableLiveData<ApiResult<GetMyPlanResponse>?>()
    val getMyPlanResponse: MutableLiveData<ApiResult<GetMyPlanResponse>?>
        get() = _getMyPlanResponse

    fun getMyPlan(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getMyPlanResponse.value = ApiResult.Loading
            _getMyPlanResponse.value = repo.getMyPlan(map)
        }
    }



    private val _getStoryResponse =
        MutableLiveData<ApiResult<GetStoryResponse>?>()
    val getStoryResponse: MutableLiveData<ApiResult<GetStoryResponse>?>
        get() = _getStoryResponse

    fun getStory(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getStoryResponse.value = ApiResult.Loading
            _getStoryResponse.value = repo.getStory(map)
        }
    }

    private val _getHighlightsResponse =
        MutableLiveData<ApiResult<GetStoryResponse>?>()
    val getHighlightsResponse: MutableLiveData<ApiResult<GetStoryResponse>?>
        get() = _getHighlightsResponse

    fun getHighlights(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getHighlightsResponse.value = ApiResult.Loading
            _getHighlightsResponse.value = repo.getHighlights(map)
        }
    }

    private val _getReelResponse =
        MutableLiveData<ApiResult<GetStoryResponse>?>()
    val getReelResponse: MutableLiveData<ApiResult<GetStoryResponse>?>
        get() = _getReelResponse

    fun getReel(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getReelResponse.value = ApiResult.Loading
            _getReelResponse.value = repo.getReel(map)
        }
    }

    private val _updateReelStatusResponse =
        MutableLiveData<ApiResult<UpdateReelResponse>?>()
    val updateReelStatusResponse: MutableLiveData<ApiResult<UpdateReelResponse>?>
        get() = _updateReelStatusResponse

    fun updateReelStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateReelStatusResponse.value = ApiResult.Loading
            _updateReelStatusResponse.value = repo.updateReelStatus(map)
        }
    }

    private val _updateReelResponse =
        MutableLiveData<ApiResult<UpdateReelResponse>?>()
    val updateReelResponse: MutableLiveData<ApiResult<UpdateReelResponse>?>
        get() = _updateReelResponse

    fun updateReel(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _updateReelResponse.value = ApiResult.Loading
            _updateReelResponse.value = repo.updateReel(map)
        }
    }

    private val _deleteReelResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val deleteReelResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _deleteReelResponse

    fun deleteReel(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _deleteReelResponse.value = ApiResult.Loading
            _deleteReelResponse.value = repo.deleteReel(map)
        }
    }

    private val _deleteHighlightsResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val deleteHighlightsResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _deleteHighlightsResponse

    fun deleteHighlights(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _deleteHighlightsResponse.value = ApiResult.Loading
            _deleteHighlightsResponse.value = repo.deleteHighlights(map)
        }
    }

    private val _deleteStoryResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val deleteStoryResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _deleteStoryResponse

    fun deleteStory(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _deleteStoryResponse.value = ApiResult.Loading
            _deleteStoryResponse.value = repo.deleteStory(map)
        }
    }

    private val _addReelResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val addReelResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _addReelResponse

    fun addReel(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _addReelResponse.value = ApiResult.Loading
            _addReelResponse.value = repo.addReel(map)
        }
    }

    private val _addHighlightsResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val addHighlightsResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _addHighlightsResponse

    fun addHighlights(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _addHighlightsResponse.value = ApiResult.Loading
            _addHighlightsResponse.value = repo.addHighlights(map)
        }
    }

    private val _addStoryResponse =
        MutableLiveData<ApiResult<CommonResponse>?>()
    val addStoryResponse: MutableLiveData<ApiResult<CommonResponse>?>
        get() = _addStoryResponse

    fun addStory(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _addStoryResponse.value = ApiResult.Loading
            _addStoryResponse.value = repo.addStory(map)
        }
    }

    private val _uploadVideoImageResponse =
        MutableLiveData<ApiResult<UploadResponse>?>()
    val uploadVideoImageResponse: MutableLiveData<ApiResult<UploadResponse>?>
        get() = _uploadVideoImageResponse

    fun uploadVideoImage(file: MultipartBody.Part?,) {
        viewModelScope.launch {
            _uploadVideoImageResponse.value = ApiResult.Loading
            _uploadVideoImageResponse.value = repo.uploadVideoImage(file)
        }
    }

    private val _uploadWithResolutionResponse =
        MutableLiveData<ApiResult<UploadResponse>?>()
    val uploadWithResolutionResponse: MutableLiveData<ApiResult<UploadResponse>?>
        get() = _uploadWithResolutionResponse

    fun uploadWithResolution(file: MultipartBody.Part?,type: String) {
        viewModelScope.launch {
            _uploadWithResolutionResponse.value = ApiResult.Loading
            _uploadWithResolutionResponse.value = repo.uploadWithResolution(file,type)
        }
    }


    private val _getLiveRoomRecordsResponse =
        MutableLiveData<ApiResult<GetLiveStreamingRoomResponse>?>()
    val getLiveRoomRecordsResponse: MutableLiveData<ApiResult<GetLiveStreamingRoomResponse>?>
        get() = _getLiveRoomRecordsResponse

    fun getLiveRoomRecords() {
        viewModelScope.launch {
            _getLiveRoomRecordsResponse.value = ApiResult.Loading
            _getLiveRoomRecordsResponse.value = repo.getLiveRoomRecords()
        }
    }

    private val _getAgoraTokenResponse =
        MutableLiveData<ApiResult<GetAgoraTokenResponse>?>()
    val getAgoraTokenResponse: MutableLiveData<ApiResult<GetAgoraTokenResponse>?>
        get() = _getAgoraTokenResponse

    fun getAgoraToken(map: HashMap<String, Any>) {
        viewModelScope.launch {
            _getAgoraTokenResponse.value = ApiResult.Loading
            _getAgoraTokenResponse.value = repo.getAgoraToken(map)
        }
    }


}