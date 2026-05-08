package com.desynatdealer.ui.home.inventoryManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.ImagePreviewAdapter
import com.desynatdealer.adapter.ProductImageAdapter
import com.desynatdealer.adapter.ProductRectangleColorAdapter
import com.desynatdealer.adapter.SizeAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentEditInventoryProductBinding
import com.desynatdealer.databinding.ImagePreviewDialogBinding
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditInventoryProductFragment : BaseFragment() {
    private var binding : FragmentEditInventoryProductBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    private var productData : GetAllProductResponse.Data.AllProductData?=null
    private var colorList = ArrayList<GetColorResponse.Data>()
    private var sizeList = ArrayList<GetSizeResponse.Data>()
    private var variantImageList = ArrayList<String>()
    private var selectedSizeList = ArrayList<AddProductRequest.Product.ProductVariation>()
    private var selectedColorList = ArrayList<AddProductRequest.Product>()
    private var colorAdapter: ProductRectangleColorAdapter?=null
    private var sizeAdapter: SizeAdapter?=null
    private var variantImageAdapter: ProductImageAdapter?=null
    private var selectedColorPos = 0
    private var selectedSizePos = 0
    var productInfo = ArrayList<AddProductRequest.Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentEditInventoryProductBinding.inflate(layoutInflater, container,false)
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

        binding?.apply {

            colorList = viewModel.colorList.value as ArrayList<GetColorResponse.Data>
            sizeList = viewModel.sizeList.value as ArrayList<GetSizeResponse.Data>

            val itemJson = arguments?.getString("productData")
            if (!itemJson.isNullOrEmpty()) {
                productData = Gson().fromJson(itemJson, GetAllProductResponse.Data.AllProductData::class.java)
            }

            toolbar.tvToolbar.text = "Product code- ${productData?.id}"

            val productInfoData = arguments?.getString("productInfo")
            val type = object : TypeToken<ArrayList<AddProductRequest.Product>>() {}.type
            productInfo = Gson().fromJson(productInfoData, type)

            selectedColorList.clear()
            colorAdapter =
                ProductRectangleColorAdapter(requireActivity(),
                    selectedColorList,colorList,object :ProductRectangleColorAdapter.OnClick{
                        override fun onClickItem(position: Int) {
                            selectedColorPos = position
                            selectedSizePos = 0
                            sizeAdapter?.updateData(selectedSizePos)
                            setData()
                        }

                    })

            binding?.rvColor?.adapter = colorAdapter

            selectedSizeList.clear()
            sizeAdapter = SizeAdapter(requireActivity(),
                selectedSizeList,sizeList,object :SizeAdapter.OnClick{
                    override fun onClickItem(position: Int) {
                        selectedSizePos = position
                        setData()
                    }

                })

            binding?.rvSize?.adapter = sizeAdapter

            variantImageList.clear()
            variantImageAdapter =
                ProductImageAdapter(requireActivity(),
                    variantImageList,object:ProductImageAdapter.OnClick{
                        override fun onClick(position: Int) {
                            imagePreviewDialog(position)
                        }

                    }
                )

            binding?.viewPager?.adapter = variantImageAdapter
            TabLayoutMediator(binding!!.dotsIndicator, binding!!.viewPager) { _, _ -> }.attach()

            setData()
        }
    }

    private fun setData() {
        binding?.apply {

            selectedSizeList.clear()
            productInfo[selectedColorPos].productVariation.let {
                selectedSizeList.addAll(
                    it
                )
            }
            sizeAdapter?.notifyDataSetChanged()

            selectedColorList.clear()

            productInfo.let { selectedColorList.addAll(it) }
            colorAdapter?.notifyDataSetChanged()

            variantImageList.clear()
            productInfo[selectedColorPos]?.variantImage?.let {
                variantImageList.addAll(
                    it
                )
            }
            variantImageAdapter?.notifyDataSetChanged()
            viewPager.setCurrentItem(0, false)
            tvName.text = if (Constants.language=="ar") productData?.productNameAr else productData?.productNameEn
            tvBrand.text = if(productData?.brand?.nameEnglish=="Other"){
                if (Constants.language=="ar") productData?.otherAr else productData?.otherEn
            }
            else {
                if (Constants.language=="ar")productData?.brand?.nameArabic else productData?.brand?.nameEnglish
            }

            tvPrice.text = "${(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.actualPrice?:0)} ${getString(R.string.kd)}"
            tvDiscountedPrice.text = "${(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.finalPrice?:0)} ${
                getString(
                    R.string.kd
                )
            }"

            val variant = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% ${getString(R.string.off)}" else " ${getString(R.string.kd)} ${
                getString(
                    R.string.off
                )
            }"
            tvOffer.text = "$discountValue$discountType"

            etTotalStocks.setText(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.totalStock)
            etSku.setText(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.sku)


            updateStatus()

        }
    }

    private fun updateStatus(){
        binding?.apply {
            if(productData?.productActiveStatus==1){
                tvSwitch.text = getString(R.string.active)
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = getString(R.string.inactive)
                tvSwitch.setTextColor(Color.parseColor("#875C3A"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnEdit.setOnClickListener {
                if (validation()) {
                    updateInventoryData()
                }
            }

            tvSwitch.setOnClickListener {
                updateInventoryProductStatus()
            }

        }
    }

    override fun observe() {
        viewModel.updateInventoryProductStatusResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (result.data?.data?.productActiveStatus==1){
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.product_active_successfully))
                    }
                    else{
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.product_de_active_successfully))
                    }
                    productData?.productActiveStatus= result.data?.data?.productActiveStatus!!
                    updateStatus()
                    viewModel.updateInventoryProductStatusResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateInventoryProductStatusResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.updateInventoryProductStockResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.inventory_updated_successfully)){
                        productInfo[selectedColorPos].productVariation[selectedSizePos].sku = binding?.etSku?.text?.trim().toString()
                        productInfo[selectedColorPos].productVariation[selectedSizePos].totalStock = binding?.etTotalStocks?.text?.trim().toString()
                        viewModel.updateInventoryProductStockResponse.value = null
                        findNavController().popBackStack()
                    }


                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateInventoryProductStockResponse.value = null
                }
                else ->{

                }
            }
        }

    }

    private fun updateInventoryProductStatus(){
        val map = HashMap<String,Any>()
        map["product_id"] = productData?.id.toString()
        viewModel.updateInventoryProductStatus(map)
    }

    private fun updateInventoryData(){
        val map = HashMap<String,Any>()
        map["product_id"] = productData?.id.toString()
        map["variation_id"] = productInfo[selectedColorPos].productVariation[selectedSizePos].id.toString()
        map["stock_quantity"] = binding?.etTotalStocks?.text?.trim().toString()
        map["sku"] = binding?.etSku?.text?.trim().toString()
        viewModel.updateInventoryProductStock(map)
    }

    private fun validation(): Boolean {
        binding?.apply {

            val stockStr = etTotalStocks.text?.trim().toString()
            val skuStr = etSku.text?.trim().toString()

            if (stockStr.isEmpty()) {
                showToast(getString(R.string.please_enter_stock))
                return false
            }

            if (skuStr.isEmpty()) {
                showToast(getString(R.string.please_enter_sku_limit))
                return false
            }

            val stockVal = stockStr.toIntOrNull()
            val skuVal = skuStr.toIntOrNull()

            if (stockVal == null) {
                showToast(getString(R.string.invalid_stock))
                return false
            }

            if (skuVal == null) {
                showToast(getString(R.string.invalid_sku_limit))
                return false
            }

            if (stockVal == 0 && skuVal == 0) {
                return true
            }

            if (skuVal >= stockVal) {
                showToast(getString(R.string.sku_limit_must_be_smaller_than_total_stock))
                return false
            }
        }
        return true
    }

    private fun imagePreviewDialog(position: Int) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ImagePreviewDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            ivCross.setOnClickListener{
                dialog?.dismiss()
            }

            val imagePreviewAdapter = ImagePreviewAdapter(requireActivity(),variantImageList)
            viewPager?.adapter = imagePreviewAdapter

            TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()

        }

        dialog?.show()
    }

}