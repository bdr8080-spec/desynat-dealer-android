package com.desynatdealer.ui.home.bannerManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentBannerDetailBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerDetailFragment : BaseFragment() {
    private var binding: FragmentBannerDetailBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var bannerData: GetAllBannerResponse.Data? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerDetailBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("bannerPaymentDone")
            ?.observe(viewLifecycleOwner) { isPaid ->
                if (isPaid == true) {
                    binding?.btnPay?.isVisible = false

                    binding?.tvStatusValue?.text = "Active"
                    binding?.tvStatusValue?.let { CommonMethod.applyGradientText(it) }

                    findNavController()
                        .currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<Boolean>("bannerPaymentDone")
                }
            }
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("bannerData")
        if (!itemJson.isNullOrEmpty()) {
            bannerData = Gson().fromJson(itemJson, GetAllBannerResponse.Data::class.java)
            setData()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Details"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnPay.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("bannerData", Gson().toJson(bannerData))
                    putString("from", "banner")
                }
                findNavController().navigate(R.id.action_bannerDetailFragment_to_paymentMethodFragment,bundle)
            }
        }
    }

    private fun setData() {
        binding?.apply {
            tvRequestId.text = bannerData?.orderId
            tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(
                bannerData?.createdAt.toString(),
                outputFormat = "dd/MM/yyyy 'at' hh:mm a"
            )

            when (bannerData?.adminVerify) {
                "0" -> {
                    tvStatusValue.text = "Pending"
                    tvStatusValue.setTextColor(Color.parseColor("#E28F12"))
                    btnPay.isVisible = false
                }

                "1" -> {
                    when (bannerData?.paymentStatus) {
                        "0" -> {
                            tvStatusValue.text = "Confirmed"
                            tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                            btnPay.isVisible = true
                        }

                        "1" -> {
                            tvStatusValue.text = "Active"
                            CommonMethod.applyGradientText(tvStatusValue)
                            btnPay.isVisible = false
                        }

                        "2" -> {
                            tvStatusValue.text = "Rejected"
                            tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                            btnPay.isVisible = false
                        }
                    }
                }
                "2" -> {
                    tvStatusValue.text = "Rejected"
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                    btnPay.isVisible = false
                    if (bannerData?.cancelledReason!=null) {
                        tvReason.isVisible = true
                        tvRejectedReason.isVisible = true
                        tvRejectedReason.text = bannerData?.cancelledReason
                    }
                }
            }

            when (bannerData?.bannerPosition) {
                "1" -> {
                    tvBannerPosition.text = "Home Top"
                    tvCategory.isVisible = false
                    tvCategoryValue.isVisible = false
                    tvSubCategory.isVisible = false
                    tvSubCategoryValue.isVisible = false
                }

                "2" -> {
                    tvBannerPosition.text = "Home Bottom"
                    tvCategory.isVisible = false
                    tvCategoryValue.isVisible = false
                    tvSubCategory.isVisible = false
                    tvSubCategoryValue.isVisible = false
                }

                "3" -> {
                    tvBannerPosition.text = "Category"
                    tvCategoryValue.text = bannerData?.categoryDetails?.name
                    tvSubCategoryValue.text = bannerData?.subcategoryDetails?.nameEnglish
                    tvCategory.isVisible = true
                    tvCategoryValue.isVisible = true
                    tvSubCategory.isVisible = true
                    tvSubCategoryValue.isVisible = true
                }
            }
            tvFromDate.text = CommonMethod.customizeChangeDateFormat(
                bannerData?.startDate.toString(),
                "yyyy-MM-dd",
                "dd/MM/yyyy"
            )
            tvToDate.text = CommonMethod.customizeChangeDateFormat(
                bannerData?.endDate.toString(),
                "yyyy-MM-dd",
                "dd/MM/yyyy"
            )
            tvTotalDays.text = bannerData?.totalDays
            tvTotalPrice.text = "${CommonMethod.formatPrice(bannerData?.price ?: 0.0)} KD"

            ivBanner.load(bannerData?.uploadBanner) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }
    }

    override fun observe() {
    }
}