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
import com.desynatdealer.adapter.VariantColorAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentEditProductBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSellerBrandResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProductFragment : BaseFragment() {
    private var binding : FragmentEditProductBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var colorList = ArrayList<GetColorResponse.Data>()
    private var brandList = ArrayList<GetSellerBrandResponse.Data>()
    private var sizeList = ArrayList<GetSizeResponse.Data>()
    private var variantList = ArrayList<GetAllProductResponse.Data.AllProductData.ProductVariant>()
    private var selectedVariantList = ArrayList<GetAllProductResponse.Data.AllProductData.ProductVariant>()
    private var variantAdapter :VariantAdapter? =null
    private var variantColorAdapter : VariantColorAdapter? =null
    private var productData :GetAllProductResponse.Data.AllProductData?=null
    var selectedColorId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentEditProductBinding.inflate(layoutInflater, container,false)
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

        val itemJson = arguments?.getString("productData")
        if (!itemJson.isNullOrEmpty()) {
            productData = Gson().fromJson(itemJson, GetAllProductResponse.Data.AllProductData::class.java)
            setData()
            getSubCategory(productData?.categoryId.toString())
            variantList.clear()
            productData?.productVariants?.let { variantList.addAll(it) }
        }

        variantAdapter = VariantAdapter(requireActivity(),selectedVariantList,colorList,sizeList,true,object:VariantAdapter.VariantClick{
            override fun onVariantClick(position: Int, type: Int) {

            }

        })
        binding?.rvVariant?.adapter = variantAdapter

        selectedColorId = productData?.productVariants?.get(0)?.colorId.toString()
        getVariantList()

        variantColorAdapter = VariantColorAdapter(requireActivity(),variantList,colorList,object:VariantColorAdapter.OnClick{
            override fun onClickItem(position: Int) {
                selectedColorId = productData?.productVariants?.get(position)?.colorId.toString()
                getVariantList()
            }

        })
        binding?.rvVariantColor?.adapter = variantColorAdapter

    }

    private fun getVariantList() {
        val productVariant = productData?.productVariants
        selectedVariantList.clear()
        if (productVariant != null) {
            for(i in 0 until productVariant.size){
                if (productVariant[i].colorId==selectedColorId){
                    selectedVariantList.add(productVariant[i])
                }

            }
        }
        variantAdapter?.notifyDataSetChanged()
    }

    private fun setData() {
        binding?.apply {
            toolbar.tvToolbar.text = "Edit"

            when(productData?.category?.name){
                "Women" -> cbWomen.isChecked =true
                "Men" -> cbMen.isChecked =true
                "Kids" -> cbKids.isChecked =true
            }

            etProductName.setText(productData?.productNameEn)
            etProductNameArabic.setText(productData?.productNameAr)

            etSubCategory.setText(productData?.subcategory?.nameEnglish)
            etBrand.setText(productData?.brand?.nameEnglish)

            etDescription.setText(productData?.descriptionEn)
            etDescriptionArabic.setText(productData?.descriptionAr)

            etMinCartQuantity.setText(productData?.minCardQuantity)
            etMaxCartQuantity.setText(productData?.maxCardQuantity)

            setYesNoCheckbox(productData?.productReturn,cbReturnableYes,cbReturnableNo)
            etReturnableDay.setText(productData?.returnDays)

            setYesNoCheckbox(productData?.productExchangable,cbExchangeYes,cbExchangeNo)
            etExchangeableDay.setText(productData?.exchangableDays)

            setYesNoCheckbox(productData?.reviewRating,cbReviewRatingYes,cbReviewRatingeNo)
            setYesNoCheckbox(productData?.affiliatedProduct,cbAffiliatedProductYes,cbAffiliatedProductNo)
            setYesNoCheckbox(productData?.customizedProduct,cbCustomizedProductYes,cbCustomizedProductNo)
            setYesNoCheckbox(productData?.readyToShip,cbReadyToShipYes,cbReadyToShipNo)
            setYesNoCheckbox(productData?.cashOnDelivery,cbCashOnDeliveryYes,cbCashOnDeliveryNo)

        }
    }

    private fun setYesNoCheckbox(value: String?, yesCB: RadioButton, noCB: RadioButton) {
        val isYes = value == "1"
        yesCB.isChecked = isYes
        noCB.isChecked = !isYes
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAddVariant.setOnClickListener {

            }

            btnEdit.setOnClickListener {

            }

            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSizeChart.setOnClickListener {

            }

        }
    }

    override fun observe() {

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

    }

    private fun getSubCategory(categoryId:String) {
        viewModel.getSubCategory(categoryId)
    }

}