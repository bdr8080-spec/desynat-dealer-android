package com.desynatdealer.ui.home.refundManagement

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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.RefundAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentRefundManagementBinding
import com.desynatdealer.databinding.OrderFilterDialogBinding
import com.desynatdealer.databinding.RefundFilterDialogBinding
import com.desynatdealer.ui.home.refundManagement.response.GetRefundOrderResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class RefundManagementFragment : BaseFragment() {
    private var binding : FragmentRefundManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var search: Job? = null
    private var lastQuery: String? = null
    private var isFromSwipe = false
    private var refundList = ArrayList<GetRefundOrderResponse.Data.Orders.Data>()
    private var refundAdapter: RefundAdapter?=null
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false

    private var refundFilter: String? = null
    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRefundManagementBinding.inflate(layoutInflater, container,false)
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

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getRefund()
            }

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
                        delay(600)
                        getRefund()
                    }
                }
            })

            refundAdapter = RefundAdapter(requireActivity(),refundList, object :RefundAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {

                }

            })

            rvRefund.adapter = refundAdapter

            nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if ( !pagination && currentPage < lastPage) {
                            pagination = true
                            isFromSwipe = false
                            currentPage += 1
                            getRefund()
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }
        }

        getRefund()

    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivExport.setOnClickListener {

            }

            ivFilter.setOnClickListener {
                refundFilterDialog()
            }
        }
    }

    override fun observe() {
        viewModel.getRefundOrderResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    if (!isFromSwipe&&!pagination){
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    binding?.progressBar?.isVisible = false
                    if (isFromSwipe) {
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }

                    currentPage = result.data?.data?.orders?.currentPage?:0
                    totalItem = result.data?.data?.orders?.total?:0

                    lastPage =  result.data?.data?.orders?.lastPage?:0

                    val oldSize = refundList.size
                    if (!pagination){
                        refundList.clear()
                    }
                    val newItems =  result.data?.data?.orders?.data ?: emptyList()

                    refundList.addAll(newItems)
                    checkData()
                    setRevenueData(result.data?.data?.totalRefunds)

                    if (pagination){

                        result.data?.data?.orders?.data?.size?.let {
                            refundAdapter?.notifyItemRangeInserted(oldSize,
                                it
                            )
                        }
                    }
                    else{
                        refundAdapter?.notifyDataSetChanged()
                    }

                    pagination = false
                    isFromSwipe = false

                    viewModel.getRefundOrderResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getRefundOrderResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun setRevenueData(totalRefunds: String?) {
        binding?.ivAmount?.text  = "${CommonMethod.formatPrice(totalRefunds?.toDoubleOrNull()?:0.0)} KD"
    }

    private fun getRefund(){
        val map = HashMap<String, Any>()
        map["limit"] = 10
        map["page"] = currentPage
      //  map["country_name"] = currentPage
        if (refundFilter!=null){
            map["filter"] = refundFilter.toString()
        }
        if (fromDateText!=null){
            map["from_date"] = fromDateText.toString()
        }
        if (toDateText!=null){
            map["to_date"] = toDateText.toString()
        }

        viewModel.getRefundOrder(map)
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = refundList.isEmpty()
    }

    private fun refundFilterDialog() {
        var filter: String? = null
        var tempFromTimestamp: Long? = null
        var tempToTimestamp: Long? = null
        var tempFromDateText: String? = null
        var tempToDateText: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RefundFilterDialogBinding.inflate(LayoutInflater.from(context))
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


            if (refundFilter != null) {
                filter = refundFilter
                when (filter) {
                    "1" -> rbReturn.isChecked = true
                    "2" -> rbExchange.isChecked = true
                    "3" -> rbCancelled.isChecked = true
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
                    R.id.rbReturn -> "1"
                    R.id.rbExchange -> "2"
                    R.id.rbCancelled -> "3"
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
                refundFilter = null

                fromTimestamp = null
                toTimestamp = null
                fromDateText = null
                toDateText = null

                tempFromTimestamp = null
                tempToTimestamp = null
                tempFromDateText = null
                tempToDateText = null

                getRefund()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {

                refundFilter = filter

                fromTimestamp = tempFromTimestamp
                toTimestamp = tempToTimestamp
                fromDateText = tempFromDateText
                toDateText = tempToDateText

                if (refundFilter == null && fromDateText == null && toDateText == null) {
                    showToast(getString(R.string.please_select_status))
                    return@setOnClickListener
                } else {
                    getRefund()
                    dialog.dismiss()
                }

            }

        }

        dialog.show()
    }


}