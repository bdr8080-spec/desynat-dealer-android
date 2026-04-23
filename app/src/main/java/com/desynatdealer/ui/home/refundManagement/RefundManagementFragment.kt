package com.desynatdealer.ui.home.refundManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.desynatdealer.ui.home.refundManagement.response.GetRefundOrderResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
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
    private var refundList = ArrayList<GetRefundOrderResponse.Data.Data>()
    private var refundAdapter: RefundAdapter?=null
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false

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

                    currentPage = result.data?.data?.currentPage?:0
                    totalItem = result.data?.data?.total?:0

                    lastPage =  result.data?.data?.lastPage?:0

                    val oldSize = refundList.size
                    if (!pagination){
                        refundList.clear()
                    }
                    val newItems =  result.data?.data?.data ?: emptyList()

                    refundList.addAll(newItems)
                    checkData()

                    if (pagination){

                        result.data?.data?.data?.size?.let {
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

    private fun getRefund(){
        val map = HashMap<String, Any>()
        map["limit"] = 10
        map["page"] = currentPage
        viewModel.getRefundOrder(map)
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = refundList.isEmpty()
    }

}