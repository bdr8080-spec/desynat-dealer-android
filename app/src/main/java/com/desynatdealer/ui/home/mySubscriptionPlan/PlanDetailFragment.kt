package com.desynatdealer.ui.home.mySubscriptionPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentPlanDetailBinding
import com.desynatdealer.ui.home.mySubscriptionPlan.response.GetMyPlanResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PlanDetailFragment : BaseFragment() {

    private var binding: FragmentPlanDetailBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var mySubscriptionData: GetMyPlanResponse.Data.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanDetailBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.details)

            val itemJson = arguments?.getString("mySubscriptionData")
            if (!itemJson.isNullOrEmpty()) {
                mySubscriptionData = Gson().fromJson(itemJson, GetMyPlanResponse.Data.Data::class.java)
                setData()
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnUpgrade.setOnClickListener {
                renewSubscriptionPlan()
            }
        }
    }

    override fun observe() {
        viewModel.renewSubscriptionPlanResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    val bundle = Bundle().apply {
                        putString(
                            "mySubscriptionData",
                            Gson().toJson(mySubscriptionData)
                        )
                        putString("transactionId", result.data?.data?.transactionId)
                        putString("from", "renew")
                    }
                    findNavController().navigate(
                        R.id.action_mySubscription_to_paymentMethodFragment,
                        bundle
                    )
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
    }

        private fun renewSubscriptionPlan() {
            val map = HashMap<String,Any>()
            map["plan_id"] = mySubscriptionData?.planId.toString()
            viewModel.renewSubscriptionPlan(map)
        }

    private fun setData(){
        binding?.apply {
            if (mySubscriptionData?.isExpired=="0"){
                tvStatus.text = getString(R.string.active)
                btnUpgrade.text = getString(R.string.upgrade_plan)
                btnUpgrade.isVisible = false
                CommonMethod.applyGradientText(tvStatus)
            }
            else{
                tvStatus.paint.shader = null
                tvStatus.setTextColor(requireActivity().getColor(R.color.black))
                tvStatus.text = getString(R.string.expired)
                btnUpgrade.text = getString(R.string.renew_plan)
            }

            tvPlanName.text = if (Constants.language=="ar") mySubscriptionData?.planInfo?.planNameArabic else mySubscriptionData?.planInfo?.planNameEnglish
            tvAmountValue.text = "${CommonMethod.formatPrice(mySubscriptionData?.planInfo?.price?.toDoubleOrNull()?:0.0)} ${
                getString(
                    R.string.kd
                )
            }"
            tvPaidAmount.text = "${CommonMethod.formatPrice(mySubscriptionData?.planInfo?.price?.toDoubleOrNull()?:0.0)} ${
                getString(
                    R.string.kd
                )
            }"
            tvPurchaseOn.text = CommonMethod.customizeChangeDateFormat(mySubscriptionData?.createdAt.toString(), outputFormat = "dd/MM/yyyy '${
                getString(R.string.at)
            }' hh:mm a")
            tvExpiredOn.text = CommonMethod.customizeChangeDateFormat(mySubscriptionData?.planEndDate.toString(), inputFormat = "yyyy-MM-dd hh:mm:ss", outputFormat = "dd/MM/yyyy '${
                getString(R.string.at)
            }' hh:mm a")
            tvTransactionId.text = "${mySubscriptionData?.transactionId}"

            when(mySubscriptionData?.paymentMethod){
                "1"->{
                    tvPaymentType.text = getString(R.string.paid_by_card)
                    ivPaymentType.setImageResource(R.drawable.ic_card)
                }
                "2"->{
                    tvPaymentType.text = getString(R.string.paid_by_wallet)
                    ivPaymentType.setImageResource(R.drawable.ic_wallet)
                }
            }

            when(mySubscriptionData?.planInfo?.durationType){
                1 -> {
                    tvDurationValue.text = "${getString(R.string.for_text)} ${mySubscriptionData?.planInfo?.duration} ${
                        getString(
                            R.string.month
                        )
                    }"
                }
                2 -> {
                    tvDurationValue.text = "${getString(R.string.for_text)} ${mySubscriptionData?.planInfo?.duration} ${
                        getString(
                            R.string.year
                        )
                    }"
                }
                else -> {
                    tvDurationValue.text = "${getString(R.string.for_text)} ${mySubscriptionData?.planInfo?.duration} ${
                        getString(
                            R.string.month
                        )
                    }"
                }
            }

            btnUpgrade.setOnClickListener {
                if (mySubscriptionData?.isExpired=="1"){
                    val bundle = Bundle().apply {
                        putString("mySubscriptionData", Gson().toJson(mySubscriptionData))
                        putString("from", "renew")
                    }
                    findNavController().navigate(R.id.action_subscriptionDetailFragment_to_paymentMethodFragment,bundle)
                }
            }

        }

    }

}