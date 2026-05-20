package com.desynatdealer.controller

import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.model.create_account.CreateAccountRes
import com.desynatdealer.model.login.req.LoginResponse
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.model.upload.UploadResponse
import com.desynatdealer.ui.auth.response.GetSellerSummaryResponse
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
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.translation.TranslateRequest
import com.desynatdealer.utils.translation.TranslateResponse
import com.desynatdealer.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatdealer.ui.home.cms.response.GetContactResponse
import com.desynatdealer.ui.home.cms.response.GetTicketTypeResponse
import com.desynatdealer.ui.home.cms.response.UpdateNotificationResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetLiveUserCountResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetOtherLiveStreamingRoomResponse
import com.desynatdealer.ui.home.orderManagement.response.GetOrderNotificationCountResponse
import com.desynatdealer.ui.home.refundManagement.response.GetRefundOrderResponse
import com.desynatdealer.ui.home.response.GetDashboardDataResponse
import com.desynatdealer.ui.home.staffManagement.response.AddNewStaffRequest
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.ui.home.staffManagement.response.GetSellerAllMenuResponse
import com.desynatdealer.ui.home.staffManagement.response.GetStaffCategoryResponse
import com.desynatdealer.ui.home.staffManagement.response.UpdateStaffStatusResponse
import com.desynatdealer.ui.home.staffManagement.response.ViewStaffDetailResponse
import com.desynatdriver.ui.home.cms.response.GetCmsResponse
import com.desynatdriver.ui.home.cms.response.GetFaqResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface CommonApiService {

    @POST("api/seller/login")
    suspend fun login(@Body map: HashMap<String,Any>): Response<LoginResponse>


    @Multipart
  //  @POST("api/upload")
    @POST(Constants.UPLOAD_VIDEO_IMAGE)
    suspend fun upload(
        @Part file: MultipartBody.Part?
    ): Response<UploadResponse>



  @Multipart
    @POST(Constants.uploadWithResolutionPromotional)
    suspend fun uploadWithResolutionPromotional(
        @Part file: MultipartBody.Part?
    ): Response<UploadResponse>


    @POST("api/seller/create_account")
    suspend fun createAccount(@Body req: HashMap<String, String>): Response<CreateAccountRes>


    @POST("api/seller/verify_otp")
    suspend fun verifyOtp(@Body req: HashMap<String, String>): Response<Unit>


    @POST("api/seller/store_information")
    suspend fun storeInformation(@Body req: HashMap<String, String>): Response<Unit>



    @POST(Constants.GET_SELLER_REVIEW)
    suspend fun getSellerSummary(@Body req: HashMap<String, String>): Response<GetSellerSummaryResponse>


    @POST("api/seller/store_document_and_bank_details")
    suspend fun storeDocumentAndBankDetails(@Body loginReq: StoreDocumentAndBankDetailsReq): Response<Unit>

    @POST("api/seller/seller_store_submit_request")
    suspend fun sellerStoreSubmitRequest(@Body map: HashMap<String, String>): Response<Unit>


    @POST("api/seller/forgot_password")
    suspend fun forgotPassword(@Body map: HashMap<String, String>): Response<CreateAccountRes>


    @POST("api/seller/create_password")
    suspend fun changePassword(@Body map: HashMap<String, String>): Response<Unit>

    @POST("api/seller/resend_otp")
    suspend fun resendOtp(@Body map: HashMap<String, String>): Response<Unit>

    @GET(Constants.GET_COUNTRY)
    suspend fun getCountry(): Response<GetCountryResponse>

    @GET(Constants.LOGOUT)
    suspend fun logout(@Header("Authorization") auth: String): Response<CommonResponse>

    @GET(Constants.GET_SUBSCRIPTION_PLAN)
    suspend fun getSubscriptionPlan(): Response<GetAllPlanResponse>

    @POST(Constants.GET_PROFILE)
    suspend fun getSellerInfo(@Header("Authorization") auth: String
    ): Response<GetSellerInfo>

    @POST(Constants.CREATE_SUBSCRIPTION_PLAN)
    suspend fun createSubscriptionPlan(
        @Body map: HashMap<String, Any>
    ): Response<CreateSubscriptionPlanResponse>

    @POST(Constants.BUY_SUBSCRIPTION_PLAN)
    suspend fun buySubscriptionPlan(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_CATEGORY)
    suspend fun getCategory(): Response<GetCategoryResponse>

    @GET(Constants.GET_SUB_CATEGORY)
    suspend fun getSubCategory(
        @Query ("category_id") categoryId:String
    ): Response<GetSubCategoryResponse>

    @GET(Constants.GET_BRAND)
    suspend fun getBrand(): Response<GetSellerBrandResponse>

    @GET(Constants.GET_COLOR)
    suspend fun getColor(): Response<GetColorResponse>

    @GET(Constants.GET_SIZE)
    suspend fun getSize(): Response<GetSizeResponse>

    @GET(Constants.STORE_ON_OFF)
    suspend fun storeOnOff(): Response<CommonResponse>

    @POST(Constants.GET_ALL_PRODUCT)
    suspend fun getAllProduct(@Body map: HashMap<String, Any>): Response<GetAllProductResponse>

    @POST(Constants.ADD_PRODUCT)
    suspend fun addProduct(@Body request : AddProductRequest): Response<CommonResponse>

    @POST(Constants.UPDATE_PRODUCT)
    suspend fun editProduct(@Body request : AddProductRequest): Response<CommonResponse>

    @POST(Constants.PRODUCT_DETAIL)
    suspend fun getProductDetail(@Body map: HashMap<String, Any>): Response<GetProductDetailResponse>

    @PUT(Constants.PRODUCT_ACTIVE_INACTIVE)
    suspend fun updateProductStatus(@Body map: HashMap<String, Any>): Response<GetProductDetailResponse>

 //   @DELETE(Constants.DELETE_PRODUCT)
    @HTTP(method = "DELETE", path = Constants.DELETE_PRODUCT, hasBody = true)
    suspend fun deleteProduct(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.ALL_INVENTORY_PRODUCT)
    suspend fun getAllInventoryProduct(@Body map: HashMap<String, Any>): Response<GetAllProductResponse>

    @POST(Constants.SINGLE_INVENTORY_DETAIL)
    suspend fun getInventoryProductDetail(@Body map: HashMap<String, Any>): Response<GetProductDetailResponse>

    @PUT(Constants.PRODUCT_ACTIVE_INACTIVE)
    suspend fun updateInventoryProductStatus(@Body map: HashMap<String, Any>): Response<GetProductDetailResponse>

    @PUT(Constants.UPDATE_INVENTORY_STOCK)
    suspend fun updateInventoryProductStock(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @PUT(Constants.UPDATE_AFFILIATE_COMMISSION)
    suspend fun updateAffiliateCommission(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.UPDATE_STORE_BANK_DETAIL)
    suspend fun updateBankDetail(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.UPDATE_STORE_INFO)
    suspend fun updateStoreInfo(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.CHANGE_NUMBER_EMAIL)
    suspend fun changeNumber(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.OTP_VERIFICATION)
    suspend fun otpVerification(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.ADD_BANNER)
    suspend fun addBanner(@Body request: AddBannerRequest): Response<AddBannerResponse>

    @POST(Constants.GET_ALL_BANNER)
    suspend fun getBanner(@Body map: HashMap<String, Any>): Response<GetAllBannerResponse>

    @POST(Constants.GET_BANNER_PRICE)
    suspend fun getBannerPrice(@Body map: HashMap<String, Any>): Response<GetBannerPriceResponse>

    @POST(Constants.BANNER_PAYMENT)
    suspend fun bannerPayment(@Body map: HashMap<String, Any>): Response<AddBannerResponse>

    @POST(Constants.GET_ALL_ORDER)
    suspend fun getOrder(@Body map: HashMap<String, Any>): Response<GetAllOrderResponse>

    @GET(Constants.ORDER_NOTIFICATION)
    suspend fun getOrderNotification(): Response<GetOrderNotificationCountResponse>

    @POST(Constants.MARK_ALL_ORDER_NOTIFICATION)
    suspend fun markAllOrderNotification(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.GET_SINGLE_ORDER_DETAIL)
    suspend fun getOrderDetail(@Body map: HashMap<String, Any>): Response<GetSingleOrderResponse>

    @POST(Constants.UPDATE_SINGLE_ORDER_STATUS)
    suspend fun updateOrderStatus(@Body map: HashMap<String, Any>): Response<GetSingleOrderResponse>

    @POST(Constants.UPDATE_RETURN_EXCHANGE_ORDER_STATUS)
    suspend fun updateOrderExchangeReturnStatus(@Body map: HashMap<String, Any>): Response<GetSingleOrderResponse>

    @POST(Constants.ALL_CHAT)
    suspend fun getAllChatList(@Body map: HashMap<String, Any>): Response<GetChatListResponse>

    @POST(Constants.SINGLE_CHAT)
    suspend fun getChatDetail(@Body map: HashMap<String, Any>): Response<ChatDetailResponse>

    @POST(Constants.MARK_ALL_READ_MSG)
    suspend fun markAllRead(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.SEND_PUSH_NOTIFICATION)
    suspend fun sendNotification(
        @Body map: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.MODIFY_SINGLE_CHAT)
    suspend fun modifySingleChat(@Body map: HashMap<String, Any>): Response<ModifySingleChatResponse>

    @POST(Constants.GET_All_DEAL)
    suspend fun getDeal(@Body map: HashMap<String, Any>): Response<GetAllDealResponse>

    @POST(Constants.GET_DEAL_DETAIL)
    suspend fun getDealDetail(@Body map: HashMap<String, Any>): Response<GetDealDetailResponse>

    @POST(Constants.ADD_PRODUCT_IN_DEAL)
    suspend fun addProductInDeal(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.REMOVE_PRODUCT_IN_DEAL)
    suspend fun removeProductInDeal(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.SHOW_ALL_PRODUCT_DEAL)
    suspend fun showProductInDealCategorySubcategory(@Body map: HashMap<String, Any>): Response<GetDealAllProductResponse>

    @POST("https://translation.googleapis.com/language/translate/v2")
    suspend fun googleTranslator(
        @Query("key") apiKey: String,
        @Body request: TranslateRequest
    ): Response<TranslateResponse>

    @GET(Constants.GET_TIME_SLOT)
    suspend fun getTimeSlot( @Query("date") date: String): Response<GetTimeSlotResponse>

    @GET(Constants.GET_LIVE_REQUEST)
    suspend fun getLiveRequest(@QueryMap map: HashMap<String, Any>): Response<GetLiveRequestResponse>

    @POST(Constants.CANCEL_LIVE_REQUEST)
    suspend fun cancelLiveRequest(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.SEND_LIVE_REQUEST)
    suspend fun sendLiveRequest(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @GET(Constants.GET_NOTIFICATION_LIST)
    suspend fun getNotificationList(@QueryMap map: HashMap<String, Any>): Response<GetUserNotificationResponse>

 @GET(Constants.NOTIFICATION_COUNT)
    suspend fun getNotificationCount(): Response<GetNotificationCountResponse>

    @POST(Constants.READ_NOTIFICATION)
    suspend fun readNotification(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.READ_ALL_NOTIFICATION)
    suspend fun readAllNotification(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.RENEW_SUBSCRIPTION_PLAN)
    suspend fun renewSubscriptionPlan(@Body map: HashMap<String, Any>): Response<RenewSubscriptionPlanResponse>

    @GET(Constants.GET_MY_SUBSCRIPTION_PLAN)
    suspend fun getMyPlan(@QueryMap map: HashMap<String, Any>): Response<GetMyPlanResponse>

    @POST(Constants.ADD_STORY)
    suspend fun addStory(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.ADD_REEL)
    suspend fun addReel(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.ADD_HIGHLIGHTS)
    suspend fun addHighlights(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.DELETE_STORY)
    suspend fun deleteStory(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.DELETE_REEL)
    suspend fun deleteReel(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.DELETE_HIGHLIGHTS)
    suspend fun deleteHighlights(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @GET(Constants.GET_STORY)
    suspend fun getStory(@QueryMap map: HashMap<String, Any>): Response<GetStoryResponse>

    @GET(Constants.GET_ADMIN_STORY)
    suspend fun getAdminStory(@QueryMap map: HashMap<String, Any>): Response<GetStoryResponse>

    @GET(Constants.GET_REEL)
    suspend fun getReel(@QueryMap map: HashMap<String, Any>): Response<GetStoryResponse>

    @GET(Constants.GET_HIGHLIGHTS)
    suspend fun getHighlights(@QueryMap map: HashMap<String, Any>): Response<GetStoryResponse>

    @POST(Constants.UPDATE_REEL)
    suspend fun updateReel(@Body map: HashMap<String, Any>): Response<UpdateReelResponse>

    @POST(Constants.UPDATE_REEL_STATUS)
    suspend fun updateReelStatus(@Body map: HashMap<String, Any>): Response<UpdateReelResponse>

    @Multipart
    @POST(Constants.UPLOAD_VIDEO_IMAGE)
    suspend fun uploadVideoImage(
        @Part file: MultipartBody.Part? = null
    ): Response<UploadResponse>


    @Multipart
    @POST(Constants.UPLOAD_WITH_RESOLUTION)
    suspend fun uploadWithResolution(
        @Part file: MultipartBody.Part? = null,
        @Part("type") type: RequestBody? = null
    ): Response<UploadResponse>

    @GET(Constants.GET_LIVE_ROOM_RECORD)
    suspend fun getLiveRoomRecords(): Response<GetLiveStreamingRoomResponse>


    @GET(Constants.GET_OTHER_LIVE_ROOM_RECORD)
    suspend fun getOtherLiveStreaming(): Response<GetOtherLiveStreamingRoomResponse>


    @GET(Constants.GET_LIVE_EVENT_COUNT)
    suspend fun getLiveEventUserCount(): Response<GetOtherLiveStreamingRoomResponse>


    @POST(Constants.GET_AGORA_TOKEN)
    suspend fun getAgoraToken(
        @Body  map: HashMap<String, Any>
    ): Response<GetAgoraTokenResponse>

    @GET(Constants.GET_RAISED_TICKET)
    suspend fun getRaisedTicket(@QueryMap map: HashMap<String, Any>): Response<GetAllRaisedTicketResponse>

    @POST(Constants.RAISE_TICKET)
    suspend fun raiseTicket(
        @Body  map: HashMap<String, Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_TICKET_TYPE)
    suspend fun getTicketType(): Response<GetTicketTypeResponse>

    @GET("channel/user/{appid}/{channelName}")
    suspend fun getLiveUserCount(
        @Header("Authorization") auth: String,
        @Path("appid") appid: String,
        @Path("channelName") channelName: String
    )
    : Response<GetLiveUserCountResponse>

    @POST(Constants.NOTIFICATION_ON_OFF)
    suspend fun notificationOnOff(@QueryMap map: HashMap<String, Any>): Response<UpdateNotificationResponse>

    @POST(Constants.APP_NOTIFICATION_ON_OFF)
    suspend fun appNotificationOnOff(@QueryMap map: HashMap<String, Any>): Response<UpdateNotificationResponse>

    @GET(Constants.CONTACT_US)
    suspend fun contactUs(@QueryMap map: HashMap<String, Any>): Response<GetContactResponse>

    @GET(Constants.FAQ)
    suspend fun faq(@QueryMap map: HashMap<String, Any>): Response<GetFaqResponse>

    @GET(Constants.ABOUT_US)
    suspend fun aboutUs(@QueryMap map: HashMap<String, Any>): Response<GetCmsResponse>

    @GET(Constants.TERMS_CONDITION)
    suspend fun termsAndCondition(@QueryMap map: HashMap<String, Any>): Response<GetCmsResponse>

    @GET(Constants.PRIVACY_POLICY)
    suspend fun privacyPolicy(@QueryMap map: HashMap<String, Any>): Response<GetCmsResponse>

    @GET(Constants.RETURN_POLICY)
    suspend fun returnPolicy(@QueryMap map: HashMap<String, Any>): Response<GetCmsResponse>

    @GET(Constants.CANCELLATION_POLICY)
    suspend fun cancellationPolicy(@QueryMap map: HashMap<String, Any>): Response<GetCmsResponse>

    @GET(Constants.EXCHANGE_POLICY)
    suspend fun exchangePolicy(@QueryMap map: HashMap<String, Any>): Response<GetCmsResponse>

    @GET(Constants.SELLER_ALL_MENU)
    suspend fun getSellerAllMenu(): Response<GetSellerAllMenuResponse>

    @GET(Constants.STAFF_CATEGORY_TYPE)
    suspend fun getStaffRole(): Response<GetStaffCategoryResponse>

    @POST(Constants.GET_ALL_STAFF_LIST)
    suspend fun getAllStaff(@Body map: HashMap<String, Any>): Response<GetAllStaffResponse>

    @POST(Constants.ADD_NEW_STAFF)
    suspend fun addNewStaff(@Body request: AddNewStaffRequest): Response<CommonResponse>

    @POST(Constants.UPDATE_STAFF)
    suspend fun updateStaff(@Body request: AddNewStaffRequest): Response<CommonResponse>

    @POST(Constants.UPDATE_STAFF_STATUS)
    suspend fun updateStaffStatus(@Body map: HashMap<String, Any>): Response<UpdateStaffStatusResponse>

    @POST(Constants.DELETE_STAFF)
    suspend fun deleteStaff(@Body map: HashMap<String, Any>): Response<CommonResponse>

    @POST(Constants.VIEW_SINGLE_STAFF_DETAIL)
    suspend fun getStaffDetail(@Body map: HashMap<String, Any>): Response<ViewStaffDetailResponse>

    @GET(Constants.GET_REFUND_ORDER)
    suspend fun getRefundOrder(@QueryMap map: HashMap<String, Any>): Response<GetRefundOrderResponse>

    @POST(Constants.UPDATE_PASSWORD)
    suspend fun updatePassword(@Body map: HashMap<String, String>): Response<CommonResponse>

    @POST(Constants.DELETE_ACCOUNT)
    suspend fun deleteAccount(): Response<CommonResponse>

    @GET(Constants.GET_DASHBOARD_DATA)
    suspend fun getDashboardData(@QueryMap map: HashMap<String, String>): Response<GetDashboardDataResponse>


}