package com.desynatdealer.ui.home.productManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.ProductColorAdapter
import com.desynatdealer.adapter.ProductImageAdapter
import com.desynatdealer.adapter.ReviewAdapter
import com.desynatdealer.adapter.SizeAdapter
import com.desynatdealer.adapter.SpecificationAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentProductDetailBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment() {
    private var binding : FragmentProductDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var productData :GetAllProductResponse.Data.AllProductData?=null
    private var colorList = ArrayList<GetColorResponse.Data>()
    private var sizeList = ArrayList<GetSizeResponse.Data>()
    private var colorAdapter: ProductColorAdapter?=null
    private var sizeAdapter: SizeAdapter?=null
    private var specificationAdapter: SpecificationAdapter?=null
    private var reviewAdapter: ReviewAdapter?=null
    private var selectedPos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentProductDetailBinding.inflate(layoutInflater, container,false)
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
        colorList = viewModel.colorList.value as ArrayList<GetColorResponse.Data>
        sizeList = viewModel.sizeList.value as ArrayList<GetSizeResponse.Data>
        val itemJson = arguments?.getString("productData")
        if (!itemJson.isNullOrEmpty()) {
            productData = Gson().fromJson(itemJson, GetAllProductResponse.Data.AllProductData::class.java)
            setData()
        }

        colorAdapter = productData?.productVariants?.let {
            ProductColorAdapter(requireActivity(),
                it,colorList,true)
        }
            binding?.rvColor?.adapter = colorAdapter

        sizeAdapter = productData?.productVariants?.let {
            SizeAdapter(requireActivity(),
                it,sizeList,object :SizeAdapter.OnClick{
                    override fun onClickItem(position: Int) {
                        selectedPos = position

                        setData()
                    }

                })
        }
        binding?.rvSize?.adapter = sizeAdapter

        specificationAdapter = productData?.productSpecifications?.let {
            SpecificationAdapter(requireActivity(),
                it)
        }
        binding?.rvSpecification?.adapter = specificationAdapter


        val adapter = productData?.productVariants?.let {
            ProductImageAdapter(requireActivity(),
                it
            )
        }
        binding?.viewPager?.adapter = adapter
        TabLayoutMediator(binding!!.dotsIndicator, binding!!.viewPager) { _, _ -> }.attach()

        reviewAdapter = ReviewAdapter(requireActivity(), arrayListOf("",""))
        binding?.rvRating?.adapter = reviewAdapter
    }

    private fun setData() {
        binding?.apply {
            tvTitle.text = productData?.productNameEn
            tvBrand.text = productData?.brand?.nameEnglish
            tvName.text = productData?.productNameEn
            tvStore.text = productData?.storeId

         /*   val rating = productData?.reviewRating?.toFloatOrNull() ?: 0f
            ratingBar.rating = rating
            tvRating.text = "(${productData?.reviewRating})"*/

            tvPrice.text = "${(productData?.productVariants?.get(selectedPos)?.actualPrice?:0)} KD"
            tvDiscountedPrice.text = "${(productData?.productVariants?.get(selectedPos)?.finalPrice?:0)} KD"

            val variant = productData?.productVariants?.getOrNull(selectedPos)
            val discountValue = variant?.discountValue ?: 0
            val discountType = if (variant?.discountType == "1") "% off" else "KD off"
            tvOffer.text = "$discountValue $discountType"

            tvDate.text = "Added on- ${CommonMethod.customizeChangeDateFormat(productData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"
            tvDescription.text = productData?.descriptionEn

            tvReturnTime.text = "${productData?.returnDays} days"
            tvExchangeTime.text = "${productData?.exchangableDays} days"
            tvRatingAndReviewStatus.text = if(productData?.reviewRating=="1") "Yes" else "No"
            tvAffiliatedProduct.text = if(productData?.affiliatedProduct=="1") "Yes" else "No"
            tvCustomizedStatus.text = if(productData?.customizedProduct=="1") "Yes" else "No"
            tvReadyToShipStatus.text = if(productData?.readyToShip=="1") "Yes" else "No"
            tvCashOnDeliveryStatus.text = if(productData?.cashOnDelivery=="1") "Yes" else "No"

            if (productData?.category?.name=="Women"){
                clWomenSize.isVisible = true
                tvBustSize.text = productData?.productVariants?.get(selectedPos)?.bust?:"NA"
                tvHipSize.text = productData?.productVariants?.get(selectedPos)?.hip?:"NA"
                tvShoulderSize.text = productData?.productVariants?.get(selectedPos)?.shoulder?:"NA"
                tvWaistSize.text = productData?.productVariants?.get(selectedPos)?.waist?:"NA"
                tvSleeveLengthSize.text = productData?.productVariants?.get(selectedPos)?.sleeveLength?:"NA"
                tvLengthSize.text = productData?.productVariants?.get(selectedPos)?.length?:"NA"

            }
            else{
                clWomenSize.isVisible = false
            }

            tvTotalStocksValue.text =  productData?.productVariants?.get(selectedPos)?.totalStock
            tvSkuValue.text = productData?.productVariants?.get(selectedPos)?.sku
            tvSkuThresholdValue.text =  productData?.productVariants?.get(selectedPos)?.skuThresholdValue

            updateStatus()

        }
    }

    private fun updateStatus(){
        binding?.apply {
            if(productData?.productActiveStatus==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#8B5E3C"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivShare.setOnClickListener {

            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("productData", Gson().toJson(productData))
                }
                findNavController().navigate(R.id.action_productDetailFragment_to_editProductFragment,bundle)
            }

            btnDelete.setOnClickListener {
                deleteProduct()
            }

            tvSwitch.setOnClickListener {
                updateProductStatus()
            }
        }
    }

    override fun observe() {
        viewModel.deleteProductResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),"Product deleted successfully"){
                        findNavController().popBackStack()
                    }
                    viewModel.deleteProductResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.deleteProductResponse.value = null
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
                    productData?.productActiveStatus= result.data?.data?.productActiveStatus!!
                    updateStatus()
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
    }


    private fun updateProductStatus(){
        val map = HashMap<String,Any>()
        map["product_id"] = productData?.id.toString()
        viewModel.updateProductStatus(map)
    }

    private fun deleteProduct(){
        val map = HashMap<String,Any>()
        map["product_id"] = productData?.id.toString()
        viewModel.deleteProduct(map)
    }


}