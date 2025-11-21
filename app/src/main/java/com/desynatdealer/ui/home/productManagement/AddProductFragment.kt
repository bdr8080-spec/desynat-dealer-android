package com.desynatdealer.ui.home.productManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.VariantAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddProductBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSellerBrandResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : BaseFragment() {
    private var binding : FragmentAddProductBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var colorList = ArrayList<GetColorResponse.Data>()
    private var brandList = ArrayList<GetSellerBrandResponse.Data>()
    private var sizeList = ArrayList<GetSizeResponse.Data>()
    private var variantList = ArrayList<GetAllProductResponse.Data.AllProductData.ProductVariant>()
    private var variantAdapter :VariantAdapter? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentAddProductBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogUtils.getInstance().hideProgress()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()

        categoryList = viewModel.categoryList.value as ArrayList<GetCategoryResponse.Data>
        colorList = viewModel.colorList.value as ArrayList<GetColorResponse.Data>
        sizeList = viewModel.sizeList.value as ArrayList<GetSizeResponse.Data>
        brandList = viewModel.brandList.value as ArrayList<GetSellerBrandResponse.Data>


            if (categoryList.isNotEmpty()){
                for(i in 0 until categoryList.size){
                    if (categoryList[i].name=="Women"){
                        getSubCategory(categoryList[i].id.toString())
                    }
                }
            }

        binding?.apply {

            toolbar.tvToolbar.text = "Add New Product"

            variantList.clear()

        variantAdapter = VariantAdapter(requireActivity(),variantList,colorList,sizeList,false,object:VariantAdapter.VariantClick{
            override fun onVariantClick(position: Int, type: Int) {

            }

        })
        rvVariant.adapter = variantAdapter

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAddVariant.setOnClickListener {

            }

            btnAdd.setOnClickListener {

            }

            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSizeChart.setOnClickListener {

            }

        }
    }

    override fun observe() {
        viewModel.categoryResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {

                }
                is ApiResult.Success -> {
                    categoryList.clear()
                    result.data?.data?.let { categoryList.addAll(it) }
                    if (categoryList.isNotEmpty()){
                        for(i in 0 until categoryList.size){
                            if (categoryList[i].name=="Women"){
                                getSubCategory(categoryList[i].id.toString())
                            }
                        }
                    }
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

        viewModel.subCategoryResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    subCategoryList.clear()
                    result.data?.data?.let { subCategoryList.addAll(it) }
                    viewModel.subCategoryResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.subCategoryResponse.value = null
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
                    categoryList.clear()
                    result.data?.data?.let { sizeList.addAll(it) }
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

    private fun getProductInfo() {
        viewModel.getCategory()
        viewModel.getColor()
        viewModel.getSize()
        viewModel.getBrand()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
    }

    private fun getSubCategory(categoryId:String) {
        viewModel.getSubCategory(categoryId)
    }

}