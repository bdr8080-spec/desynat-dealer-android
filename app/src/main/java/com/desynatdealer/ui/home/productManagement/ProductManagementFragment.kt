package com.desynatdealer.ui.home.productManagement

import android.app.Dialog
import android.graphics.Color
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
import com.desynatdealer.adapter.ProductAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentProductManagementBinding
import com.desynatdealer.databinding.ProductFilterDialogBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSellerBrandResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.desynatdealer.adapter.FilterBrandAdapter
import com.desynatdealer.adapter.FilterCategoryAdapter
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.SharedPreference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductManagementFragment : BaseFragment() {
    private var binding : FragmentProductManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var productAdapter: ProductAdapter?=null
    private var productData :GetAllProductResponse.Data?=null
    private var productList = ArrayList<GetAllProductResponse.Data.AllProductData>()
    var type = 0 // 0 all product 1 active product 2 inactive product

    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var colorList = ArrayList<GetColorResponse.Data>()
    private var brandList = ArrayList<GetSellerBrandResponse.Data>()
    private var sizeList = ArrayList<GetSizeResponse.Data>()

    var selectedPos = -1

    private var search: Job? = null
    private var lastQuery: String? = null

    private var filterBrandAdapter: FilterBrandAdapter? = null
    private var filterCategoryAdapter: FilterCategoryAdapter? = null
    private var selectedBrandList = ArrayList<GetSellerBrandResponse.Data>()
    private var selectedCategory: GetCategoryResponse.Data?=null
    private var isFromSwipe = false
    var canAction = true
    var canView = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentProductManagementBinding.inflate(layoutInflater, container,false)
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
            if (SharedPreference.get(requireActivity()).userType == "1") {
                canAction = CommonMethod.getPermission(requireActivity(),"seller.products")?.canAction == 1
                canView = CommonMethod.getPermission(requireActivity(),"seller.products")?.canView == 1
                ivAdd.isVisible = canAction
            }
            ivBack.isVisible = SharedPreference.get(requireActivity()).userType=="1"

            productAdapter = ProductAdapter(requireActivity(),productList,colorList,object:ProductAdapter.ProductClick{
                override fun onProductClick(position: Int, type: Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("productData", Gson().toJson(productList[position]))
                            }
                            findNavController().navigate(R.id.action_productManagementFragment_to_productDetailFragment,bundle)
                        }
                        2->{
                            selectedPos = position
                            updateProductStatus()
                        }
                    }
                }
            })
            rvProduct.adapter = productAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getAllProduct()
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
                        getAllProduct()
                    }
                }
            })
        }
        getProductInfo()
        getAllProduct()
    }

    override fun initControl() {
        binding?.apply {
            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_productManagementFragment_to_addProductFragment)
            }

            ivFilter.setOnClickListener {
                productFilterDialog()
            }

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvAll.setOnClickListener {
                type=0
                getAllProduct()
                tvAll.setTextColor(Color.parseColor("#875C3A"))
                viewAll.setBackgroundColor(Color.parseColor("#875C3A"))
                tvActive.setTextColor(Color.parseColor("#B3B3B3"))
                viewActive.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvInactive.setTextColor(Color.parseColor("#B3B3B3"))
                viewInactive.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            tvActive.setOnClickListener {
                type=1
                getAllProduct()
                tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvActive.setTextColor(Color.parseColor("#875C3A"))
                viewActive.setBackgroundColor(Color.parseColor("#875C3A"))
                tvInactive.setTextColor(Color.parseColor("#B3B3B3"))
                viewInactive.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            tvInactive.setOnClickListener {
                type=2
                getAllProduct()
                tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvActive.setTextColor(Color.parseColor("#B3B3B3"))
                viewActive.setBackgroundColor(Color.parseColor("#B3B3B3"))
                tvInactive.setTextColor(Color.parseColor("#875C3A"))
                viewInactive.setBackgroundColor(Color.parseColor("#875C3A"))
            }
        }
    }


    override fun observe() {
        viewModel.getProductResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    if (!isFromSwipe){
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isFromSwipe){
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }
                    productData = result.data?.data
                    productList.clear()
                    when(type){
                        0-> result.data?.data?.allProductData?.let { productList.addAll(it) }
                        1-> result.data?.data?.activeProduct?.let { productList.addAll(it) }
                        2-> result.data?.data?.inactiveProduct?.let { productList.addAll(it) }
                    }

                    checkData()
                    productAdapter?.notifyDataSetChanged()
                    viewModel.getProductResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getProductResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.categoryResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {

                }
                is ApiResult.Success -> {
                    categoryList.clear()
                    result.data?.data?.let { categoryList.addAll(it) }
                    viewModel.categoryList.value = categoryList
                    viewModel.categoryResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.categoryResponse.value = null
                }
                else ->{

                }
            }
        }


        viewModel.updateProductStatusResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    result.data?.message?.let { showToast(it) }

                    if (result.data?.data?.productActiveStatus==1){
                        productData?.activeProductCount = productData?.activeProductCount?.plus(1)
                            productData?.inactiveProductCount = productData?.inactiveProductCount?.minus(1)
                    }
                    else{
                        productData?.inactiveProductCount = productData?.inactiveProductCount?.plus(1)
                        productData?.activeProductCount = productData?.activeProductCount?.minus(1)
                    }

                    if (type==0){
                    productList[selectedPos].productActiveStatus = result.data?.data?.productActiveStatus!!
                    productAdapter?.notifyItemChanged(selectedPos)
                        selectedPos = -1
                        }
                    else{
                        productList.removeAt(selectedPos)
                        productAdapter?.notifyItemRemoved(selectedPos)
                        productAdapter?.notifyItemRangeChanged(selectedPos, productList.size - selectedPos)
                        selectedPos = -1
                    }
                    checkData()
                    viewModel.updateProductStatusResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateProductStatusResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.sizeResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    sizeList.clear()
                    result.data?.data?.let { sizeList.addAll(it) }
                    viewModel.sizeList.value = sizeList
                    viewModel.sizeResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.sizeResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.brandResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    brandList.clear()
                    result.data?.data?.let { brandList.addAll(it) }
                    viewModel.brandList.value = brandList
                    viewModel.brandResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.brandResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.colorResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    colorList.clear()
                    result.data?.data?.let { colorList.addAll(it) }
                    viewModel.colorList.value = colorList
                    productAdapter?.notifyDataSetChanged()
                    viewModel.colorResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.colorResponse.value = null
                }
                else ->{

                }
            }
        }
    }

    private fun checkData() {
        binding?.apply {
            tvNoDataFound.isVisible = productList.isEmpty()

            tvAll.text = "${getString(R.string.all)}(${productData?.allProductCount})"
            tvActive.text = "${getString(R.string.active)}(${productData?.activeProductCount})"
            tvInactive.text = "${getString(R.string.inactive)}(${productData?.inactiveProductCount})"
        }
    }

    private fun getAllProduct(){
        val map = HashMap<String,Any>()
        map["type"] = type
        if (lastQuery!=null) {
            map["search"] = lastQuery.toString()
        }
        if (selectedCategory!=null) {
            map["cargory_filter"] = arrayListOf(selectedCategory?.id.toString())
        }

        if (selectedBrandList.isNotEmpty()) {
            map["brand_filter"] = selectedBrandList.map { it.id }
        }
        viewModel.getAllProduct(map)
    }

    private fun getProductInfo() {
        viewModel.getCategory()
        viewModel.getColor()
        viewModel.getSize()
        viewModel.getBrand()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun updateProductStatus(){
        val map = HashMap<String,Any>()
        map["product_id"] = productList[selectedPos].id.toString()
        viewModel.updateProductStatus(map)
    }

    private fun productFilterDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ProductFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            brandList.forEach { it.isSelected = false }

            if (selectedBrandList.isNotEmpty()) {
                brandList.forEach { brand ->
                    brand.isSelected = selectedBrandList.any { it.id == brand.id }
                }
            }

            filterBrandAdapter = FilterBrandAdapter(requireActivity(), brandList,object :
                FilterBrandAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvBrand.adapter = filterBrandAdapter

            filterCategoryAdapter = FilterCategoryAdapter(requireActivity(), categoryList,object :FilterCategoryAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    selectedCategory = categoryList[position]
                }

            })

            rvCategory.adapter = filterCategoryAdapter

            filterCategoryAdapter?.setSelectedCategoryId(selectedCategory?.id)

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            tvClear.setOnClickListener {
                selectedCategory = null
                selectedBrandList.clear()
                getAllProduct()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                selectedBrandList.clear()
                for(i in 0 until brandList.size){
                    if (brandList[i].isSelected) {
                        selectedBrandList.add(brandList[i])
                    }
                }
                getAllProduct()
                dialog.dismiss()
            }

        }

        dialog.show()
    }


}