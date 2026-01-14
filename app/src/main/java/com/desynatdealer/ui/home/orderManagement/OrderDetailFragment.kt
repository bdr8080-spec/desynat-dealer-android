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

            btnUpdateStatus.setOnClickListener {

                if (orderDetailData?.returnType=="2"){
                    updateOrderExchangeReturnStatus(selectedStatus)
                }
                else {
                    updateOrderStatus(selectedStatus)
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
                    ProgressDialogUtils.getInstance().hideProgress()
                    orderDetailData?.deliveryStatus = result.data?.data?.deliveryStatus.toString()
                    setData()
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
                    ProgressDialogUtils.getInstance().hideProgress()
                    orderDetailData?.deliveryStatus = result.data?.data?.deliveryStatus.toString()
                    setData()
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
                tvStatus.text = "Cancelled"
                tvStatus.setTextColor(Color.parseColor("#E70000"))
            }
            else {
                when (orderDetailData?.deliveryStatus) {
                    "1" -> {
                        tvStatus.text = "Confirmed"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.isVisible = true
                        btnUpdateStatus.isVisible = true
                        llComplete.isVisible = false
                    }

                    "2" -> {
                        tvStatus.text = "Confirmed"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.isVisible = true
                        btnUpdateStatus.isVisible = true
                        llComplete.isVisible = false
                    }
                    "2.1" -> {
                        tvStatus.text = "Shipped"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                    }
                    "2.2" -> {
                        tvStatus.text = "Out for Delivery"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                    }

                    "3" -> {

                        when (orderDetailData?.returnType) {
                            "1" -> {
                                tvStatus.text = "Return Req"
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                                etStatus.isVisible = false
                                btnUpdateStatus.isVisible = false
                                llComplete.isVisible = false
                                llReturnDetail.isVisible = true
                                tvExchangeSize.isVisible = false
                                tvExchangeSizeValue.isVisible = false


                                tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                                tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
                                tvReturnReasonValue.text = orderDetailData?.returnReason
                                if (orderDetailData?.returnImage != null && orderDetailData?.returnImage != "" && orderDetailData?.returnImage != "null"
                                ) {
                                    relUploadImage.isVisible = true
                                    tvUploadedImage.isVisible = true
                                    ivReturnImage.load(orderDetailData?.returnImage) {
                                        crossfade(true)
                                        placeholder(R.drawable.ic_placeholder)
                                        kotlin.error(R.drawable.ic_placeholder)
                                    }
                                } else {
                                    tvUploadedImage.isVisible = false
                                    relUploadImage.isVisible = false
                                }
                            }

                            "2" -> {
                                tvStatus.text = "Exchange Req"
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                                etStatus.isVisible = true
                                btnUpdateStatus.isVisible = true
                                llComplete.isVisible = false
                                llReturnDetail.isVisible = true
                                tvExchangeSize.isVisible = true
                                tvExchangeSizeValue.isVisible = true
                                tvUploadedImage.isVisible = false
                                relUploadImage.isVisible = false
                                tvReturnDetails.text = "Exchange Details"
                                tvReturnOn.text = "Exchange on"
                                tvReturnRequestId.text = "Exchange Request ID"
                                tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                                tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
                                tvExchangeSizeValue.text =  orderDetailData?.exchangeNewSize
                                tvReturnReasonValue.text =  orderDetailData?.exchangeReason
                            }

                            else -> {
                                tvStatus.text = "Delivered"
                                tvStatus.setTextColor(Color.parseColor("#047BD5"))
                                etStatus.isVisible = false
                                btnUpdateStatus.isVisible = false


                                if (orderDetailData?.userRatingReviewProvide != null) {
                                    llComplete.isVisible = true
                                    rbProductRating.rating =
                                        orderDetailData?.userRatingReviewProvide?.productRating
                                            ?: 0f
                                    rbStoreRating.rating =
                                        orderDetailData?.userRatingReviewProvide?.productRating
                                            ?: 0f
                                    tvProductReview.text =
                                        orderDetailData?.userRatingReviewProvide?.productReview
                                    tvSellerReview.text =
                                        orderDetailData?.userRatingReviewProvide?.storeReview
                                } else {
                                    llComplete.isVisible = false
                                }
                            }
                        }
                    }

                    "4" -> {
                        tvStatus.text = "Cancelled"
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                        llReturnDetail.isVisible = true
                        tvExchangeSize.isVisible = false
                        tvExchangeSizeValue.isVisible = false
                        tvUploadedImage.isVisible = false
                        relUploadImage.isVisible = false
                        tvReturnDetails.text = "Cancellation details"
                        tvReturnOn.text = "Cancelled on"
                        tvReturnRequestId.text = "Cancelled Request ID"
                        tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                        tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
                        tvReturnReasonValue.text = orderDetailData?.cancelReason
                    }

                    "5" -> {
                        tvStatus.text = "Return Req"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                        llReturnDetail.isVisible = true
                        tvExchangeSize.isVisible = false
                        tvExchangeSizeValue.isVisible = false


                        tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                        tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
                        tvReturnReasonValue.text = orderDetailData?.returnReason
                        if (orderDetailData?.returnImage != null && orderDetailData?.returnImage != "" && orderDetailData?.returnImage != "null"
                        ) {
                            relUploadImage.isVisible = true
                            tvUploadedImage.isVisible = true
                            ivReturnImage.load(orderDetailData?.returnImage) {
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
                        tvStatus.text = "Out for Delivery"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                    }

                    "8" -> {
                        tvStatus.text = "Not Delivered"
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                        etStatus.isVisible = false
                        btnUpdateStatus.isVisible = false
                        llComplete.isVisible = false
                    }

                    "9" -> {
                        tvStatus.text = "Exchange Req"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        etStatus.isVisible = true
                        btnUpdateStatus.isVisible = true
                        llComplete.isVisible = false
                        llReturnDetail.isVisible = true
                        tvExchangeSize.isVisible = true
                        tvExchangeSizeValue.isVisible = true
                        tvUploadedImage.isVisible = false
                        relUploadImage.isVisible = false
                        tvReturnDetails.text = "Exchange Details"
                        tvReturnOn.text = "Exchange on"
                        tvReturnRequestId.text = "Exchange Request ID"
                        tvReturnRequestIdValue.text = "#"+orderDetailData?.id
                        tvReturnOnValue.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
                        tvExchangeSizeValue.text =  orderDetailData?.exchangeNewSize
                        tvReturnReasonValue.text =  orderDetailData?.exchangeReason
                    }
                }
            }
            tvDateTime.text = CommonMethod.customizeChangeDateFormat(
                orderDetailData?.createdAt.toString(),
                outputFormat = "dd/MM/yyyy 'at' hh:mm a"
            )

            tvZoneValue.text = orderDetailData?.deliveryBoyId
            tvName.text = orderDetailData?.order?.userDetails?.fullName
            tvPhoneNumber.text = orderDetailData?.order?.userDetails?.countryCode+ " "+orderDetailData?.order?.userDetails?.mobile
            tvMailId.text = orderDetailData?.order?.userDetails?.email
            tvShippingLocation.text = orderDetailData?.order?.deliveryDetails?.address
            ivUser.load(orderDetailData?.order?.userDetails?.profileImage) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            tvOrderIdValue.text = "#${orderDetailData?.order?.id}"
          //  tvOrderTypeValue.text = if (orderDetailData?.order?.orderType=="1") "Normal Order" else "Custom Order"

                when(orderDetailData?.order?.paymentMethod){
                    "1"-> {
                        tvPaidBy.text = "By Card"
                        tvCustomPaidBy.text = "By Card"
                    }
                    "2"->{
                        tvPaidBy.text ="By Wallet"
                        tvCustomPaidBy.text ="By Wallet"
                    }
                    "3"-> {
                        tvTransaction.isVisible = false
                        tvTransactionId.isVisible = false
                        tvCustomTransaction.isVisible = false
                        tvCustomTransactionId.isVisible = false
                        tvPaidBy.text ="Via COD"
                        tvCustomPaidBy.text ="Via COD"
                    }
                }
            tvTransactionId.text = "#${orderDetailData?.order?.transactionId}"
            tvCustomTransactionId.text = "#${orderDetailData?.order?.transactionId}"

            if (orderDetailData?.order?.orderType=="2"){
                clCustomOrderBilling.isVisible = true
                clBilling.isVisible = false
            }
            else{
                clCustomOrderBilling.isVisible = false
                clBilling.isVisible = true
            }

            tvPerItemPrice.text = "Item Price (${CommonMethod.formatPrice(orderDetailData?.amount?:0.0)} X ${orderDetailData?.quantity})"
            tvPerItemPriceValue.text = "${CommonMethod.formatPrice((orderDetailData?.amount?:0.0 )* (orderDetailData?.quantity?:1))} KD"
            tvShippingFees.text = "${CommonMethod.formatPrice(orderDetailData?.shippingFees?:0.0)} KD"
            tvTaxValue.text = "${CommonMethod.formatPrice(orderDetailData?.tax?:0.0)} KD"
            tvTotalAmountValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.totalAmount?:0.0)} KD"

            tvItem.text = "${orderDetailData?.quantity} items"
            tvTotalValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.totalAmount?:0.0)} KD"
            tvPaidByCustomerValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.totalAmount?:0.0)} KD"
            tvAdminCommissionValue.text = "${CommonMethod.formatPrice(orderDetailData?.adminCommission?:0.0)} KD"
            tvSubTotalValue.text = "${CommonMethod.formatPrice(orderDetailData?.amount?:0.0)} KD"
            tvShippingCharges.text = "${CommonMethod.formatPrice(orderDetailData?.order?.shippingFees?:0.0)} KD"
            if (orderDetailData?.order?.promocodeStatus=="yes"){
                tvCouponAppliedValue.isVisible = true
                tvCouponApplied.isVisible = true
              //  tvCouponApplied.text = "Coupon Discount (${orderDetailData?.order?.promocodeDetails?.nameEnglish})"
                tvCouponAppliedValue.text = "${CommonMethod.formatPrice(orderDetailData?.order?.promocodeValue?:0.0)} KD"
                tvSaved.text = "Saved ${CommonMethod.formatPrice(orderDetailData?.order?.promocodeValue?:0.0)} KD"

            }
            else{
                tvCouponApplied.isVisible = false
                tvCouponAppliedValue.isVisible = false
            }

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

            if (orderDetailData?.returnType=="2"){
                rbShipped.text = "Exchange"
            }
            else{
                rbShipped.text = "Shipped"
            }

            ivBack.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbShipped ->{

                        if (orderDetailData?.returnType=="2"){
                            selectedStatus = "1"
                            binding?.etStatus?.setText("Exchange")
                        }
                        else{
                            selectedStatus = "2.1"
                            binding?.etStatus?.setText("Shipped")
                        }

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