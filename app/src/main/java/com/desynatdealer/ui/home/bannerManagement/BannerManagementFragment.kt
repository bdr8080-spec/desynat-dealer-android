package com.desynatdealer.ui.home.bannerManagement

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
import com.desynatdealer.adapter.BannerAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.BannerFilterDialogBinding
import com.desynatdealer.databinding.FragmentBannerManagementBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class BannerManagementFragment : BaseFragment() {
    private var binding : FragmentBannerManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    private var search: Job? = null
    private var lastQuery: String? = null

    private var bannerList = ArrayList<GetAllBannerResponse.Data>()
    private var bannerAdapter : BannerAdapter?=null
    private var selectedPos = -1
    private var bannerFilter:String? = null
    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null
    private var isFromSwipe = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerManagementBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "Banner Management"

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        getBanner()
                        delay(600)

                    }
                }
            })

            bannerList.clear()
            bannerAdapter = BannerAdapter(requireActivity(),bannerList,object :BannerAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("bannerData", Gson().toJson(bannerList[position]))
                            }
                            findNavController().navigate(R.id.action_bannerManagementFragment_to_bannerDetailFragment,bundle)
                        }
                        2->{
                            val bundle = Bundle().apply {
                                putString("bannerData", Gson().toJson(bannerList[position]))
                                putString("from", "banner")
                            }
                            findNavController().navigate(R.id.action_bannerManagementFragment_to_paymentMethodFragment,bundle)
                        }
                    }
                }

            })
            rvBanner.adapter = bannerAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getBanner()
            }

           getBanner()

        }
    }

    private fun getBanner() {
        val map = HashMap<String,Any>()
        if (lastQuery!=null&&lastQuery!="") {
            map["search"] = lastQuery.toString()
        }
        if (bannerFilter!=null&&bannerFilter!="") {
            map["banner_filter"] = bannerFilter.toString()
        }
        if (fromDateText!=null&&fromDateText!="") {
            map["from"] =
                CommonMethod.formatDate(fromDateText.toString(),"dd/MM/yyyy","yyyy-MM-dd")

        }
        if (toDateText!=null&&toDateText!="") {
            map["to"] =
                CommonMethod.formatDate(toDateText.toString(),"dd/MM/yyyy","yyyy-MM-dd")

        }
        viewModel.getBanner(map)
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_bannerManagementFragment_to_addBannerFragment)
            }

            ivFilter.setOnClickListener {
                bannerFilterDialog()
            }
        }
    }

    override fun observe() {
        viewModel.getBannerResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    if (!isFromSwipe){
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isFromSwipe){
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }
                    bannerList.clear()

                    result.data?.data?.let {
                        bannerList.addAll(it)
                    }
                    bannerAdapter?.notifyDataSetChanged()
                    checkData()
                    viewModel.getBannerResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getBannerResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = bannerList.isEmpty()
    }

    private fun bannerFilterDialog() {
        var filter:String? = null
        var tempFromTimestamp: Long? = null
        var tempToTimestamp: Long? = null
        var tempFromDateText: String? = null
        var tempToDateText: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = BannerFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            if (bannerFilter!=null) {
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
                    showToast("Please select From date first")
                    return@setOnClickListener
                }

                openCustomDatePicker(
                    requireActivity(),
                    minDate = tempFromTimestamp
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

                getBanner()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                bannerFilter = filter

                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText

                if (bannerFilter==null &&fromDateText==null&&toDateText==null) {
                    showToast("Please select banner status")
                    return@setOnClickListener
                }
                else{
                    getBanner()
                    dialog.dismiss()
                }

            }

        }

        dialog.show()
    }

}