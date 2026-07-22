package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentPaymentMethodBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.ui.home.mySubscriptionPlan.response.GetMyPlanResponse
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import com.myfatoorah.sdk.entity.MFBoxShadow
import com.myfatoorah.sdk.entity.MFCardViewError
import com.myfatoorah.sdk.entity.MFCardViewInput
import com.myfatoorah.sdk.entity.MFCardViewLabel
import com.myfatoorah.sdk.entity.MFCardViewPlaceHolder
import com.myfatoorah.sdk.entity.MFCardViewStyle
import com.myfatoorah.sdk.entity.MFCardViewText
import com.myfatoorah.sdk.entity.executepayment.MFExecutePaymentRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentResponse
import com.myfatoorah.sdk.entity.paymentstatus.MFGetPaymentStatusResponse
import com.myfatoorah.sdk.enums.MFAPILanguage
import com.myfatoorah.sdk.enums.MFCurrencyISO
import com.myfatoorah.sdk.enums.MFFontFamily
import com.myfatoorah.sdk.enums.MFFontWeight
import com.myfatoorah.sdk.views.MFResult
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentMethodFragment : BaseFragment() {
    private var binding: FragmentPaymentMethodBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var subscriptionData: GetAllPlanResponse.Data? = null
    private var mySubscriptionData: GetMyPlanResponse.Data.Data? = null
    private var bannerData: GetAllBannerResponse.Data? = null
    private var storyData: GetStoryResponse.Data? = null
    private var profileData: GetSellerInfo.Data? = null
    private var paymentData: MFGetPaymentStatusResponse? = null
    private var isLoad = false
    private var from = ""
    private var transactionId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentMethodBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        profileData = SharedPreference.get(requireActivity()).getUser()
        initiateSession()
        // cardStyling()
        from = arguments?.getString("from").toString()
        when (from) {
            "subscription", "mySubscription" -> {
                val itemJson = arguments?.getString("subscriptionData")
                transactionId = arguments?.getString("transactionId").toString()
                if (!itemJson.isNullOrEmpty()) {
                    subscriptionData =
                        Gson().fromJson(itemJson, GetAllPlanResponse.Data::class.java)

                }
            }

            "renew" -> {
                val itemJson = arguments?.getString("mySubscriptionData")
                transactionId = arguments?.getString("transactionId").toString()
                if (!itemJson.isNullOrEmpty()) {
                    mySubscriptionData =
                        Gson().fromJson(itemJson, GetMyPlanResponse.Data.Data::class.java)

                }
            }

            "banner" -> {
                val itemJson = arguments?.getString("bannerData")
                if (!itemJson.isNullOrEmpty()) {
                    bannerData = Gson().fromJson(itemJson, GetAllBannerResponse.Data::class.java)
                }
            }

            "story" -> {
                val itemJson = arguments?.getString("storyData")
                if (!itemJson.isNullOrEmpty()) {
                    storyData = Gson().fromJson(itemJson, GetStoryResponse.Data::class.java)
                }
            }
        }

        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.payment_method)
            when (from) {
                "subscription", "mySubscription" -> {
                    tvTotalAmount.text =
                        "${CommonMethod.formatPrice(subscriptionData?.price ?: 0.0)} ${
                            getString(
                                R.string.kd
                            )
                        }"
                    when (subscriptionData?.durationType) {
                        1 -> {
                            tvDays.text =
                                "${getString(R.string.for_text)} ${subscriptionData?.duration} ${
                                    getString(
                                        R.string.month
                                    )
                                }"
                        }

                        else -> {
                            tvDays.text =
                                "${getString(R.string.for_text)} ${subscriptionData?.duration} ${
                                    getString(
                                        R.string.days
                                    )
                                }"
                        }
                    }
                }

                "renew" -> {
                    tvTotalAmount.text =
                        "${CommonMethod.formatPrice(mySubscriptionData?.planInfo?.price?.toDoubleOrNull() ?: 0.0)} ${
                            getString(
                                R.string.kd
                            )
                        }"
                    when (mySubscriptionData?.planInfo?.durationType) {
                        1 -> {
                            tvDays.text =
                                "${getString(R.string.for_text)} ${mySubscriptionData?.planInfo?.duration} ${
                                    getString(
                                        R.string.month
                                    )
                                }"
                        }

                        else -> {
                            tvDays.text =
                                "${getString(R.string.for_text)} ${mySubscriptionData?.planInfo?.duration} ${
                                    getString(
                                        R.string.days
                                    )
                                }"
                        }
                    }
                }

                "banner" -> {
                    tvTotalAmount.text = "${CommonMethod.formatPrice(bannerData?.price ?: 0.0)} ${
                        getString(
                            R.string.kd
                        )
                    }"
                    tvDays.text =
                        "${getString(R.string.for_text)} ${bannerData?.totalDays} ${getString(R.string.days)}"
                }

                "story" -> {
                    tvTotalAmount.text =
                        "${CommonMethod.formatPrice(storyData?.amount?.toDoubleOrNull() ?: 0.0)} ${
                            getString(
                                R.string.kd
                            )
                        }"
                    tvDays.text =
                        "${getString(R.string.for_text)} ${storyData?.totalDays} ${getString(R.string.days)}"
                }
            }

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvAddNewCard.setOnClickListener {
                findNavController().navigate(R.id.action_paymentMethodFragment_to_AddCardFragment)
            }

            btnPay.setOnClickListener {
                if (isLoad) {
                    executeRedirectPayment(
                        2,
                        if (from == "subscription") subscriptionData?.price
                            ?: 0.0 else if (from == "mySubscription") subscriptionData?.price
                            ?: 0.0 else if (from == "renew") mySubscriptionData?.planInfo?.price?.toDoubleOrNull()
                            ?: 0.0 else if (from == "banner") bannerData?.price ?: 0.0
                        else storyData?.amount?.toDoubleOrNull() ?: 0.0
                    )
                    //  executeEmbeddedPayment()
                }

            }
        }
    }


    override fun observe() {
        viewModel.createSubscriptionPlanResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    result.data?.data?.transactionId?.let { buySubscriptionPlan(it) }
                    viewModel.createSubscriptionPlanResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.createSubscriptionPlanResponse.value = null
                }

                else -> {

                }
            }
        }

        viewModel.renewSubscriptionPlanResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    result.data?.data?.transactionId?.let { buySubscriptionPlan(it) }
                    viewModel.renewSubscriptionPlanResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.renewSubscriptionPlanResponse.value = null
                }

                else -> {

                }
            }
        }

        viewModel.buySubscriptionPlanResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()

                    when (from) {
                        "renew" -> {
                            CommonMethod.showSuccessDialog(
                                requireActivity(), getString(R.string.plan_renewed_successfully)
                            ) {
                                SharedPreference.get(requireActivity()).isSubscription = true
                                findNavController().popBackStack()
                            }
                        }

                        "mySubscription" -> {
                            CommonMethod.showSuccessDialog(
                                requireActivity(), getString(R.string.plan_purchased_successfully)
                            ) {
                                SharedPreference.get(requireActivity()).isSubscription = true
                                findNavController().popBackStack()
                            }

                        }

                        else -> {
                            CommonMethod.showSuccessDialog(
                                requireActivity(), getString(R.string.plan_purchased_successfully)
                            ) {
                                SharedPreference.get(requireActivity()).isSubscription = true
                                findNavController().navigate(R.id.action_paymentMethodFragment_to_HomeFragment)
                            }
                        }
                    }

                    viewModel.buySubscriptionPlanResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.buySubscriptionPlanResponse.value = null
                }

                else -> {

                }
            }
        }

        viewModel.bannerPaymentResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "bannerPaymentDone",
                        true
                    )
                    CommonMethod.showSuccessDialog(
                        requireActivity(), result.data?.message.toString()
                    ) {
                        findNavController().popBackStack()
                    }
                    viewModel.bannerPaymentResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.bannerPaymentResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.completeStoryPaymentResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "bannerPaymentDone",
                        true
                    ) // Reusing this to refresh if needed
                    CommonMethod.showSuccessDialog(
                        requireActivity(), result.data?.message.toString()
                    ) {
                        findNavController().popBackStack()
                    }
                    viewModel.completeStoryPaymentResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.completeStoryPaymentResponse.value = null
                }

                else -> {}
            }
        }

    }

    private fun cardStyling() {
        binding?.mfPaymentView?.cardStyle = MFCardViewStyle(
            hideCardIcons = false, direction = "ltr", cardHeight = 230, input = MFCardViewInput(
                color = requireActivity().getColor(R.color.black),
                fontSize = 15f,
                fontFamily = MFFontFamily.CourierNew,
                inputHeight = 40f,
                inputMargin = 6f,
                borderColor = requireActivity().getColor(R.color.grey),
                borderWidth = 1f,
                borderRadius = 8f,
                boxShadow = MFBoxShadow(10, 10, 5, 0, requireActivity().getColor(R.color.white)),
                placeHolder = MFCardViewPlaceHolder(
                    holderName = getString(R.string.name_on_card),
                    cardNumber = getString(R.string.number),
                    expiryDate = getString(R.string.mm_yy),
                    securityCode = getString(R.string.cvv)
                )
            ), label = MFCardViewLabel(
                display = true,
                color = requireActivity().getColor(R.color.black),
                fontSize = 14f,
                fontFamily = MFFontFamily.CourierNew,
                fontWeight = MFFontWeight.Bold,
                text = MFCardViewText(
                    holderName = getString(R.string.card_holder_name),
                    cardNumber = getString(R.string.card_number),
                    expiryDate = getString(R.string.expiry_date),
                    securityCode = getString(R.string.security_code)
                )
            ), error = MFCardViewError(
                borderColor = requireActivity().getColor(R.color.red),
                borderRadius = 8f,
                boxShadow = MFBoxShadow(10, 10, 5, 0, requireActivity().getColor(R.color.white))
            )
        )
    }

    private fun initiateSession() {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        MFSDK.initiateSession {
            when (it) {
                is MFResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    isLoad = true/*  binding?.mfPaymentView?.load(
                          it.response,
                          onCardBinChanged = { bin ->
                              Log.d("initiateSession", "bin: $bin")
                          }
                      )*/
                }

                is MFResult.Fail -> {
                    isLoad = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    it.error.message?.let { it1 -> showToast(it1) }
                    Log.d("initiateSession", "Fail: " + Gson().toJson(it.error))
                }

                MFResult.Loading -> {

                }
            }
        }
    }

    private fun executeEmbeddedPayment() {
        viewLifecycleOwner.lifecycleScope.launch {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            val request = if (from == "subscription") {
                MFExecutePaymentRequest(subscriptionData?.price).apply {
                    customerReference = subscriptionData?.id.toString()
                    customerName = "${profileData?.firstName} ${profileData?.lastName}"
                    mobileCountryCode = profileData?.countryCode
                    customerMobile = profileData?.mobile
                    customerEmail = profileData?.email
                }
            } else if (from == "banner") {
                MFExecutePaymentRequest(bannerData?.price).apply {
                    customerReference = bannerData?.id.toString()
                    customerName = "${profileData?.firstName} ${profileData?.lastName}"
                    mobileCountryCode = profileData?.countryCode
                    customerMobile = profileData?.mobile
                    customerEmail = profileData?.email
                }
            } else {
                MFExecutePaymentRequest(storyData?.amount?.toDoubleOrNull()).apply {
                    customerReference = storyData?.id.toString()
                    customerName = "${profileData?.firstName} ${profileData?.lastName}"
                    mobileCountryCode = profileData?.countryCode
                    customerMobile = profileData?.mobile
                    customerEmail = profileData?.email
                }
            }

            binding?.mfPaymentView?.validate { result: MFResult<String> ->
                when (result) {
                    is MFResult.Success -> {
                        Log.d(
                            "executeEmbeddedPayment123",
                            "Response: " + Gson().toJson(result.response)
                        )
                        ProgressDialogUtils.getInstance().hideProgress()
                        binding?.mfPaymentView?.pay(
                            requireActivity(), request, MFAPILanguage.EN, onInvoiceCreated = {
                                Log.d(
                                    "executeEmbeddedPayment", "invoiceId: $it"
                                )
                            }) { invoiceId: String, result: MFResult<MFGetPaymentStatusResponse> ->
                            when (result) {
                                is MFResult.Success -> {
                                    Log.d(
                                        "executeEmbeddedPayment",
                                        "Response: " + Gson().toJson(result.response)
                                    )
                                    paymentData = result.response
                                    if (from == "subscription") {
                                        buySubscriptionPlan(transactionId)
                                    } else if (from == "mySubscription") {
                                        buySubscriptionPlan(transactionId)
                                    } else if (from == "renew") {
                                        buySubscriptionPlan(transactionId)
                                    } else if (from == "banner") {
                                        payForBanner()
                                    } else {
                                        payForStory()
                                    }

                                }

                                is MFResult.Fail -> {
                                    //   result.error.message?.let { showToast(it) }
                                    Log.d(
                                        "executeEmbeddedPayment",
                                        "Response: " + Gson().toJson(result)
                                    )
                                    Log.d(
                                        "executeEmbeddedPayment",
                                        "Fail: " + Gson().toJson(result.error)
                                    )
                                    showToast(getString(R.string.payment_failed))
                                    lifecycleScope.launchWhenResumed {
                                        if (isAdded && !isDetached && findNavController().currentDestination?.id == R.id.paymentMethod) {
                                            try {
                                                findNavController().popBackStack()
                                            } catch (e: Exception) {
                                                Log.w(
                                                    "executeEmbeddedPayment",
                                                    "Safe popBackStack failed: ${e.message}"
                                                )
                                            }
                                        }
                                    }
                                }

                                MFResult.Loading -> {
                                }
                            }
                        }
                    }

                    is MFResult.Fail -> {
                        Log.d("executeEmbeddedPayment123", "Fail: " + Gson().toJson(result.error))
                        ProgressDialogUtils.getInstance().hideProgress()
                        result.error.message?.let { showToast(it) }
                    }

                    MFResult.Loading -> {

                    }
                }
            }
        }
    }

    fun createSubscriptionPlan() {
        val map = HashMap<String, Any>()
        map["plan_id"] = subscriptionData?.id.toString()
        viewModel.createSubscriptionPlan(map)
    }

    private fun renewSubscriptionPlan() {
        val map = HashMap<String, Any>()
        map["plan_id"] = mySubscriptionData?.planId.toString()
        viewModel.renewSubscriptionPlan(map)
    }

    fun buySubscriptionPlan(transactionId: String) {
        val map = HashMap<String, Any>()
        if (from == "renew") {
            map["plan_id"] = mySubscriptionData?.planId.toString()
        } else {
            map["plan_id"] = subscriptionData?.id.toString()
        }
        map["transaction_id"] = transactionId
        map["payment_status"] = 1// 1 paid 2 reject
        if (paymentData != null) {
            map["online_transaction_id"] =
                paymentData?.invoiceTransactions?.get(0)?.transactionId.toString()
        } else {
            map["online_transaction_id"] = "Online-$transactionId"
        }
        viewModel.buySubscriptionPlan(map)
    }

    private fun payForBanner() {
        val map = HashMap<String, Any>()
        map["order_id"] = bannerData?.orderId.toString()
        map["payment_status"] = "1"
        map["transaction_id"] = paymentData?.invoiceTransactions?.get(0)?.transactionId.toString()
        viewModel.bannerPayment(map)
    }

    private fun payForStory() {
        val map = HashMap<String, Any>()
        map["story_id"] = storyData?.id.toString()
        map["transaction_id"] = paymentData?.invoiceTransactions?.get(0)?.transactionId.toString()
        viewModel.completeStoryPayment(map)
    }

    private fun executeRedirectPayment(paymentMethodId: Int, price: Double) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        val requestInitiate = MFInitiatePaymentRequest(0.100, MFCurrencyISO.KUWAIT_KWD)
        MFSDK.initiatePayment(
            requestInitiate, MFAPILanguage.EN
        ) { result: MFResult<MFInitiatePaymentResponse> ->
            when (result) {
                is MFResult.Success -> Log.d(
                    "executeRedirectPayment",
                    "Response: " + Gson().toJson(result.response)
                )

                is MFResult.Fail -> Log.d(
                    "executeRedirectPayment",
                    "Fail: " + Gson().toJson(result.error)
                )

                else -> {}
            }
        }

        val request = MFExecutePaymentRequest(paymentMethodId, price).apply {}

        MFSDK.executePayment(
            requireActivity(), request, MFAPILanguage.EN
        ) { invoiceId: String, result: MFResult<MFGetPaymentStatusResponse> ->
            ProgressDialogUtils.getInstance().hideProgress()

            when (result) {
                is MFResult.Success -> {
                    Log.d("executeRedirectPayment", "Success: ${Gson().toJson(result.response)}")
                    paymentData = result.response
                    if (from == "subscription") {
                        buySubscriptionPlan(transactionId)
                    } else if (from == "mySubscription") {
                        buySubscriptionPlan(transactionId)
                    } else if (from == "renew") {
                        buySubscriptionPlan(transactionId)
                    } else if (from == "banner") {
                        payForBanner()
                    } else {
                        payForStory()
                    }
                }

                is MFResult.Fail -> {
                    Log.d("executeRedirectPayment", "Fail: ${Gson().toJson(result.error)}")
                    result.error.message?.let { showToast(it) }
//                    showToast("Payment failed")

                }

                MFResult.Loading -> {
                }
            }
        }
    }

}