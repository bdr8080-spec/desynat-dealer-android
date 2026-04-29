package com.desynatdealer.data.repo

import com.desynatdealer.controller.ApiResult
import com.desynatdealer.controller.CommonApiService
import com.desynatdealer.controller.networkCall
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
import com.desynatdealer.utils.toPlainRequestBody
import com.desynatdealer.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatdealer.ui.home.cms.response.GetContactResponse
import com.desynatdealer.ui.home.cms.response.GetTicketTypeResponse
import com.desynatdealer.ui.home.cms.response.UpdateNotificationResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetOtherLiveStreamingRoomResponse
import com.desynatdealer.ui.home.orderManagement.response.GetOrderNotificationCountResponse
import com.desynatdealer.ui.home.refundManagement.response.GetRefundOrderResponse
import com.desynatdealer.ui.home.staffManagement.response.AddNewStaffRequest
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.ui.home.staffManagement.response.GetSellerAllMenuResponse
import com.desynatdealer.ui.home.staffManagement.response.GetStaffCategoryResponse
import com.desynatdealer.ui.home.staffManagement.response.UpdateStaffStatusResponse
import com.desynatdealer.ui.home.staffManagement.response.ViewStaffDetailResponse
import com.desynatdriver.ui.home.cms.response.GetCmsResponse
import com.desynatdriver.ui.home.cms.response.GetFaqResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class HomeRepository @Inject constructor(private val api: CommonApiService) {

    suspend fun getSubscriptionPlan(): ApiResult<GetAllPlanResponse> {
        return try {
            networkCall(api.getSubscriptionPlan())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun createSubscriptionPlan(map: HashMap<String, Any>): ApiResult<CreateSubscriptionPlanResponse> {
        return try {
            networkCall(api.createSubscriptionPlan(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun buySubscriptionPlan(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.buySubscriptionPlan(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }


    suspend fun logout(token: String): ApiResult<CommonResponse> {
        return try {
            networkCall(api.logout(token))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getSellerInfo(token: String): ApiResult<GetSellerInfo> {
        return try {
            networkCall(api.getSellerInfo(token))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getCategory(): ApiResult<GetCategoryResponse> {
        return try {
            networkCall(api.getCategory())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getSubCategory(categoryId: String): ApiResult<GetSubCategoryResponse> {
        return try {
            networkCall(api.getSubCategory(categoryId))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getColor(): ApiResult<GetColorResponse> {
        return try {
            networkCall(api.getColor())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getBrand(): ApiResult<GetSellerBrandResponse> {
        return try {
            networkCall(api.getBrand())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun addProduct(request: AddProductRequest): ApiResult<CommonResponse> {
        return try {
            networkCall(api.addProduct(request))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun editProduct(request: AddProductRequest): ApiResult<CommonResponse> {
        return try {
            networkCall(api.editProduct(request))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getSize(): ApiResult<GetSizeResponse> {
        return try {
            networkCall(api.getSize())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getAllProduct(map: HashMap<String, Any>): ApiResult<GetAllProductResponse> {
        return try {
            networkCall(api.getAllProduct(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getProductDetail(map: HashMap<String, Any>): ApiResult<GetProductDetailResponse> {
        return try {
            networkCall(api.getProductDetail(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateProductStatus(map: HashMap<String, Any>): ApiResult<GetProductDetailResponse> {
        return try {
            networkCall(api.updateProductStatus(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun deleteProduct(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.deleteProduct(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun storeOnOff(): ApiResult<CommonResponse> {
        return try {
            networkCall(api.storeOnOff())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun upload(
        file: MultipartBody.Part?,
    ): ApiResult<UploadResponse> {
        return try {
            networkCall(api.upload(file))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }


    suspend fun uploadWithResolutionPromotional(
        file: MultipartBody.Part?,
    ): ApiResult<UploadResponse> {
        return try {
            networkCall(api.uploadWithResolutionPromotional(file))
        } catch (e: Exception) {
            ApiResult.Error("Syntax Error")
        }
    }

    suspend fun getAllInventoryProduct(map: HashMap<String, Any>): ApiResult<GetAllProductResponse> {
        return try {
            networkCall(api.getAllInventoryProduct(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getInventoryProductDetail(map: HashMap<String, Any>): ApiResult<GetProductDetailResponse> {
        return try {
            networkCall(api.getInventoryProductDetail(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateInventoryProductStatus(map: HashMap<String, Any>): ApiResult<GetProductDetailResponse> {
        return try {
            networkCall(api.updateInventoryProductStatus(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateInventoryProductStock(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.updateInventoryProductStock(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateAffiliateCommission(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.updateAffiliateCommission(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateStoreInfo(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.updateStoreInfo(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun changeNumber(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.changeNumber(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun otpVerification(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.otpVerification(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateBankDetail(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.updateBankDetail(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun addBanner(request: AddBannerRequest): ApiResult<AddBannerResponse> {
        return try {
            networkCall(api.addBanner(request))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getBanner(map: HashMap<String, Any>): ApiResult<GetAllBannerResponse> {
        return try {
            networkCall(api.getBanner(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getBannerPrice(map: HashMap<String, Any>): ApiResult<GetBannerPriceResponse> {
        return try {
            networkCall(api.getBannerPrice(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun bannerPayment(map: HashMap<String, Any>): ApiResult<AddBannerResponse> {
        return try {
            networkCall(api.bannerPayment(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getOrder(map: HashMap<String, Any>): ApiResult<GetAllOrderResponse> {
        return try {
            networkCall(api.getOrder(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun markAllOrderNotification(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.markAllOrderNotification(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getOrderNotification(): ApiResult<GetOrderNotificationCountResponse> {
        return try {
            networkCall(api.getOrderNotification())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getOrderDetail(map: HashMap<String, Any>): ApiResult<GetSingleOrderResponse> {
        return try {
            networkCall(api.getOrderDetail(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateOrderStatus(map: HashMap<String, Any>): ApiResult<GetSingleOrderResponse> {
        return try {
            networkCall(api.updateOrderStatus(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateOrderExchangeReturnStatus(map: HashMap<String, Any>): ApiResult<GetSingleOrderResponse> {
        return try {
            networkCall(api.updateOrderExchangeReturnStatus(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getAllChatList(map: HashMap<String, Any>): ApiResult<GetChatListResponse> {
        return try {
            networkCall(api.getAllChatList(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getChatDetail(map: HashMap<String, Any>): ApiResult<ChatDetailResponse> {
        return try {
            networkCall(api.getChatDetail(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun markAllRead(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.markAllRead(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun sendNotification(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.sendNotification(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun modifySingleChat(map: HashMap<String, Any>): ApiResult<ModifySingleChatResponse> {
        return try {
            networkCall(api.modifySingleChat(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getDeal(map: HashMap<String, Any>): ApiResult<GetAllDealResponse> {
        return try {
            networkCall(api.getDeal(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getDealDetail(map: HashMap<String, Any>): ApiResult<GetDealDetailResponse> {
        return try {
            networkCall(api.getDealDetail(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }


    suspend fun addProductInDeal(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.addProductInDeal(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun removeProductInDeal(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.removeProductInDeal(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun showProductInDealCategorySubcategory(map: HashMap<String, Any>): ApiResult<GetDealAllProductResponse> {
        return try {
            networkCall(api.showProductInDealCategorySubcategory(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getTimeSlot(date: String): ApiResult<GetTimeSlotResponse> {
        return try {
            networkCall(api.getTimeSlot(date))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getLiveRequest(map: HashMap<String, Any>): ApiResult<GetLiveRequestResponse> {
        return try {
            networkCall(api.getLiveRequest(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun cancelLiveRequest(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.cancelLiveRequest(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun sendLiveRequest(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.sendLiveRequest(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun changePassword(map: HashMap<String, String>): ApiResult<Unit> {
        return try {
            networkCall(api.updatePassword(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getNotificationList(map: HashMap<String, Any>): ApiResult<GetUserNotificationResponse> {
        return try {
            networkCall(api.getNotificationList(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getNotificationCount(): ApiResult<GetNotificationCountResponse> {
        return try {
            networkCall(api.getNotificationCount())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun readNotification(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.readNotification(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun readAllNotification(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.readAllNotification(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun renewSubscriptionPlan(map: HashMap<String, Any>): ApiResult<RenewSubscriptionPlanResponse> {
        return try {
            networkCall(api.renewSubscriptionPlan(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getMyPlan(map: HashMap<String, Any>): ApiResult<GetMyPlanResponse> {
        return try {
            networkCall(api.getMyPlan(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getStory(map: HashMap<String, Any>): ApiResult<GetStoryResponse> {
        return try {
            networkCall(api.getStory(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getAdminStory(map: HashMap<String, Any>): ApiResult<GetStoryResponse> {
        return try {
            networkCall(api.getAdminStory(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getHighlights(map: HashMap<String, Any>): ApiResult<GetStoryResponse> {
        return try {
            networkCall(api.getHighlights(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getReel(map: HashMap<String, Any>): ApiResult<GetStoryResponse> {
        return try {
            networkCall(api.getReel(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun addStory(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.addStory(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun addReel(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.addReel(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun addHighlights(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.addHighlights(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun deleteStory(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.deleteStory(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun deleteReel(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.deleteReel(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun deleteHighlights(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.deleteHighlights(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateReel(map: HashMap<String, Any>): ApiResult<UpdateReelResponse> {
        return try {
            networkCall(api.updateReel(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateReelStatus(map: HashMap<String, Any>): ApiResult<UpdateReelResponse> {
        return try {
            networkCall(api.updateReelStatus(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun uploadVideoImage(
        file: MultipartBody.Part?,
    ): ApiResult<UploadResponse> {
        return try {
            networkCall(api.uploadVideoImage(file))
        }
//        catch (e: Exception) {
//            ApiResult.Error(e.message.toString())
//        }
        catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun uploadWithResolution(
        file: MultipartBody.Part?, type: String
    ): ApiResult<UploadResponse> {
        return try {
            networkCall(api.uploadWithResolution(file, type.toPlainRequestBody()))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getLiveRoomRecords(): ApiResult<GetLiveStreamingRoomResponse> {
        return try {
            networkCall(api.getLiveRoomRecords())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getOtherLiveStreaming(): ApiResult<GetOtherLiveStreamingRoomResponse> {
        return try {
            networkCall(api.getOtherLiveStreaming())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getLiveEventUserCount(): ApiResult<GetOtherLiveStreamingRoomResponse> {
        return try {
            networkCall(api.getLiveEventUserCount())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getAgoraToken(map: HashMap<String, Any>): ApiResult<GetAgoraTokenResponse> {
        return try {
            networkCall(api.getAgoraToken(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getRaisedTicket(map: HashMap<String, Any>): ApiResult<GetAllRaisedTicketResponse> {
        return try {
            networkCall(api.getRaisedTicket(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun raiseTicket(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.raiseTicket(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun notificationOnOff(map: HashMap<String, Any>): ApiResult<UpdateNotificationResponse> {
        return try {
            networkCall(api.notificationOnOff(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun appNotificationOnOff(map: HashMap<String, Any>): ApiResult<UpdateNotificationResponse> {
        return try {
            networkCall(api.appNotificationOnOff(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun contactUs(map: HashMap<String, Any>): ApiResult<GetContactResponse> {
        return try {
            networkCall(api.contactUs(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun faq(map: HashMap<String, Any>): ApiResult<GetFaqResponse> {
        return try {
            networkCall(api.faq(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun aboutUs(map: HashMap<String, Any>): ApiResult<GetCmsResponse> {
        return try {
            networkCall(api.aboutUs(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }


    suspend fun privacyPolicy(map: HashMap<String, Any>): ApiResult<GetCmsResponse> {
        return try {
            networkCall(api.privacyPolicy(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun termsAndCondition(map: HashMap<String, Any>): ApiResult<GetCmsResponse> {
        return try {
            networkCall(api.termsAndCondition(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun cancellationPolicy(map: HashMap<String, Any>): ApiResult<GetCmsResponse> {
        return try {
            networkCall(api.cancellationPolicy(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun exchangePolicy(map: HashMap<String, Any>): ApiResult<GetCmsResponse> {
        return try {
            networkCall(api.exchangePolicy(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun returnPolicy(map: HashMap<String, Any>): ApiResult<GetCmsResponse> {
        return try {
            networkCall(api.returnPolicy(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }


    suspend fun getTicketType(): ApiResult<GetTicketTypeResponse> {
        return try {
            networkCall(api.getTicketType())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getAllStaff(map: HashMap<String, Any>): ApiResult<GetAllStaffResponse> {
        return try {
            networkCall(api.getAllStaff(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getSellerAllMenu(): ApiResult<GetSellerAllMenuResponse> {
        return try {
            networkCall(api.getSellerAllMenu())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getStaffRole(): ApiResult<GetStaffCategoryResponse> {
        return try {
            networkCall(api.getStaffRole())
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun addNewStaff(request: AddNewStaffRequest): ApiResult<CommonResponse> {
        return try {
            networkCall(api.addNewStaff(request))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateStaff(request: AddNewStaffRequest): ApiResult<CommonResponse> {
        return try {
            networkCall(api.updateStaff(request))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun updateStaffStatus(map: HashMap<String, Any>): ApiResult<UpdateStaffStatusResponse> {
        return try {
            networkCall(api.updateStaffStatus(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun deleteStaff(map: HashMap<String, Any>): ApiResult<CommonResponse> {
        return try {
            networkCall(api.deleteStaff(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getStaffDetail(map: HashMap<String, Any>): ApiResult<ViewStaffDetailResponse> {
        return try {
            networkCall(api.getStaffDetail(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun getRefundOrder(map: HashMap<String, Any>): ApiResult<GetRefundOrderResponse> {
        return try {
            networkCall(api.getRefundOrder(map))
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

}