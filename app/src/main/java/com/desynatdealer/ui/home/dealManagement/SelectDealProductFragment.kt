package com.desynatdealer.ui.home.dealManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.DealProductAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentSelectDealProductBinding
import com.desynatdealer.ui.home.dealManagement.response.GetDealDetailResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectDealProductFragment : BaseFragment() {
    private var binding : FragmentSelectDealProductBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var dealProductAdapter: DealProductAdapter?=null
    private var productList = ArrayList<GetDealDetailResponse.Data.RemainingProduct>()
    private var dealId = ""
    private var selectedProduct = ArrayList<String>()
    private var search: Job? = null
    private var lastQuery: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectDealProductBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
         dealId = arguments?.getString("dealId").toString()
        val json = arguments?.getString("productList")

        productList = if (!json.isNullOrEmpty()) {
            val type = object :
                TypeToken<List<GetDealDetailResponse.Data.RemainingProduct>>() {}.type

            val list: List<GetDealDetailResponse.Data.RemainingProduct> =
                Gson().fromJson(json, type)

            ArrayList(list)
        } else {
            arrayListOf()
        }

        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.select_product)

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
                   //     delay(600)
                        dealProductAdapter?.filter?.filter(lastQuery)

                    }
                }
            })

            dealProductAdapter = DealProductAdapter(requireActivity(),productList,"1",object :
                DealProductAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {

                }

                override fun onSelectionChanged(isAnySelected: Boolean) {
                    if (isAnySelected){
                        btnAdd.alpha = 1f
                        btnAdd.isClickable = true
                    }
                    else{
                        btnAdd.alpha = 0.5f
                        btnAdd.isClickable = false
                    }
                }

            },
                object : DealProductAdapter.OnFilterResult {
                    override fun onResult(isEmpty: Boolean) {
                        binding?.tvNoDataFound?.isVisible = isEmpty
                    }
                })

            rvProduct.adapter = dealProductAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAdd.setOnClickListener {
                selectedProduct.clear()
                for (i in 0 until productList.size){
                    if (productList[i].isAdded){
                        selectedProduct.add(productList[i].id)
                    }
                }

                if (selectedProduct.size!=0){
                    addProductInDeal()
                }
                else{
                    showToast(getString(R.string.please_select_product))
                }
            }
        }
    }

    override fun observe() {
        viewModel.addProductInDealResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.products_added_to_deals_successfully)){
                        findNavController().popBackStack()
                    }
                    viewModel.addProductInDealResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addProductInDealResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.showProductInDealCategorySubcategoryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    productList.clear()
                    result.data?.data?.let { productList.addAll(it) }
                    dealProductAdapter?.notifyDataSetChanged()
                    checkData()
                    viewModel.showProductInDealCategorySubcategoryResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.showProductInDealCategorySubcategoryResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = productList.size!=0
    }

    private fun addProductInDeal() {
        val map = HashMap<String,Any>()
        map["deal_id"] = dealId
        map["product_ids"] = selectedProduct
        viewModel.addProductInDeal(map)
    }

    private fun getDealProduct() {
        val map = HashMap<String,Any>()
        map["deal_id"] = dealId
        if (lastQuery!=null){
            map["search"] = lastQuery.toString()
        }
        viewModel.showProductInDealCategorySubcategory(map)
    }

}