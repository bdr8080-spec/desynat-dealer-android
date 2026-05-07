package com.desynatdealer.ui.home.productManagement

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
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.ImagePreviewAdapter
import com.desynatdealer.adapter.ProductImageAdapter
import com.desynatdealer.adapter.ProductRectangleColorAdapter
import com.desynatdealer.adapter.ReviewAdapter
import com.desynatdealer.adapter.SizeAdapter
import com.desynatdealer.adapter.SpecificationAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentProductDetailBinding
import com.desynatdealer.databinding.ImagePreviewDialogBinding
import com.desynatdealer.databinding.SizeGuideChartDialogBinding
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
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
    private var variantImageList = ArrayList<String>()
    private var selectedSizeList = ArrayList<AddProductRequest.Product.ProductVariation>()
    private var selectedColorList = ArrayList<AddProductRequest.Product>()
    private var colorAdapter: ProductRectangleColorAdapter?=null
    private var sizeAdapter: SizeAdapter?=null
    private var specificationAdapter: SpecificationAdapter?=null
    private var reviewAdapter: ReviewAdapter?=null
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
        binding = FragmentProductDetailBinding.inflate(layoutInflater, container,false)
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
        colorList = viewModel.colorList.value as ArrayList<GetColorResponse.Data>
        sizeList = viewModel.sizeList.value as ArrayList<GetSizeResponse.Data>
        val itemJson = arguments?.getString("productData")
        if (!itemJson.isNullOrEmpty()) {
            productData = Gson().fromJson(itemJson, GetAllProductResponse.Data.AllProductData::class.java)
            //   setData()
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

        getProductDetail()

        specificationAdapter = productData?.productSpecifications?.let {
            SpecificationAdapter(requireActivity(),
                it)
        }
        binding?.rvSpecification?.adapter = specificationAdapter

        reviewAdapter = ReviewAdapter(requireActivity(), arrayListOf("",""))
        binding?.rvRating?.adapter = reviewAdapter
        binding?.apply {
            if (SharedPreference.get(requireActivity()).userType == "1") {
                canAction = CommonMethod.getPermission(requireActivity(),"seller.products")?.canAction == 1
                canView = CommonMethod.getPermission(requireActivity(),"seller.products")?.canView == 1
                    btnEdit.isVisible = canAction
                    btnDelete.isVisible = canAction
            }
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
           // productData?.productVariants?.let { selectedSizeList.addAll(it) }
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

            tvTitle.text = "Product code- ${productData?.id}"
            tvBrand.text = if(productData?.brand?.nameEnglish=="Other"){
                if (Constants.language=="ar") productData?.otherAr else productData?.otherEn
            }
            else {
                if (Constants.language=="ar")productData?.brand?.nameArabic else productData?.brand?.nameEnglish
            }
            tvName.text = if (Constants.language=="ar") productData?.productNameAr else productData?.productNameEn
            tvStore.text = SharedPreference.get(requireActivity()).getUser()?.storeInfo?.storeName +" (${productData?.category?.name})"

         /*   val rating = productData?.reviewRating?.toFloatOrNull() ?: 0f
            ratingBar.rating = rating
            tvRating.text = "(${productData?.reviewRating})"*/

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

            tvDate.text = "${getString(R.string.added_on)}- ${CommonMethod.customizeChangeDateFormat(productData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"
            tvDescription.text = productData?.descriptionEn

            if(productData?.productReturn=="1"){
                tvReturnTime.text = "${productData?.returnDays} ${getString(R.string.days)}"
            }
            if(productData?.productReturn=="1"){
                tvExchangeTime.text = "${productData?.exchangableDays} ${getString(R.string.days)}"
            }
            tvRatingAndReviewStatus.text = if(productData?.reviewRating=="1") getString(R.string.yes) else getString(R.string.no)
            tvAffiliatedProduct.text = if(productData?.affiliatedProduct=="1") getString(R.string.yes) else getString(R.string.no)
            tvCustomizedStatus.text = if(productData?.customizedProduct=="1") getString(R.string.yes) else getString(R.string.no)
            tvReadyToShipStatus.text = if(productData?.readyToShip=="1") getString(R.string.yes) else getString(R.string.no)
            tvCashOnDeliveryStatus.text = if(productData?.cashOnDelivery=="1") getString(R.string.yes) else getString(R.string.no)

            if (productData?.category?.name=="Women"){
                clWomenSize.isVisible = true
                tvBustSize.text = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.bust?:"NA"
                tvHipSize.text =productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.hip?:"NA"
                tvShoulderSize.text = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.shoulder?:"NA"
                tvWaistSize.text = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.waist?:"NA"
                tvSleeveLengthSize.text = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.sleeveLength?:"NA"
                tvLengthSize.text = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.length?:"NA"

            }
            else{
                clWomenSize.isVisible = false
            }

            tvTotalStocksValue.text =  productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.totalStock
            tvSkuValue.text = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.sku
//            tvSkuThresholdValue.text =  productData?.productVariants?.get(selectedSizePos)?.skuThresholdValue

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

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }


            ivShare.setOnClickListener {
                CommonMethod.shareProduct(requireActivity(),
                    productData?.productNameEn ?: "",
                    productData?.descriptionEn ?: "",
                    productData?.productVariants?.get(selectedColorPos)?.variantImage?.get(0) ?: ""
                )
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("productData", Gson().toJson(productData))
                    putString("productInfo", Gson().toJson(productInfo))
                }
                findNavController().navigate(R.id.action_productDetailFragment_to_editProductFragment,bundle)
            }

            btnDelete.setOnClickListener {
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    getString(R.string.are_you_sure_you_want_to_delete_the_product)
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deleteProduct()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }

            }

            tvSwitch.setOnClickListener {
                updateProductStatus()
            }

            tvSizeGuide.setOnClickListener {
                sizeGuideDialog()
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
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.product_deleted_successfully)){
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

        viewModel.getProductDetailResponse.observe(viewLifecycleOwner){result->
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
                    viewModel.getProductDetailResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getProductDetailResponse.value = null
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

  private fun getProductDetail(){
        val map = HashMap<String,Any>()
        map["product_id"] = productData?.id.toString()
        viewModel.getProductDetail(map)
    }

    private fun sizeGuideDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SizeGuideChartDialogBinding.inflate(LayoutInflater.from(context))
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

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            ivSizeChart.load(productData?.uploadSizeChart) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        }

        dialog.show()
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