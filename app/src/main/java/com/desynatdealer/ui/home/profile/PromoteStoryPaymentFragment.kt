package com.desynatdealer.ui.home.profile

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentPromoteStoryPaymentBinding
import com.desynatdealer.databinding.SendRequestPromotDialogBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoteStoryPaymentFragment : BaseFragment() {

    companion object {
        var come_form = "" //for story management
    }

    private var binding: FragmentPromoteStoryPaymentBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    private var file = ""
    private var thumbnail = ""
    private var mediaType = ""
    private var storyPrice = "0.0"
    private var selectedDateApi = "2026-07-17"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromoteStoryPaymentBinding.inflate(inflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.promote_story)

            file = arguments?.getString("file") ?: ""
            thumbnail = arguments?.getString("thumbnail") ?: ""
            mediaType = arguments?.getString("mediaType") ?: ""

            tvDate.text = "2026-07-17"
            tvTotalPrice.text = "0 KD"
        }
        getStoryPrice()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            clProfileStory.setOnClickListener {
                rbProfileStory.isChecked = true
                rbFeaturedStory.isChecked = false

                tvSelectDateTxt.isVisible = false
                clDate.isVisible = false
                clPrice.isVisible = false

                clProfileStory.setBackgroundResource(R.drawable.selected_card_bg)
                clFeaturedStory.setBackgroundResource(R.drawable.search_bg)
            }

            clFeaturedStory.setOnClickListener {
                rbFeaturedStory.isChecked = true
                rbProfileStory.isChecked = false

                tvSelectDateTxt.isVisible = true
                clDate.isVisible = true
                clPrice.isVisible = true

                clFeaturedStory.setBackgroundResource(R.drawable.selected_card_bg)
                clProfileStory.setBackgroundResource(R.drawable.search_bg)
            }

            clDate.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    tvDate.text = date
                    selectedDateApi = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                }
            }

            if (come_form == "onlyPaidStory") {
                clProfileStory.isVisible = false
                rbFeaturedStory.setOnClickListener { clFeaturedStory.performClick() }
            } else {
                clProfileStory.isVisible = true
                rbProfileStory.setOnClickListener { clProfileStory.performClick() }
            }


            btnSendRequest.setOnClickListener {
                addStoryApi()
            }
        }
    }

    private fun addStoryApi() {
        val map = HashMap<String, Any>()
        map["file"] = file
        if (mediaType != "image" && thumbnail.isNotEmpty()) {
            map["thumbnail"] = thumbnail
        }

        if (binding?.rbFeaturedStory?.isChecked == true) {
            map["is_promotional"] = 1
            map["start_date"] = selectedDateApi
            map["end_date"] = selectedDateApi
            map["total_days"] = 1
            map["amount"] = storyPrice
        } else {
            map["is_promotional"] = 0
        }

        viewModel.addStory(map)
    }

    override fun observe() {
        viewModel.getStoryPriceResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    storyPrice = result.data?.data?.per_day_price ?: "0.0"
                    binding?.tvTotalPrice?.text = "$storyPrice KD"
                    viewModel.getStoryPriceResponse.value = null
                }

                is ApiResult.Error -> {
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getStoryPriceResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.addStoryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (binding?.rbFeaturedStory?.isChecked == true) {
                        showSuccessDialog()
                    } else {
                        if (come_form == "onlyPaidStory") {
                            if (!findNavController().popBackStack(R.id.viewProfile, false)) {
                                findNavController().navigate(R.id.viewProfile)
                            }
                        } else {
                            findNavController().popBackStack(R.id.viewProfile, false)
                        }
                    }
                    viewModel.addStoryResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addStoryResponse.value = null
                }

                else -> {}
            }
        }


    }

    private fun showSuccessDialog() {
        val dialogBinding = SendRequestPromotDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialog.setCancelable(false)
        dialog.show()

        // Using Handler to dismiss and navigate
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded && dialog.isShowing) {
                dialog.dismiss()
                if (!findNavController().popBackStack(R.id.viewProfile, false)) {
                    findNavController().popBackStack(R.id.storyManagementFragment, false)
                }
            }
        }, 2500)
    }

    private fun getStoryPrice() {
        val map = HashMap<String, Any>()
        map["country_id"] = 1
        map["no_of_days"] = 1
        viewModel.getStoryPrice(map)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
