package com.desynatdealer.ui.home.orderManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.ProductOrderAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentOrderDetailBinding
import com.desynatdealer.databinding.UpdateOrderStatusDialogBinding
import com.desynatdealer.ui.home.orderManagement.response.GetAllOrderResponse
import com.desynatdealer.ui.home.orderManagement.response.GetSingleOrderResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.setImage
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {
    private var binding : FragmentOrderDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var productOrderAdapter: ProductOrderAdapter?=null
    private var productOrderList = ArrayList<GetSingleOrderResponse.Data>()
    private var orderData :GetAllOrderResponse.Data.AllOrder?=null
    private var orderDetailData :GetSingleOrderResponse.Data?=null
    private var orderStatus :String = "1" //1 all 2 ongoing 2.1 shipped 2.2 on the way 3 delivered 4 cancelled 5 return 6 confirmed 7-picked 8-not delivered
    var selectedStatus = ""

    var canAction = true
    var canView = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {

            if (SharedPreference.get(requireActivity()).userType == "1") {
                canAction = CommonMethod.getPermission(requireActivity(),"seller.order_management")?.canAction == 1
                canView = CommonMethod.getPermission(requireActivity(),"seller.order_management")?.canView == 1
            }

            productOrderAdapter = ProductOrderAdapter(requireActivity(), productOrderList)
            rvProduct.adapter = productOrderAdapter
        }

        val itemJson = arguments?.getString("orderData")
        if (!itemJson.isNullOrEmpty()) {
            orderData = Gson().fromJson(itemJson, GetAllOrderResponse.Data.AllOrder::class.java)
        }

        getOrderDetail()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            etStatus.setOnClickListener {
                updateDeliveryStatusDialog()
            }

            tvAccept.setOnClickListener {
                selectedStatus = "1"
                updateOrderExchangeReturnStatus(selectedStatus)
            }

            tvReject.setOnClickListener {

            }

            btnUpdateStatus.setOnClickListener {
                if (selectedStatus!=""){
                    updateOrderStatus(selectedStatus)
                }
                else{
                    showToast(getString(R.string.please_select_status))
                }

            }
        }
    }

    override fun observe() {
        viewModel.getOrderDetailResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    orderDetailData = result.data?.data
                    setData()

                    viewModel.getOrderDetailResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getOrderDetailResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.updateOrderStatusResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    getOrderDetail()
                    viewModel.updateOrderStatusResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateOrderStatusResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.updateOrderExchangeReturnStatusResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                   getOrderDetail()
                    viewModel.updateOrderExchangeReturnStatusResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateOrderExchangeReturnStatusResponse.value = null
                }
                else ->{

                }
            }
        }

    }

    private fun setData(){
        binding?.apply {

            productOrderList.clear()
            orderDetailData?.let { productOrderList.add(it) }
            productOrderAdapter?.notifyDataSetChanged()
            if (orderDetailData?.paymentStatus == "2" || orderDetailData?.paymentStatus == "3") {
                tvStatus.text = getString(R.string.cancelled)
                tvStatus.setTextColor(Color.parseColor("#E70000"))
                tvStatus.text = getString(R.string.cancelled)
                tvStatus.setTextColor(Color.parseColor("#E70000"))
                etStatus.isVisible = false
                btnUpdateStatus.isVisible = false
                llComplete.isVisible = false
                llReturnDetail.isVisible = true
                tvExchangeSize.isVisible = false
                tvExchangeSizeValue.isVisible = false
                tvUploadedImage.isVisible = false
                relUploadImage.isVisible = false
                tvReturnDetails.text = getString(R.string.cancellation_details)
                tvReturnOn.text = getString(R.string.cancelled_on)
                tvReturnRequestId.text = getString(R.string.cancelled_request_id)
                tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a")

                if (orderDetailData?.cancelReason!="null"&&orderDetailData?.cancelReason!=null&&orderDetailData?.cancelReason!=""){

                    tvReturnReason.isVisible = true
                    tvReturnReasonValue.isVisible = true
                }
                else{
                    tvReturnReason.isVisible = false
                    tvReturnReasonValue.isVisible = false
                }
                tvReturnReasonValue.text = orderDetailData?.cancelReason
            }
            else {
                when (orderDetailData?.deliveryStatus) {
                    "1" -> {

                        if (orderDetailData?.rEApprove=="1"&&orderDetailData?.returnType=="1"&&orderDetailData?.assignStatus=="0"){
                            tvStatus.text = getString(R.string.return_req_accepted)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                            etStatus.isVisible = true

                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            clReturnExchange.isVisible = false
                            if (orderDetailData?.userRatingReviewProvide != null) {
                                llComplete.isVisible = true
                                rbProductRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.productRating
                                        ?: 0f
                                rbStoreRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.storeRating
                                        ?: 0f
                                if (orderDetailData?.userRatingReviewProvide?.productReview!=null&&orderDetailData?.userRatingReviewProvide?.productReview!="null"&&orderDetailData?.userRatingReviewProvide?.productReview!=""){
                                    tvProductReview.isVisible = true
                                    tvReview.isVisible = true
                                }
                                else{
                                    tvProductReview.isVisible = false
                                    tvReview.isVisible = false
                                }

                                if (orderDetailData?.userRatingReviewProvide?.storeReview!=null&&orderDetailData?.userRatingReviewProvide?.storeReview!="null"&&orderDetailData?.userRatingReviewProvide?.storeReview!=""){
                                    tvSellerReview.text =
                                        orderDetailData?.userRatingReviewProvide?.storeReview
                                    tvSellerReview.isVisible = true
                                    tvStoreReview.isVisible = true
                                }
                                else{
                                    tvSellerReview.isVisible = false
                                    tvStoreReview.isVisible = false
                                }


                            } else {
                                llComplete.isVisible = false
                            }
                        }
                       else if (orderDetailData?.rEApprove=="1"&&orderDetailData?.returnType=="1"&&orderDetailData?.assignStatus=="1"){
                            tvStatus.text = getString(R.string.out_for_picked)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                            etStatus.isVisible = true

                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            clReturnExchange.isVisible = false
                            if (orderDetailData?.userRatingReviewProvide != null) {
                                llComplete.isVisible = true
                                rbProductRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.productRating
                                        ?: 0f
                                rbStoreRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.storeRating
                                        ?: 0f
                                if (orderDetailData?.userRatingReviewProvide?.productReview!=null&&orderDetailData?.userRatingReviewProvide?.productReview!="null"&&orderDetailData?.userRatingReviewProvide?.productReview!=""){
                                    tvProductReview.text =
                                        orderDetailData?.userRatingReviewProvide?.productReview
                                    tvProductReview.isVisible = true
                                    tvReview.isVisible = true
                                }
                                else{
                                    tvProductReview.isVisible = false
                                    tvReview.isVisible = false
                                }

                                if (orderDetailData?.userRatingReviewProvide?.storeReview!=null&&orderDetailData?.userRatingReviewProvide?.storeReview!="null"&&orderDetailData?.userRatingReviewProvide?.storeReview!=""){
                                    tvSellerReview.text =
                                        orderDetailData?.userRatingReviewProvide?.storeReview
                                    tvSellerReview.isVisible = true
                                    tvStoreReview.isVisible = true
                                }
                                else{
                                    tvSellerReview.isVisible = false
                                    tvStoreReview.isVisible = false
                                }


                            } else {
                                llComplete.isVisible = false
                            }
                        }

                        else{
                            tvStatus.text = getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                            etStatus.isVisible = canAction
                            etStatus.setText("")
                            btnUpdateStatus.isVisible = canAction
                            llComplete.isVisible = false
                        }

                    }

                    "2" -> {
                        tvStatus.text = getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.setText("")
                        etStatus.isVisible = canAction
                        btnUpdateStatus.isVisible = canAction
                        llComplete.isVisible = false
                    }
                    "2.1" -> {
                        tvStatus.text = getString(R.string.shipped)
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                    }
                    "2.2" -> {
                        tvStatus.text = getString(R.string.out_for_delivery)
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                    }

                    "3" -> {
                        if (orderDetailData?.r_e_type=="0"&&orderDetailData?.p_orderdetails_id=="0"&&orderDetailData?.rEApprove=="0"&&orderDetailData?.returnType=="0"){
                            tvStatus.text = getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#047BD5"))
                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            clReturnExchange.isVisible = false

                            if (orderDetailData?.userRatingReviewProvide != null) {
                                llComplete.isVisible = true
                                rbProductRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.productRating
                                        ?: 0f
                                rbStoreRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.storeRating
                                        ?: 0f
                                if (orderDetailData?.userRatingReviewProvide?.productReview!=null&&orderDetailData?.userRatingReviewProvide?.productReview!="null"&&orderDetailData?.userRatingReviewProvide?.productReview!=""){
                                    tvProductReview.text = orderDetailData?.userRatingReviewProvide?.productReview
                                    tvProductReview.isVisible = true
                                    tvReview.isVisible = true
                                }
                                else{
                                    tvProductReview.isVisible = false
                                    tvReview.isVisible = false
                                }

                                if (orderDetailData?.userRatingReviewProvide?.storeReview!=null&&orderDetailData?.userRatingReviewProvide?.storeReview!="null"&&orderDetailData?.userRatingReviewProvide?.storeReview!=""){
                                    tvSellerReview.text =
                                        orderDetailData?.userRatingReviewProvide?.storeReview
                                    tvSellerReview.isVisible = true
                                    tvStoreReview.isVisible = true
                                }
                                else{
                                    tvSellerReview.isVisible = false
                                    tvStoreReview.isVisible = false
                                }
                            } else {
                                llComplete.isVisible = false
                            }
                        }
                        else if (orderDetailData?.r_e_type=="0"&&orderDetailData?.p_orderdetails_id=="0"&&orderDetailData?.rEApprove=="1"&&orderDetailData?.returnType=="2"){

                            if (orderDetailData?.exchangeOrderDetails?.deliveryStatus=="7"){
                                tvStatus.text = getString(R.string.out_for_drop)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                           else if (orderDetailData?.exchangeOrderDetails?.deliveryStatus=="3"){
                                tvStatus.text = getString(R.string.exchange_complete)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                            else{
                                tvStatus.text = getString(R.string.exchange_req_accepted)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }

                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            llComplete.isVisible = false
                            clReturnExchange.isVisible = false
                            llReturnDetail.isVisible = true
                            tvExchangeSize.isVisible = true
                            tvExchangeSizeValue.isVisible = true
                            tvUploadedImage.isVisible = false
                            relUploadImage.isVisible = false
                            tvReturnDetails.text = getString(R.string.exchange_details)
                            tvReturnOn.text = getString(R.string.exchange_on)
                            tvReturnRequestId.text = getString(R.string.exchange_request_id)
                            tvReturnRequestIdValue.text = "#" + orderDetailData?.id
                            tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(
                                orderDetailData?.updatedAt.toString(),
                                outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a"
                            )
                            tvExchangeSizeValue.text = orderDetailData?.exchangeNewSize
                            tvReturnReasonValue.text = orderDetailData?.exchangeReason

                            if (orderDetailData?.userRatingReviewProvide != null) {
                                llComplete.isVisible = true
                                rbProductRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.productRating
                                        ?: 0f
                                rbStoreRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.storeRating
                                        ?: 0f
                                if (orderDetailData?.userRatingReviewProvide?.productReview!=null&&orderDetailData?.userRatingReviewProvide?.productReview!="null"&&orderDetailData?.userRatingReviewProvide?.productReview!=""){
                                    tvProductReview.text =
                                        orderDetailData?.userRatingReviewProvide?.productReview
                                    tvProductReview.isVisible = true
                                    tvReview.isVisible = true
                                }
                                else{
                                    tvProductReview.isVisible = false
                                    tvReview.isVisible = false
                                }

                                if (orderDetailData?.userRatingReviewProvide?.storeReview!=null&&orderDetailData?.userRatingReviewProvide?.storeReview!="null"&&orderDetailData?.userRatingReviewProvide?.storeReview!=""){
                                    tvSellerReview.text =
                                        orderDetailData?.userRatingReviewProvide?.storeReview
                                    tvSellerReview.isVisible = true
                                    tvStoreReview.isVisible = true
                                }
                                else{
                                    tvSellerReview.isVisible = false
                                    tvStoreReview.isVisible = false
                                }


                            } else {
                                llComplete.isVisible = false
                            }
                        }

                        else if (orderDetailData?.r_e_type=="0"&&orderDetailData?.p_orderdetails_id=="0"&&orderDetailData?.rEApprove=="0"&&orderDetailData?.returnType=="2"){
                             tvStatus.text = getString(R.string.exchange_req)
                             tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                             etStatus.isVisible = false
                             btnUpdateStatus.isVisible = false
                             clReturnExchange.isVisible = true
                             llComplete.isVisible = false
                             llReturnDetail.isVisible = true
                             tvExchangeSize.isVisible = true
                             tvExchangeSizeValue.isVisible = true
                             tvUploadedImage.isVisible = false
                             relUploadImage.isVisible = false
                             tvReturnDetails.text = getString(R.string.exchange_details)
                             tvReturnOn.text = getString(R.string.exchange_on)
                             tvReturnRequestId.text = getString(R.string.exchange_request_id)
                             tvReturnRequestIdValue.text = "#" + orderDetailData?.id
                             tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(
                                 orderDetailData?.updatedAt.toString(),
                                 outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a"
                             )
                             tvExchangeSizeValue.text = orderDetailData?.exchangeNewSize
                             tvReturnReasonValue.text = orderDetailData?.exchangeReason
                        }

                        else if (orderDetailData?.r_e_type=="1"&&orderDetailData?.p_orderdetails_id=="1"&&orderDetailData?.rEApprove=="1"&&orderDetailData?.returnType=="2"){
                            tvStatus.text = getString(R.string.exchange_complete)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            clReturnExchange.isVisible = false
                            llComplete.isVisible = false
                            llReturnDetail.isVisible = true
                            tvExchangeSize.isVisible = true
                            tvExchangeSizeValue.isVisible = true
                            tvUploadedImage.isVisible = false
                            relUploadImage.isVisible = false
                            tvReturnDetails.text = getString(R.string.exchange_details)
                            tvReturnOn.text = getString(R.string.exchange_on)
                            tvReturnRequestId.text = getString(R.string.exchange_request_id)
                            tvReturnRequestIdValue.text = "#" + orderDetailData?.id
                            tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(
                                orderDetailData?.updatedAt.toString(),
                                outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a"
                            )
                            tvExchangeSizeValue.text = orderDetailData?.exchangeNewSize
                            tvReturnReasonValue.text = orderDetailData?.exchangeReason
                        }

                        else if (orderDetailData?.r_e_type=="0"&&orderDetailData?.p_orderdetails_id=="0"&&orderDetailData?.rEApprove=="0"&&orderDetailData?.returnType=="1"){
                            tvStatus.text = getString(R.string.return_req)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            clReturnExchange.isVisible = true
                            llComplete.isVisible = false
                            llReturnDetail.isVisible = true
                            tvExchangeSize.isVisible = false
                            tvExchangeSizeValue.isVisible = false


                            tvReturnRequestIdValue.text = "#" + orderDetailData?.id
                            tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(
                                orderDetailData?.updatedAt.toString(),
                                outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a"
                            )
                            tvReturnReasonValue.text = orderDetailData?.returnReason
                            if (orderDetailData?.returnImage?.size != 0 && orderDetailData?.returnImage!=null
                            ) {
                                relUploadImage.isVisible = true
                                tvUploadedImage.isVisible = true
                                ivReturnImage.load(orderDetailData?.returnImage?.get(0)) {
                                    crossfade(true)
                                    placeholder(R.drawable.ic_placeholder)
                                    placeholder(R.drawable.ic_placeholder)
                                }
                            } else {
                                tvUploadedImage.isVisible = false
                                relUploadImage.isVisible = false
                            }
                       }

                        else if (orderDetailData?.r_e_type=="0"&&orderDetailData?.p_orderdetails_id=="0"&&orderDetailData?.rEApprove=="1"&&orderDetailData?.returnType=="1"){
                            tvStatus.text = getString(R.string.return_complete)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            clReturnExchange.isVisible = false
                            llComplete.isVisible = false
                            llReturnDetail.isVisible = true
                            tvExchangeSize.isVisible = false
                            tvExchangeSizeValue.isVisible = false


                            tvReturnRequestIdValue.text = "#" + orderDetailData?.id
                            tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(
                                orderDetailData?.updatedAt.toString(),
                                outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a"
                            )
                            tvReturnReasonValue.text = orderDetailData?.returnReason
                            if (orderDetailData?.returnImage?.size != 0 && orderDetailData?.returnImage!=null
                            ) {
                                relUploadImage.isVisible = true
                                tvUploadedImage.isVisible = true
                                ivReturnImage.load(orderDetailData?.returnImage?.get(0)) {
                                    crossfade(true)
                                    placeholder(R.drawable.ic_placeholder)
                                    placeholder(R.drawable.ic_placeholder)
                                }
                            } else {
                                tvUploadedImage.isVisible = false
                                relUploadImage.isVisible = false
                            }
                        }

                        else{
                            tvStatus.text = getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#047BD5"))
                            etStatus.isVisible = false
                            btnUpdateStatus.isVisible = false
                            clReturnExchange.isVisible = false


                            if (orderDetailData?.userRatingReviewProvide != null) {
                                llComplete.isVisible = true
                                rbProductRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.productRating
                                        ?: 0f
                                rbStoreRating.rating =
                                    orderDetailData?.userRatingReviewProvide?.storeRating
                                        ?: 0f
                                if (orderDetailData?.userRatingReviewProvide?.productReview!=null&&orderDetailData?.userRatingReviewProvide?.productReview!="null"&&orderDetailData?.userRatingReviewProvide?.productReview!=""){
                                    tvProductReview.text = orderDetailData?.userRatingReviewProvide?.productReview
                                    tvProductReview.isVisible = true
                                    tvReview.isVisible = true
                                }
                                else{
                                    tvProductReview.isVisible = false
                                    tvReview.isVisible = false
                                }

                                if (orderDetailData?.userRatingReviewProvide?.storeReview!=null&&orderDetailData?.userRatingReviewProvide?.storeReview!="null"&&orderDetailData?.userRatingReviewProvide?.storeReview!=""){
                                    tvSellerReview.text =
                                        orderDetailData?.userRatingReviewProvide?.storeReview
                                    tvSellerReview.isVisible = true
                                    tvStoreReview.isVisible = true
                                }
                                else{
                                    tvSellerReview.isVisible = false
                                    tvStoreReview.isVisible = false
                                }
                            } else {
                                llComplete.isVisible = false
                            }
                        }
                    }

                    "4" -> {
                        tvStatus.text = getString(R.string.cancelled)
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                        llReturnDetail.isVisible = true
                        tvExchangeSize.isVisible = false
                        tvExchangeSizeValue.isVisible = false
                        tvUploadedImage.isVisible = false
                        relUploadImage.isVisible = false
                        tvReturnDetails.text = getString(R.string.cancellation_details)
                        tvReturnOn.text = getString(R.string.cancelled_on)
                        tvReturnRequestId.text = getString(R.string.cancelled_request_id)
                        tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                        tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a")
                        tvReturnReasonValue.text = orderDetailData?.cancelReason
                        if (orderDetailData?.cancelReason!="null"&&orderDetailData?.cancelReason!=null&&orderDetailData?.cancelReason!=""){
                            tvReturnReason.isVisible = false
                            tvReturnReasonValue.isVisible = false
                        }
                        else{
                            tvReturnReason.isVisible = true
                            tvReturnReasonValue.isVisible = true
                        }
                    }

                    "5" -> {
                        tvStatus.text = getString(R.string.returned)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                        llReturnDetail.isVisible = true
                        tvExchangeSize.isVisible = false
                        tvExchangeSizeValue.isVisible = false


                        tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                        tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a")
                        tvReturnReasonValue.text = orderDetailData?.returnReason
                        if (orderDetailData?.returnImage?.size!=0
                        ) {
                            relUploadImage.isVisible = true
                            tvUploadedImage.isVisible = true
                            ivReturnImage.load(orderDetailData?.returnImage?.get(0)) {
                                crossfade(true)
                                placeholder(R.drawable.ic_placeholder)
                                kotlin.error(R.drawable.ic_placeholder)
                            }
                        } else {
                            tvUploadedImage.isVisible = false
                            relUploadImage.isVisible = false
                        }
                    }

                    "7" -> {
                        when (orderData?.returnType) {
                            "1" -> {
                                tvStatus.text = getString(R.string.picked_up)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                                etStatus.isVisible = false
                                btnUpdateStatus.isVisible = false
                                llComplete.isVisible = false
                            }

                            "2" -> {
                                tvStatus.text =  getString(R.string.picked_up)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                                etStatus.isVisible = false
                                btnUpdateStatus.isVisible = false
                                llComplete.isVisible = false
                            }

                            else -> {
                                tvStatus.text =  getString(R.string.out_for_delivery)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                                etStatus.isVisible = false
                                btnUpdateStatus.isVisible = false
                                llComplete.isVisible = false
                            }
                        }
                    }

                    "8" -> {
                        tvStatus.text =  getString(R.string.not_delivered)
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                    }

                    "9" -> {
                        tvStatus.text = getString(R.string.exchanged)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        etStatus.isVisible = true
                        btnUpdateStatus.isVisible = true
                        llComplete.isVisible = false
                        llReturnDetail.isVisible = true
                        tvExchangeSize.isVisible = true
                        tvExchangeSizeValue.isVisible = true
                        tvUploadedImage.isVisible = false
                        relUploadImage.isVisible = false
                        tvReturnDetails.text =  getString(R.string.exchange_details)
                        tvReturnOn.text = getString(R.string.exchange_on)
                        tvReturnRequestId.text = getString(R.string.exchange_request_id)
                        tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                        tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a")
                        tvExchangeSizeValue.text =  orderDetailData?.exchangeNewSize
                        tvReturnReasonValue.text =  orderDetailData?.exchangeReason
                    }
                }
            }
            tvDateTime.text = CommonMethod.customizeChangeDateFormat(
                orderDetailData?.createdAt.toString(),
                outputFormat = "dd/MM/yyyy '${getString(R.string.at)}' hh:mm a"
            )

            tvZoneValue.text = orderDetailData?.deliveryBoyId
            tvName.text = orderDetailData?.order?.userDetails?.fullName
            tvPhoneNumber.text = orderDetailData?.order?.userDetails?.countryCode+ " "+orderDetailData?.order?.userDetails?.mobile
            tvMailId.text = orderDetailData?.order?.userDetails?.email
          //  tvShippingLocation.text = orderDetailData?.order?.deliveryDetails?.address
            setAddress()
            ivUser.setImage(orderDetailData?.order?.userDetails?.profileImage,R.drawable.ic_user)
//            ivUser.load(orderDetailData?.order?.userDetails?.profileImage) {
//                crossfade(true)
//                placeholder(R.drawable.ic_user)
//                error(R.drawable.ic_user)
//            }
            tvOrderIdValue.text = "#${orderDetailData?.id}"
            tvOrderTypeValue.text = if (orderDetailData?.orderType=="2") getString(R.string.custom_order) else getString(
                R.string.normal_order
            )

                when(orderDetailData?.order?.paymentMethod){
                    "1"-> {
                        tvPaidBy.text = getString(R.string.by_card)
                    }
                    "2"->{
                        tvPaidBy.text = getString(R.string.by_wallet)
                    }
                    "3"-> {
                        tvTransaction.isVisible = false
                        tvTransactionId.isVisible = false
                        tvPaidBy.text = getString(R.string.via_cod)
                    }
                }
            tvTransactionId.text = "#${orderDetailData?.order?.transactionId}"

            if (orderDetailData?.orderType=="2") {
                tvCustomDetail.isVisible = true
                tvCustomDetailValue.isVisible = true
                clCustomOrderDetail.isVisible = true

                tvQuotedPrice.text = "${CommonMethod.formatPrice(orderDetailData?.chatRoomrecords?.totalAmount?.toDoubleOrNull()?:0.0)} ${
                    getString(
                        R.string.kd
                    )
                }"
                tvCustomDetailValue.text = orderDetailData?.chatRoomrecords?.customDetails
                tvTotalQuantityValue.text = orderDetailData?.chatRoomrecords?.quantity

                val subTotal = orderDetailData?.chatRoomrecords?.totalAmount?.toDouble() ?: 0.0
                val tax = orderDetailData?.tax?.toDouble() ?: 0.0
                val shippingFees = orderDetailData?.shippingFees?.toDouble() ?: 0.0
                var promoCode = 0.0
                if (orderDetailData?.order?.promocodeStatus=="yes"){
                    promoCode = orderDetailData?.order?.promocodeValue?:0.0
                }
                val totalAmount = subTotal + tax + shippingFees - promoCode

                tvItem.isVisible = false
                tvSubTotal.text =  "${getString(R.string.item_price)} (${CommonMethod.formatPrice(orderDetailData?.amount ?: 0.0)} ${
                    getString(
                        R.string.kd
                    )
                } X ${orderDetailData?.quantity ?: 1})"
                tvSubTotalValue.text = "${CommonMethod.formatPrice(orderDetailData?.chatRoomrecords?.totalAmount?.toDoubleOrNull()?:0.0)} ${
                    getString(
                        R.string.kd
                    )
                }"
                tvTotalValue.text = "${CommonMethod.formatPrice(totalAmount)} ${getString(R.string.kd)}"
                tvPaidByCustomerValue.text = "${CommonMethod.formatPrice(totalAmount)} ${getString(R.string.kd)}"
                tvDiscount.isVisible = false
                tvDiscountPrice.isVisible = false
            }
            else{
                tvCustomDetail.isVisible = false
                tvCustomDetailValue.isVisible = false
                clCustomOrderDetail.isVisible = false
                tvItem.isVisible = true
                tvSubTotal.text = getString(R.string.sub_total)
                tvSubTotalValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.totalMrp?:0.0)} ${
                    getString(
                        R.string.kd
                    )
                }"
                tvTotalValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.totalAmount?:0.0)} ${
                    getString(
                        R.string.kd
                    )
                }"
                tvPaidByCustomerValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.totalAmount?:0.0)} ${
                    getString(
                        R.string.kd
                    )
                }"
                if (CommonMethod.formatPrice(orderDetailData?.order?.discountMrp?:0.0)!="0"){
                    tvDiscount.isVisible = true
                    tvDiscountPrice.isVisible = true
                    tvDiscountPrice.text = "${CommonMethod.formatPrice(orderDetailData?.order?.discountMrp?:0.0)} ${
                        getString(
                            R.string.kd
                        )
                    }"

                }
                else{
                    tvDiscount.isVisible = false
                    tvDiscountPrice.isVisible = false
                }
            }

            tvItem.text = "${orderDetailData?.quantity} ${getString(R.string.items)}"

            tvAdminCommissionValue.text = "${CommonMethod.formatPrice(orderDetailData?.adminCommission?:0.0)} ${
                getString(
                    R.string.kd
                )
            }"
            if (CommonMethod.formatPrice(orderDetailData?.order?.shippingFees?:0.0)=="0"){
                tvShippingCharges.text = getString(R.string.free_delivery)
            }
            else{
                tvShippingCharges.text = "${CommonMethod.formatPrice(orderDetailData?.order?.shippingFees?:0.0)} ${
                    getString(
                        R.string.kd
                    )
                }"
            }

            tvOrderTaxValue.text = "${CommonMethod.formatPrice(orderDetailData?.tax?:0.0)} ${
                getString(
                    R.string.kd
                )
            }"
            if (orderDetailData?.order?.promocodeStatus=="yes"){
                tvCouponApplied.isVisible = true
                tvCouponAppliedValue.isVisible = true
                tvSaved.isVisible = true
                tvCouponAppliedValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.promocodeValue?:0.0)} KD"

            }
            else{
                tvCouponApplied.isVisible = false
                tvCouponAppliedValue.isVisible = false
                tvSaved.isVisible = false
            }
        }
    }

    private fun setAddress(){
        binding?.apply {
            val delivery = orderDetailData?.order?.deliveryDetails

            val address = delivery?.address ?: ""
            val city = delivery?.city ?: ""
            val country = delivery?.countryName ?: ""

            val addressList = mutableListOf<String>()

            if (address.isNotBlank()) {
                addressList.add(address)
            }

            delivery?.houseNo?.takeIf { it.isNotBlank() }?.let {
                addressList.add(it)
            }

            delivery?.streetNo?.takeIf { it.isNotBlank() }?.let {
                addressList.add(it)
            }

            if (city.isNotBlank() && !address.contains(city, true)) {
                addressList.add(city)
            }

            delivery?.state?.takeIf { it.isNotBlank() }?.let {
                addressList.add(it)
            }

            if (country.isNotBlank() && !address.contains(country, true)) {
                addressList.add(country)
            }

            delivery?.postalCode?.takeIf { it.isNotBlank() }?.let {
                addressList.add(it)
            }

            tvShippingLocation.text = addressList.joinToString(", ")
        }
    }

    private fun updateDeliveryStatusDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = UpdateOrderStatusDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            rbShipped.text = getString(R.string.shipped)
            ivBack.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbShipped ->{

                        selectedStatus = "2.1"
                        binding?.etStatus?.setText(getString(R.string.shipped))

                        dialog.dismiss()
                    }
                   /* R.id.rbReturn ->{
                        binding?.etStatus?.setText("Return")
                        selectedStatus = "5"
                        dialog.dismiss()
                    }
                    R.id.rbExchange -> {
                        binding?.etStatus?.setText("Exchange")
                        selectedStatus = "9"
                        dialog.dismiss()
                    }
                    R.id.rbCancelled -> {
                        binding?.etStatus?.setText("Cancelled")
                        selectedStatus = "4"

                        dialog.dismiss()
                    }*/
                    else -> ""
                }

            }

        }

        dialog.show()
    }

    private fun getOrderDetail(){
        val map = HashMap<String,Any>()
        map["id"] = orderData?.id.toString()
        viewModel.getOrderDetail(map)
    }

    private fun updateOrderStatus(status:String){
        val map = HashMap<String,Any>()
        map["id"] = orderData?.id.toString()
        map["status"] = status
        viewModel.updateOrderStatus(map)
    }

    private fun updateOrderExchangeReturnStatus(status:String){
        val map = HashMap<String,Any>()
        map["id"] = orderData?.id.toString()
        map["status"] = status
        viewModel.updateOrderExchangeReturnStatus(map)
    }
}