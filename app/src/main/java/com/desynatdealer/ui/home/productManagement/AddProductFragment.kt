package com.desynatdealer.ui.home.productManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.AddSpecificationAdapter
import com.desynatdealer.adapter.VariantAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddProductBinding
import com.desynatdealer.ui.home.productManagement.response.AddProductRequest
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetColorResponse
import com.desynatdealer.ui.home.productManagement.response.GetSellerBrandResponse
import com.desynatdealer.ui.home.productManagement.response.GetSizeResponse
import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

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
    private var specificationList = ArrayList<GetAllProductResponse.Data.AllProductData.ProductSpecification>()
    private var addSpecificationAdapter :AddSpecificationAdapter? =null

    var isWomen: Boolean = true
    var imageType = 0 //1-size-chart
    var selectedPos = -1
    var selectedBrand: GetSellerBrandResponse.Data?=null
    var selectedSubCategory: GetSubCategoryResponse.Data?=null
    var selectedCategory: GetCategoryResponse.Data?=null

    private lateinit var imagePicker: ImagePicker
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    private var sizeChartUrl: String? = null

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
                isWomen = true
                getCategoryId("Women")
            }

        binding?.apply {
            CommonMethod.applyArabicOnlyFilter(etProductNameArabic)
            CommonMethod.applyArabicOnlyFilter(etOtherBrandArabic)
            CommonMethod.applyArabicOnlyFilter(etDescriptionArabic)
            CommonMethod.removeSpace(etOtherBrand)
            CommonMethod.removeSpace(etProductName)
            CommonMethod.removeSpace(etDescription)
            CommonMethod.removeSpace(etExchangeableDay)
            CommonMethod.removeSpace(etReturnableDay)

            toolbar.tvToolbar.text = "Add New Product"

            variantList.clear()

        variantAdapter = VariantAdapter(requireActivity(),isWomen,variantList,colorList,sizeList,false,object:VariantAdapter.VariantClick{
            override fun onVariantClick(position: Int, type: Int) {
                when(type){
                    2->{
                        imageType = 2
                        selectedPos = position
                        imagePicker.showImageSourceDialog()
                    }
                    3->{
                        variantList.removeAt(position)

                        // If list becomes empty → add new one
                        if (variantList.isEmpty()) {
                            addEmpty()
                        }
                        variantAdapter?.notifyDataSetChanged()

                    }
                }
            }

        })
        rvVariant.adapter = variantAdapter

            if (variantList.isEmpty()) {
                addEmpty()
            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    val image = CommonMethod.uriToFile(requireActivity(), it).toMultiPartBody("image")
                    viewModel.upload(image)
                }
            }


            addSpecificationAdapter = AddSpecificationAdapter(requireActivity(),specificationList)
            rvSpecification.adapter = addSpecificationAdapter
            if (specificationList.isEmpty()) {
                addEmptySpecification()
            }
        }

    }

    private fun addEmptySpecification(){
        specificationList.add(
            GetAllProductResponse.Data.AllProductData.ProductSpecification(
                nameAr = "",
                nameEn = "",
                featureAr = "",
                featureEn = "",
                id = 0,
                productId = "",
                updatedAt = "",
                createdAt = ""
            )
        )
        addSpecificationAdapter?.notifyDataSetChanged()
    }

    private fun addEmpty() {
        variantList.add(
            GetAllProductResponse.Data.AllProductData.ProductVariant(
                colorId = "",
                sizeId = "",
                bust = "",
                waist = "",
                hip = "",
                shoulder = "",
                sleeveLength = "",
                length = "",
                sku = "",
                skuThresholdValue = "",
                totalStock = "",
                actualPrice = "",
                finalPrice = "",
                discountType = "1",
                discountValue = "",
                variantImage = arrayListOf(),
                createdAt = "",
                updatedAt = "",
                id = 0,
                productId = 0,
                status = ""
            )
        )
        variantAdapter?.notifyDataSetChanged()
    }

    fun getCategoryId(match:String){
        if (categoryList.isNotEmpty()){
            for(i in 0 until categoryList.size){
                if (categoryList[i].name==match){
                    selectedCategory = categoryList[i]
                    getSubCategory(categoryList[i].id.toString())
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            rgCategory.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.cbWomen -> {
                        isWomen = true
                        variantAdapter?.updateIsWomen(isWomen)
                        getCategoryId("Women")
                    }
                    R.id.cbMen -> {
                        isWomen = false
                        variantAdapter?.updateIsWomen(isWomen)
                        getCategoryId("Men")
                    }
                    R.id.cbKids -> {
                        isWomen = false
                        variantAdapter?.updateIsWomen(isWomen)
                        getCategoryId("Kids")
                    }
                }
            }

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAddVariant.setOnClickListener {

                if (!isAllVariantValid()) {
                    return@setOnClickListener
                }

                addEmpty()
            }

            ivAdd.setOnClickListener {

                if (!isAllSpecificationValid()) {
                    return@setOnClickListener
                }

                addEmptySpecification()
            }

            btnAdd.setOnClickListener {
                if (!isAllProductValid()) {
                    return@setOnClickListener
                }

                if (!isAllVariantValid()) {
                    return@setOnClickListener
                }

                if (!isAllSpecificationValid()) {
                    return@setOnClickListener
                }

                addProduct()
            }

            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSizeChart.setOnClickListener {
                imageType = 1
                imagePicker.showImageSourceDialog()
            }

            etSubCategory.setOnClickListener {
                showSubCategoryDropdown(etSubCategory)
            }

            etBrand.setOnClickListener {
                showBrandDropdown(etBrand)
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
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
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

        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    result.data?.data?.url?.let { setImageData(it) }
                    viewModel.uploadResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.uploadResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.addProductResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),
                        result.data?.message.toString()
                    ){
                        findNavController().popBackStack()
                    }
                    viewModel.addProductResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addProductResponse.value = null
                }

                else -> {}
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

    private fun showSubCategoryDropdown(view: AppCompatEditText) {
        val list = subCategoryList.map { it.nameEnglish }
        val popup = androidx.appcompat.widget.ListPopupWindow(requireActivity())
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            selectedSubCategory = subCategoryList[pos]
            view.setText(selectedSubCategory?.nameEnglish)
            popup.dismiss()
        }
        popup.show()
    }

    private fun showBrandDropdown(view: AppCompatEditText) {
        val list = brandList.map { it.nameEnglish }
        val popup = androidx.appcompat.widget.ListPopupWindow(requireActivity())
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            selectedBrand = brandList[pos]
            view.setText(selectedBrand?.nameEnglish)
            popup.dismiss()
        }
        popup.show()
    }

    private fun setImageData(image:String){
        when(imageType){
            1->{
                sizeChartUrl = image
                binding?.ivSizeChart?.load(sizeChartUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            }
            2->{
                variantList[selectedPos].variantImage = arrayListOf(image)
                variantAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
            }
        }
        imageType = 0
    }

    private fun isAllSpecificationValid(): Boolean {
        for ((index, v) in specificationList.withIndex()) {

            if (v.nameEn.isNullOrEmpty()) {
                showToast("Please enter name in english")
                return false
            }
            if (v.nameAr.isNullOrEmpty()) {
                showToast("Please enter name  in arabic")
                return false
            }
            if (v.featureEn.isNullOrEmpty()) {
                showToast("Please enter feature  in english")
                return false
            }
            if (v.featureAr.isNullOrEmpty()) {
                showToast("Please enter feature  in arabic")
                return false
            }
        }
        return true
    }

    private fun isAllVariantValid(): Boolean {
        for ((index, v) in variantList.withIndex()) {
            var comeIndex = (index+1).toString()
            if (comeIndex=="1"){
                comeIndex = ""
            }
            if (v.colorId.isNullOrEmpty()) {
                showToast("Select color for variant ${comeIndex}")
                return false
            }
            if (v.sizeId.isNullOrEmpty()) {
                showToast("Select size for variant ${comeIndex}")
                return false
            }
            if (v.actualPrice.isNullOrEmpty()) {
                showToast("Enter actual price for variant ${comeIndex}")
                return false
            }
            if (v.discountValue.isNullOrEmpty()) {
                showToast("Enter discount for variant ${comeIndex}")
                return false
            }
            if (v.sku.isNullOrEmpty()) {
                showToast("Enter SKU for variant ${comeIndex}")
                return false
            }
            if (v.totalStock.isNullOrEmpty()) {
                showToast("Enter stock for variant ${comeIndex}")
                return false
            }
        }
        return true
    }

    private fun isAllProductValid(): Boolean {

        if (selectedCategory == null) {
            showToast("Please select category")
            return false
        }

        if (selectedSubCategory == null) {
            showToast("Please select subcategory")
            return false
        }

        if (selectedBrand == null && binding?.etOtherBrand?.text.isNullOrEmpty()) {
            showToast("Please select or enter brand")
            return false
        }

        binding?.apply {
            if (etProductName.text.isNullOrEmpty()) {
                showToast("Please enter product name in english")
                return false
            }

            if (etProductNameArabic.text.isNullOrEmpty()) {
                showToast("Please enter product name in arabic")
                return false
            }

            if (etDescription.text.isNullOrEmpty()) {
                showToast("Please enter description in english")
                return false
            }

            if (etDescriptionArabic.text.isNullOrEmpty()) {
                showToast("Please enter description in arabic")
                return false
            }

            if (sizeChartUrl.isNullOrEmpty()) {
                showToast("Please upload size chart")
                return false
            }


            val returnableId = rgReturnable.checkedRadioButtonId
            if (returnableId == -1) {
                showToast("Please select if product is returnable")
                return false
            }

            val isReturnable = returnableId == cbReturnableYes.id

            if (isReturnable && etReturnableDay.text.isNullOrEmpty()) {
                showToast("Please enter returnable days")
                return false
            }

            val exchangeId = rgExchangeble.checkedRadioButtonId
            if (exchangeId == -1) {
                showToast("Please select if product is exchangeable")
                return false
            }

            val isExchangeable = exchangeId == cbExchangeYes.id

            if (isExchangeable && etExchangeableDay.text.isNullOrEmpty()) {
                showToast("Please enter exchangeable days")
                return false
            }

            if (rgReviewRating.checkedRadioButtonId == -1) {
                showToast("Please select review & rating option")
                return false
            }

            if (rgAffiliatedProduct.checkedRadioButtonId == -1) {
                showToast("Please select affiliated product option")
                return false
            }

            if (rgCustomizedProduct.checkedRadioButtonId == -1) {
                showToast("Please select customized product option")
                return false
            }

            if (rgReadyToShip.checkedRadioButtonId == -1) {
                showToast("Please select ready to ship option")
                return false
            }

            if (rgCashOnDelivery.checkedRadioButtonId == -1) {
                showToast("Please select cash on delivery option")
                return false
            }
        }

        return true
    }

    private fun addProduct() {

        val colorIdList = variantList.map { it.colorId }
        val sizeIdList = variantList.map { it.sizeId }
        val bustList = variantList.map { it.bust }
        val waistList = variantList.map { it.waist }
        val hipList = variantList.map { it.hip }
        val shoulderList = variantList.map { it.shoulder }
        val sleeveLengthList = variantList.map { it.sleeveLength }
        val lengthList = variantList.map { it.length }
        val skuList = variantList.map { it.sku }
        val thresholdList = variantList.map { it.skuThresholdValue }
        val stockList = variantList.map { it.totalStock }
        val actualPriceList = variantList.map { it.actualPrice }
        val finalPriceList = variantList.map { it.finalPrice }
        val discountTypeList = variantList.map { it.discountType }
        val discountValueList = variantList.map { it.discountValue }
        val variantImagesList = variantList.map { it.variantImage }

        val specificationNameListEn = specificationList.map { it.nameEn }
        val specificationNameListAr = specificationList.map { it.nameAr }
        val specificationFeatureListEn = specificationList.map { it.featureEn }
        val specificationFeatureListAr = specificationList.map { it.featureAr }

        binding?.apply {

            val request = AddProductRequest(
                categoryId = selectedCategory?.id.toString(),
                subcategoryId = selectedSubCategory?.id.toString(),
                brandId = selectedBrand?.id.toString(),
                otherEn = etOtherBrand.text?.trim().toString(),
                otherAr = etOtherBrandArabic.text?.trim().toString(),
                productNameEn = etProductName.text?.trim().toString(),
                productNameAr = etProductNameArabic.text?.trim().toString(),
                descriptionEn = etDescription.text?.trim().toString(),
                descriptionAr = etDescriptionArabic.text?.trim().toString(),
                minCardQuantity = etMinCartQuantity.text?.trim().toString(),
                maxCardQuantity = etMaxCartQuantity.text?.trim().toString(),
                returnDays = etReturnableDay.text?.trim().toString(),
                exchangableDays = etExchangeableDay.text?.trim().toString(),
                uploadSizeChart = sizeChartUrl.toString(),
                productExchangable = if(cbExchangeYes.isChecked) "1" else "0",
                productReturn = if(cbReturnableYes.isChecked) "1" else "0",
                reviewRating = if(cbReviewRatingYes.isChecked) "1" else "0",
                affiliatedProduct = if(cbAffiliatedProductYes.isChecked) "1" else "0",
                customizedProduct = if(cbCustomizedProductYes.isChecked) "1" else "0",
                readyToShip = if(cbReadyToShipYes.isChecked) "1" else "0",
                cashOnDelivery = if(cbCashOnDeliveryYes.isChecked) "1" else "0",

                colorId = colorIdList,
                sizeId = sizeIdList,
                bust = bustList,
                waist = waistList,
                hip = hipList,
                shoulder = shoulderList,
                sleeveLength = sleeveLengthList,
                length = lengthList,
                sku = skuList,
                skuThresholdValue = thresholdList,
                totalStock = stockList,
                actualPrice = actualPriceList,
                finalPrice = finalPriceList,
                discountType = discountTypeList,
                discountValue = discountValueList,
                variantImage = variantImagesList,

                productSpeAr = specificationNameListAr,
                productSpeEn = specificationNameListEn,
                productSpeFeatureEn = specificationFeatureListEn,
                productSpeFeatureAr = specificationFeatureListAr,

            )
            viewModel.addProduct(request)
        }
    }

}
