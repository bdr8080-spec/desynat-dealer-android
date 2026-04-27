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
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.DealAdapter
import com.desynatdealer.adapter.DealProductAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentDealDetailBinding
import com.desynatdealer.ui.home.dealManagement.response.GetAllDealResponse
import com.desynatdealer.ui.home.dealManagement.response.GetDealDetailResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DealDetailFragment : BaseFragment() {
    private var binding : FragmentDealDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var dealType = "1"
    private var dealData : GetAllDealResponse.Data.ListedDeal?=null
    private var dealDetailData :GetDealDetailResponse.Data?=null
    private var dealProductAdapter: DealProductAdapter?=null
    private var productList = ArrayList<GetDealDetailResponse.Data.RemainingProduct>()
    private var remainingProductList = ArrayList<GetDealDetailResponse.Data.RemainingProduct>()
    private var addedProductList = ArrayList<GetDealDetailResponse.Data.AddedProduct>()
    private var isProductAdded = false
    private var isProductRemove = false
    private var selectedProduct = ArrayList<String>()
    private var search: Job? = null
    private var lastQuery: String? = null

    var canAction = true
    var canView = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDealDetailBinding.inflate(layoutInflater, container,false)
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
            val itemJson = arguments?.getString("dealData")
            dealType = arguments?.getString("dealType").toString()
            if (!itemJson.isNullOrEmpty()) {
                dealData = Gson().fromJson(itemJson, GetAllDealResponse.Data.ListedDeal::class.java)
                getDealDetail()
            }
            if (SharedPreference.get(requireActivity()).userType == "1") {
                canAction = CommonMethod.getPermission(requireActivity(),"seller.deal_management")?.canAction == 1
                canView = CommonMethod.getPermission(requireActivity(),"seller.deal_management")?.canView == 1
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
                      //  delay(600)

                        dealProductAdapter?.filter?.filter(lastQuery)

                    }
                }
            })

            when(dealType){
                "1"->{
                    toolbar.tvToolbar.text = "Listed Deals"

                }
                "2"->{
                    toolbar.tvToolbar.text = "Enrolled Deals"
                }
                "3"->{
                    toolbar.tvToolbar.text = "Expired Deals"
                }
            }

            dealProductAdapter = DealProductAdapter(requireActivity(),productList,dealType,object :DealProductAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    when(type){
                        2->{
                            removeProductInDeal(position)
                        }
                    }
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

    private fun setData() {
        binding?.apply {
            ivDeal.load(dealDetailData?.dealData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvDiscountValue.text = "${dealDetailData?.dealData?.discount}%"
            tvDetail.text = dealDetailData?.dealData?.nameEnglish
            tvCategoryValue.text = dealDetailData?.dealData?.categoryDetails?.name
            tvSubCategoryValue.text = dealDetailData?.dealData?.subCategoryDetails?.joinToString(", ") { it.nameEnglish }
            tvStartsOnValue.text = CommonMethod.customizeChangeDateFormat(dealDetailData?.dealData?.from.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            tvDurationValue.text = CommonMethod.customizeChangeDateFormat(dealDetailData?.dealData?.from.toString(),"yyyy-MM-dd","dd/MM/yyyy")+" - "+CommonMethod.customizeChangeDateFormat(dealDetailData?.dealData?.to.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            productList.clear()
            addedProductList.clear()
            remainingProductList.clear()
            when(dealType){
                "1" ->{
                    dealDetailData?.addedProducts?.let { addedProductList.addAll(it) }
                    dealDetailData?.remainingProduct?.let { productList.addAll(it) }
                    dealDetailData?.remainingProduct?.let { remainingProductList.addAll(it) }
                    dealProductAdapter?.notifyDataSetChanged()
                     if (remainingProductList.isNotEmpty()){
                        dealProductAdapter?.setDealType(dealType)
                         dealProductAdapter?.notifyDataSetChanged()
                         btnAdd.alpha = 0.5f
                         btnAdd.isClickable = false
                         btnAdd.isVisible = canAction
                        btnAddMore.isVisible = false
                    }

                    else if (addedProductList.isNotEmpty()){
                         if (dealDetailData?.addedProducts?.size!=0){
                             for(i in 0 until dealDetailData?.addedProducts?.size!!){
                                 dealDetailData?.addedProducts?.let { productList.add(it[i].productDetails) }
                             }
                         }
                        dealProductAdapter?.setDealType("2")
                         dealProductAdapter?.notifyDataSetChanged()

                         btnAdd.isVisible = false
                        if (remainingProductList.isNotEmpty()){
                            btnAddMore.isVisible = canAction
                        }
                        else{
                            btnAddMore.isVisible = false
                        }
                    }

                    else{
                         btnAdd.alpha = 0.5f
                         btnAdd.isClickable = false
                         btnAdd.isVisible = false
                        btnAddMore.isVisible = false
                    }
                    dealProductAdapter?.updateList(productList)
                    clRevenue.isVisible = false
                }
                "2" -> {
                    dealDetailData?.addedProducts?.let { addedProductList.addAll(it) }
                    dealDetailData?.remainingProduct?.let { remainingProductList.addAll(it) }

                    dealDetailData?.addedProducts
                        ?.map { it.productDetails }
                        ?.let { productList.addAll(it) }

                    if (addedProductList.isNotEmpty()) {
                        dealProductAdapter?.setDealType("2")
                        btnAdd.isVisible = false
                        btnAddMore.isVisible = remainingProductList.isNotEmpty()&&canAction
                    }
                    else if (remainingProductList.isNotEmpty()) {
                        productList.addAll(remainingProductList)
                        dealProductAdapter?.setDealType("1")
                        btnAdd.alpha = 0.5f
                        btnAdd.isClickable = false
                        btnAdd.isVisible = canAction
                        btnAddMore.isVisible = false
                    }
                    else {
                        btnAdd.isVisible = false
                        btnAddMore.isVisible = false
                    }
                    dealProductAdapter?.updateList(productList)
                    dealProductAdapter?.notifyDataSetChanged()
                    clRevenue.isVisible = false
                }

                "3" ->{
                    dealDetailData?.addedProducts?.let { addedProductList.addAll(it) }
                    if (dealDetailData?.addedProducts?.size!=0){
                        for(i in 0 until dealDetailData?.addedProducts?.size!!){
                            dealDetailData?.addedProducts?.let { productList.add(it[i].productDetails) }
                        }
                    }
                    dealProductAdapter?.updateList(productList)
                    dealProductAdapter?.notifyDataSetChanged()
                    btnAddMore.isVisible = false
                    btnAdd.isVisible = false
                    clRevenue.isVisible = true
                }
            }

            tvNoDataFound.isVisible = productList.isEmpty()

        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDeal.setOnClickListener {
                val list = ArrayList<String>()
                list.add(dealDetailData?.dealData?.image?:"")
                CommonMethod.imagePreviewDialog(requireActivity(), imageList =list )
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
                    showToast("Please select product")
                }

            }

            btnAddMore.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("productList", Gson().toJson(remainingProductList))
                    putString("dealId", dealData?.id.toString())
                }
                findNavController().navigate(R.id.action_dealDetailFragment_to_selectDealProductFragment,bundle)
            }
        }
    }

    override fun observe() {
        viewModel.getDealDetailResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isProductAdded){
                        CommonMethod.showSuccessDialog(requireActivity(),"Products added to deals successfully !")
                        isProductAdded = false
                    }
                    else if (isProductRemove){
                        CommonMethod.showSuccessDialog(requireActivity(),"Product remove to deals successfully !")
                        isProductRemove = false
                    }
                    dealDetailData = result.data?.data
                    setData()
                    viewModel.getDealDetailResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getDealDetailResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.addProductInDealResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    if (dealType=="2"){
                    isProductAdded = true
                    getDealDetail()
                    }
                    else{
                        findNavController().previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("deal_type", "2")
                        CommonMethod.showSuccessDialog(requireActivity(),"Products added to deals successfully !"){
                            findNavController().popBackStack()
                        }
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

        viewModel.removeProductInDealResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    isProductRemove = true
                    getDealDetail()
                    viewModel.removeProductInDealResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.removeProductInDealResponse.value = null
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

                    dealDetailData?.remainingProduct = result?.data?.data?: emptyList()
                    setData()
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

    private fun getDealDetail() {
        val map = HashMap<String,Any>()
        map["deal_id"] = dealData?.id.toString()
        viewModel.getDealDetail(map)
    }

    private fun addProductInDeal() {
        val map = HashMap<String,Any>()
        map["deal_id"] = dealData?.id.toString()
        map["product_ids"] = selectedProduct
        viewModel.addProductInDeal(map)
    }

    private fun removeProductInDeal(position:Int) {
        val map = HashMap<String,Any>()
        map["deal_id"] = dealData?.id.toString()
        map["id"] = addedProductList[position].id
        viewModel.removeProductInDeal(map)
    }

    private fun getDealProduct() {
        val map = HashMap<String,Any>()
        map["deal_id"] = dealData?.id.toString()
        if (lastQuery!=null){
            map["search"] = lastQuery.toString()
        }
        viewModel.showProductInDealCategorySubcategory(map)
    }

}