package com.desynatdealer.ui.home.profile.story_management

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.StoryManagementAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.BannerFilterDialogBinding
import com.desynatdealer.databinding.FragmentStoryManagementBinding
import com.desynatdealer.ui.home.profile.PromoteStoryPaymentFragment
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class StoryManagementFragment : BaseFragment() {

    private var _binding: FragmentStoryManagementBinding? = null
    private val mBinding get() = _binding!!


    private val viewModel: HomeViewModel by activityViewModels()

    private var search: Job? = null
    private var lastQuery: String? = null

    private var storyList = ArrayList<GetStoryResponse.Data>()
    private var storyAdapter: StoryManagementAdapter? = null
    private var selectedPos = -1
    private var bannerFilter: String? = null
    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null
    private var isFromSwipe = false
    var canAction = true
    var canView = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryManagementBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        _binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.story_management)

            if (SharedPreference.get(requireActivity()).userType == "1") {
                canAction = CommonMethod.getPermission(
                    requireActivity(), "seller.show_banner"
                )?.canAction == 1
                canView = CommonMethod.getPermission(
                    requireActivity(), "seller.show_banner"
                )?.canView == 1
                ivAdd.isVisible = canAction
            }
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600.milliseconds)
                        getStories()
                    }
                }
            })

            storyList.clear()
            storyAdapter = StoryManagementAdapter(
                requireActivity(), storyList, object : StoryManagementAdapter.OnClick {
                    override fun onItemClick(position: Int, type: Int) {
                        when (type) {
                            1 -> {
                                val bundle = Bundle().apply {
                                    putString("storyData", Gson().toJson(storyList[position]))
                                }
                                findNavController().navigate(
                                    R.id.action_storyManagementFragment_to_bannerDetail, bundle
                                )
                            }

                            2 -> {
                                val bundle = Bundle().apply {
                                    putString("storyData", Gson().toJson(storyList[position]))
                                    putString("from", "story")
                                }
                                findNavController().navigate(
                                    R.id.action_storyManagementFragment_to_paymentMethod, bundle
                                )
                            }

                        }
                    }
                })
            rvStory.adapter = storyAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getStories()
            }

            getStories()

        }
    }

    private fun getStories() {
        val map = HashMap<String, Any>()
        if (lastQuery != null && lastQuery != "") {
            map["search"] = lastQuery.toString()
        }

        if (bannerFilter != null && bannerFilter != "") {
            map["filter"] = bannerFilter.toString()
        }

        if (fromDateText != null && fromDateText != "") {
            map["from_date"] =
                CommonMethod.formatDate(fromDateText.toString(), "dd/MM/yyyy", "yyyy-MM-dd")

        }
        if (toDateText != null && toDateText != "") {
            map["to_date"] =
                CommonMethod.formatDate(toDateText.toString(), "dd/MM/yyyy", "yyyy-MM-dd")

        }

        viewModel.getSellerPromotionalStories(map)
    }

    override fun initControl() {
        _binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                PromoteStoryPaymentFragment.come_form = "onlyPaidStory"
                findNavController().navigate(R.id.action_storyManagementFragment_to_addStoryFragment)
            }

            ivFilter.setOnClickListener {
                bannerFilterDialog()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observe() {
        viewModel.getSellerPromotionalStoriesResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    if (!isFromSwipe) {
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isFromSwipe) {
                        _binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }
                    storyList.clear()

                    result.data?.data?.let {
                        storyList.addAll(it)
                    }
                    storyAdapter?.notifyDataSetChanged()
                    checkData()
                    viewModel.getSellerPromotionalStoriesResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isFromSwipe) {
                        _binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getSellerPromotionalStoriesResponse.value = null
                }

                else -> {}
            }
        }



    }

    private fun checkData() {
        _binding?.tvNoDataFound?.isVisible = storyList.isEmpty()
        _binding?.tvNoDataFound?.text = getString(R.string.no_story_found)
    }

    private fun bannerFilterDialog() {
        var filter: String? = null
        var tempFromTimestamp: Long? = null
        var tempToTimestamp: Long? = null
        var tempFromDateText: String? = null
        var tempToDateText: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = BannerFilterDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            // For stories, maybe some filters don't apply. Hiding Active and Expired for now if they don't map.
            // But user said "same as banner", so I'll keep them and just map the ones that make sense.

            if (bannerFilter != null) {
                filter = bannerFilter.toString()
                when (bannerFilter) {
                    "1" -> rbActive.isChecked = true
                    "2" -> rbConfirmed.isChecked = true
                    "3" -> rbPending.isChecked = true
                    "4" -> rbExpired.isChecked = true
                    "5" -> rbRejected.isChecked = true
                }
            }

            tempFromTimestamp = fromTimestamp
            tempToTimestamp = toTimestamp
            tempFromDateText = fromDateText
            tempToDateText = toDateText

            tempFromDateText?.let { etFrom.text = it }
            tempToDateText?.let { etTo.text = it }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbActive -> "1"
                    R.id.rbConfirmed -> "2"
                    R.id.rbPending -> "3"
                    R.id.rbExpired -> "4"
                    R.id.rbRejected -> "5"
                    else -> null
                }
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    tempFromTimestamp = timestamp
                    tempFromDateText = date
                    etFrom.text = date

                    tempToTimestamp = null
                    tempToDateText = null
                    etTo.text = ""
                }
            }

            etTo.setOnClickListener {
                if (tempFromTimestamp == null) {
                    showToast(getString(R.string.please_select_from_date_first))
                    return@setOnClickListener
                }

                openCustomDatePicker(
                    requireActivity(), minDate = tempFromTimestamp
                ) { date, timestamp ->
                    tempToTimestamp = timestamp
                    tempToDateText = date
                    etTo.text = date
                }
            }

            tvClear.setOnClickListener {
                filter = ""
                bannerFilter = null

                fromTimestamp = null
                toTimestamp = null
                fromDateText = null
                toDateText = null

                tempFromTimestamp = null
                tempToTimestamp = null
                tempFromDateText = null
                tempToDateText = null

                getStories()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                bannerFilter = filter

                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText

                getStories()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}
