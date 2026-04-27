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
import androidx.core.view.isVisible
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
import com.desynatdealer.databinding.FragmentInventoryProductDetailBinding
import com.desynatdealer.databinding.ImagePreviewDialogBinding
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryProductDetailFragment : BaseFragment() {
    private var binding : FragmentInventoryProductDetailBinding?=null
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
    var canAction = true
    var canView = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentInventoryProductDetailBinding.inflate(layoutInflater, container,false)
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
            if (SharedPreference.get(requireActivity()).userType == "1") {
                canAction = CommonMethod.getPermission(requireActivity(),"seller.Inventory_Management")?.canAction == 1
                canView = CommonMethod.getPermission(requireActivity(),"seller.Inventory_Management")?.canView == 1
                btnEdit.isVisible=canAction
            }

            colorList = viewModel.colorList.value as ArrayList<GetColorResponse.Data>
            sizeList = viewModel.sizeList.value as ArrayList<GetSizeResponse.Data>

            val itemJson = arguments?.getString("productData")
            if (!itemJson.isNullOrEmpty()) {
                productData = Gson().fromJson(itemJson, GetAllProductResponse.Data.AllProductData::class.java)
            }

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
                            CommonMethod.imagePreviewDialog(requireActivity(),variantImageList)
                        }

                    }
                )

            binding?.viewPager?.adapter = variantImageAdapter
            TabLayoutMediator(binding!!.dotsIndicator, binding!!.viewPager) { _, _ -> }.attach()

            getInventoryProductDetail()

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

            toolbar.tvToolbar.text = "Product code- ${productData?.id}"
            tvBrand.text = if(productData?.brand?.nameEnglish=="Other") productData?.otherEn else productData?.brand?.nameEnglish
            tvName.text = productData?.productNameEn

            tvPrice.text = "${(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.actualPrice?:0)} KD"
            tvDiscountedPrice.text = "${(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.finalPrice?:0)} KD"

            val variant = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
            tvOffer.text = "$discountValue$discountType"


            tvTotalStocksValue.text =  productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.totalStock
            tvSkuValue.text = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.sku

            updateStatus()

        }
    }

    private fun updateStatus(){
        binding?.apply {
            if(productData?.productActiveStatus==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = "Inactive"
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
                val bundle = Bundle().apply {
                    putString("productData", Gson().toJson(productData))
                    putString("productInfo", Gson().toJson(productInfo))
                }
                findNavController().navigate(R.id.action_inventoryProductDetailFragment_to_editInventoryFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                if (canAction) {
                    updateInventoryProductStatus()
                }
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
                    result.data?.message?.let { showToast(it) }
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

        viewModel.getInventoryProductDetailResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (productData!=null){
                        val subcategory = productData?.subcategory
                        val category = productData?.category
                        productData = result.data?.data

                        if (subcategory != null) {
                            productData?.subcategory = subcategory
                        }
                        if (category != null) {
                            productData?.category = category
                        }
                    }
                    else{
                        productData = result.data?.data
                    }
                    productInfo .clear()

                    productData?.productVariants?.let { mapVariantsToProducts(it) }
                        ?.let { productInfo.addAll(it) }
                    setData()
                    viewModel.getInventoryProductDetailResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getInventoryProductDetailResponse.value = null
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

    private fun getInventoryProductDetail(){
        val map = HashMap<String,Any>()
        map["product_id"] = productData?.id.toString()
        viewModel.getInventoryProductDetail(map)
    }

    fun mapVariantsToProducts(
        variants: ArrayList<GetAllProductResponse.Data.AllProductData.ProductVariant>
    ): ArrayList<AddProductRequest.Product> {

        return ArrayList(
            variants
                .groupBy { it.colorId }   // group by color_id
                .map { (colorId, listByColor) ->

                    val images = ArrayList(listByColor.first().variantImage)

                    val variationList = ArrayList(
                        listByColor.map { variant ->
                            AddProductRequest.Product.ProductVariation(
                                id = variant.id.toString(),
                                productId = variant.productId.toString(),
                                sizeId = variant.sizeId.toString(),
                                actualPrice = variant.actualPrice,
                                discountType = variant.discountType,
                                discountValue = variant.discountValue,
                                finalPrice = variant.finalPrice,
                                totalStock = variant.totalStock,
                                sku = variant.sku,
                                skuThresholdValue = variant.skuThresholdValue ?: "0",
                                bust = variant.bust,
                                waist = variant.waist,
                                hip = variant.hip,
                                shoulder = variant.shoulder,
                                sleeveLength = variant.sleeveLength,
                                length = variant.length,
                                status = variant.status,
                                createdAt = variant.createdAt,
                                updatedAt = variant.updatedAt
                            )
                        }
                    )

                    AddProductRequest.Product(
                        colorId = colorId,
                        variantImage = images,
                        productVariation = variationList,
                        productId = listByColor.first().productId.toString()
                    )
                }
        )
    }

}