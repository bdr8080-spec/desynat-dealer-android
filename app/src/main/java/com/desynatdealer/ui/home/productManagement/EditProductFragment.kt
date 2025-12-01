package com.desynatdealer.ui.home.productManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.AddSpecificationAdapter
import com.desynatdealer.adapter.VariantAdapter
import com.desynatdealer.adapter.VariantColorAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentEditProductBinding
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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditProductFragment : BaseFragment() {
    private var binding : FragmentEditProductBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var colorList = ArrayList<GetColorResponse.Data>()
    private var brandList = ArrayList<GetSellerBrandResponse.Data>()
    private var sizeList = ArrayList<GetSizeResponse.Data>()
    private var variantList = ArrayList<GetAllProductResponse.Data.AllProductData.Product>()
    private var selectedVariantList = ArrayList<GetAllProductResponse.Data.AllProductData.Product>()
    private var variantAdapter :VariantAdapter? =null
    private var variantColorAdapter : VariantColorAdapter? =null
    private var productData :GetAllProductResponse.Data.AllProductData?=null
    var selectedColorId = ""
    var imageType = 0 //1-size-chart
    var selectedPos = -1
    var selectedBrand: GetSellerBrandResponse.Data?=null
    var selectedSubCategory: GetSubCategoryResponse.Data?=null
    var selectedCategory: GetCategoryResponse.Data?=null

    private var specificationList = ArrayList<GetAllProductResponse.Data.AllProductData.ProductSpecification>()
    private var addSpecificationAdapter : AddSpecificationAdapter? =null
    var isWomen: Boolean = true

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
            productData?.product?.let { variantList.addAll(it) }
        }

        variantList.clear()
        productData?.product?.let { variantList.addAll(it) }

        variantAdapter = VariantAdapter(requireActivity(),
            productData?.category?.name=="Women",variantList,colorList,sizeList,true,object:VariantAdapter.VariantClick{
            override fun onVariantClick(position: Int, type: Int,imgPos:Int,subPos:Int) {
                when(type){
                    2->{
                        imageType = 2
                        selectedPos = position
                        imagePicker.showImageSourceDialog()
                    }
                    3->{
                        variantList.removeAt(position)

                        if (variantList.isEmpty()) {
                            addEmpty()
                        }
                        variantAdapter?.notifyDataSetChanged()

                    }
                    4->{
                        variantList[position].variantImage.removeAt(imgPos)
                        variantAdapter?.notifyItemChanged(position)

                    }
                }
            }

        })
        binding?.rvVariant?.adapter = variantAdapter

        selectedColorId = productData?.product?.get(0)?.colorId.toString()
            // getVariantList()


        variantColorAdapter = VariantColorAdapter(requireActivity(),variantList,colorList,object:VariantColorAdapter.OnClick{
            override fun onClickItem(position: Int) {
                selectedColorId = productData?.product?.get(position)?.colorId.toString()
                getVariantList()
            }

        })
        binding?.rvVariantColor?.adapter = variantColorAdapter

        binding?.apply {
            CommonMethod.applyArabicOnlyFilter(etProductNameArabic)
          //  CommonMethod.applyArabicOnlyFilter(etOtherBrandArabic)
            CommonMethod.applyArabicOnlyFilter(etDescriptionArabic)
          //  CommonMethod.removeSpace(etOtherBrand)
            CommonMethod.removeSpace(etProductName)
            CommonMethod.removeSpace(etDescription)
            CommonMethod.removeSpace(etExchangeableDay)
            CommonMethod.removeSpace(etReturnableDay)

            specificationList.clear()
            productData?.productSpecifications?.let { specificationList.addAll(it) }
            addSpecificationAdapter = AddSpecificationAdapter(requireActivity(),specificationList)
            rvSpecification.adapter = addSpecificationAdapter
            if (specificationList.isEmpty()) {
                addEmptySpecification()
            }

            etMinCartQuantity.addTextChangedListener(object : TextWatcher {

                private var isEditing = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    s?.let {
                        val value = it.toString()

                        if (value == "0") {
                            it.clear()
                            isEditing = false
                            return
                        }

                        if (value.length > 1 && value.startsWith("0")) {
                            val newValue = value.trimStart('0')
                            it.replace(0, it.length, newValue)
                            isEditing = false
                            return
                        }
                    }

                    isEditing = false
                }
            })

            etMaxCartQuantity.addTextChangedListener(object : TextWatcher {

                private var isEditing = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    s?.let {
                        val value = it.toString()

                        if (value == "0") {
                            it.clear()
                            isEditing = false
                            return
                        }

                        if (value.length > 1 && value.startsWith("0")) {
                            val newValue = value.trimStart('0')
                            it.replace(0, it.length, newValue)
                            isEditing = false
                            return
                        }
                    }

                    isEditing = false
                }
            })

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

    }

    private fun validateMinMax(): Boolean {
        binding?.apply {

            val minText = etMinCartQuantity.text.toString().trim()
            val maxText = etMaxCartQuantity.text.toString().trim()

            val min = minText.toIntOrNull()
            val max = maxText.toIntOrNull()

            etMinCartQuantity.error = null
            etMaxCartQuantity.error = null

            if (minText.isEmpty()) {
                showToast("Please enter minimum quantity")
                return false
            }

            if (maxText.isEmpty()) {
                showToast("Please enter maximum quantity")
                return false
            }

            if (min == null) {
                showToast("Invalid number")
                return false
            }

            if (max == null) {
                showToast("Invalid number")
                return false
            }

            if (min >= max) {
                showToast("Max must be greater than Min")
                return false
            }

            return true
        }

        return false
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


    private fun getVariantList() {
        val productVariant = productData?.product
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

            sizeChartUrl = productData?.uploadSizeChart

            selectedCategory = productData?.category
            selectedSubCategory = productData?.subcategory
            selectedBrand = productData?.brand

            when(selectedCategory?.name){
                "Women" -> cbWomen.isChecked =true
                "Men" -> cbMen.isChecked =true
                "Kids" -> cbKids.isChecked =true
            }

            etProductName.setText(productData?.productNameEn)
            etProductNameArabic.setText(productData?.productNameAr)

            etSubCategory.setText(productData?.subcategory?.nameEnglish)
            etBrand.setText(productData?.brand?.nameEnglish)

                if (productData?.brand?.nameEnglish=="Other"){
                    etOtherBrand.isVisible = true
                    etOtherBrandArabic.isVisible = true

                    tvOtherBrand.isVisible = true
                    tvOtherBrandArabic.isVisible = true

                    tvBrandArabic.isVisible = true
                    tvBrandEnglish.isVisible = true

                    etOtherBrand.setText(productData?.otherEn)
                    etOtherBrandArabic.setText(productData?.otherAr)
                }
                else{
                    tvBrandArabic.isVisible = false
                    tvBrandEnglish.isVisible = false
                    etOtherBrand.isVisible = false
                    etOtherBrandArabic.isVisible = false
                    tvOtherBrand.isVisible = false
                    tvOtherBrandArabic.isVisible = false
                }


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

            ivSizeChart.load(productData?.uploadSizeChart) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
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

            rgReturnable.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.cbReturnableYes -> {
                        etReturnableDay.isVisible = true
                    }
                    R.id.cbReturnableNo -> {
                        etReturnableDay.isVisible = false
                    }

                }
            }

            rgExchangeble.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.cbExchangeYes -> {
                        etExchangeableDay.isVisible = true
                    }
                    R.id.cbExchangeNo -> {
                        etExchangeableDay.isVisible = false
                    }

                }
            }

            btnEdit.setOnClickListener {
                if (!isAllProductValid()) {
                    return@setOnClickListener
                }

                if (!isAllVariantValid()) {
                    return@setOnClickListener
                }

                if (!isAllSpecificationValid()) {
                    return@setOnClickListener
                }

                editProduct()
            }

            etSubCategory.setOnClickListener {
                showSubCategoryDropdown(etSubCategory)
            }

            etBrand.setOnClickListener {
                showBrandDropdown(etBrand)
            }


            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSizeChart.setOnClickListener {
                imageType = 1
                imagePicker.showImageSourceDialog()
            }

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

        }
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

    override fun observe() {

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

        viewModel.editProductResponse.observe(viewLifecycleOwner) { result ->
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
                    viewModel.editProductResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.editProductResponse.value = null
                }

                else -> {}
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

    }

    private fun getSubCategory(categoryId:String) {
        viewModel.getSubCategory(categoryId)
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
                variantList[selectedPos].variantImage.add(image)
                variantAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
            }
        }
        imageType = 0
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
            if (selectedBrand?.nameEnglish=="Other"){
                binding?.etOtherBrand?.isVisible = true
                binding?.etOtherBrandArabic?.isVisible = true

                binding?.tvOtherBrandArabic?.isVisible = true
                binding?.tvOtherBrand?.isVisible = true

                binding?.tvBrandArabic?.isVisible = true
                binding?.tvBrandEnglish?.isVisible = true

            }
            else{
                binding?.tvBrandArabic?.isVisible = false
                binding?.tvBrandEnglish?.isVisible = false

                binding?.etOtherBrand?.isVisible = false
                binding?.etOtherBrandArabic?.isVisible = false

                binding?.tvOtherBrand?.isVisible = false
                binding?.tvOtherBrandArabic?.isVisible = false
            }
            popup.dismiss()
        }
        popup.show()
    }

    private fun addEmpty() {
        val emptySubVariantList  = ArrayList<GetAllProductResponse.Data.AllProductData.Product.ProductVariation>()

        emptySubVariantList.add(GetAllProductResponse.Data.AllProductData.Product.ProductVariation(
            sizeId = "",
            bust = null,
            waist = null,
            hip = null,
            shoulder = null,
            sleeveLength = null,
            length = null,
            sku = "",
            skuThresholdValue = "",
            totalStock = "",
            actualPrice = "",
            finalPrice = "",
            discountType = "1",
            discountValue = "",
            createdAt = null,
            updatedAt = null,
            id = null,
            productId = null,
            status = ""
        ))

        variantList.add(
            GetAllProductResponse.Data.AllProductData.Product(
                colorId = "",
                variantImage = arrayListOf(),
                productVariation = emptySubVariantList
            )
        )
        variantAdapter?.notifyDataSetChanged()
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

        for ((index, product) in variantList.withIndex()) {

            if (product.variantImage.isEmpty()) {
                showToast("Please upload product image")
                return false
            }

            if (product.colorId.isEmpty()) {
                showToast("Please select color")
                return false
            }

            for ((innerIndex, v) in product.productVariation.withIndex()) {

                if (v.sizeId.isEmpty()) {
                    showToast("Please select size")
                    return false
                }

                if (selectedCategory?.name.equals("Women", ignoreCase = true)) {

                    if (v.bust?.isEmpty() == true) {
                        showToast("Please enter bust size")
                        return false
                    }
                    if (v.waist?.isEmpty() == true) {
                        showToast("Please enter waist size")
                        return false
                    }
                    if (v.hip?.isEmpty() == true) {
                        showToast("Please enter hip size")
                        return false
                    }
                    if (v.shoulder?.isEmpty() == true) {
                        showToast("Please enter shoulder size")
                        return false
                    }
                    if (v.sleeveLength?.isEmpty() == true) {
                        showToast("Please enter sleeve length")
                        return false
                    }
                    if (v.length?.isEmpty() == true) {
                        showToast("Please enter length")
                        return false
                    }
                }

                // Stock
                if (v.totalStock.isEmpty()) {
                    showToast("Please enter stock")
                    return false
                }

                // SKU
                if (v.sku.isEmpty()) {
                    showToast("Please enter SKU limit")
                    return false
                }

                val skuVal = v.sku.toIntOrNull()
                val stockVal = v.totalStock.toIntOrNull()

                if (skuVal == null) {
                    showToast("Invalid SKU limit")
                    return false
                }
                if (stockVal == null) {
                    showToast("Invalid stock")
                    return false
                }

                if (skuVal >= stockVal) {
                    showToast("SKU limit must be smaller than total stock")
                    return false
                }

                // Price
                if (v.actualPrice?.isEmpty() == true) {
                    showToast("Please enter actual price")
                    return false
                }
                if (v.discountValue.isEmpty()) {
                    showToast("Please enter discount")
                    return false
                }
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

        if (selectedBrand == null) {
            showToast("Please select  brand")
            return false
        }
        if(selectedBrand?.nameEnglish=="Other"&&binding?.etOtherBrand?.text.isNullOrEmpty()){
            showToast("Please enter brand name in english")
            return false
        }

        if(selectedBrand?.nameEnglish=="Other"&&binding?.etOtherBrandArabic?.text.isNullOrEmpty()){
            showToast("Please enter brand name in arabic")
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

            if (!isAllSpecificationValid()) {
                return false
            }

            if (!isAllVariantValid()) {
                return false
            }

            if(!validateMinMax()){
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
            if (sizeChartUrl.isNullOrEmpty()) {
                showToast("Please upload size chart")
                return false
            }
        }

        return true
    }

    private fun editProduct() {

        val colorIdList = variantList.map { it.colorId }
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
                otherEn = if(selectedBrand?.nameEnglish=="Other") etOtherBrand.text?.trim().toString() else null,
                otherAr = if(selectedBrand?.nameEnglish=="Other") etOtherBrandArabic.text?.trim().toString() else null,
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

                products = variantList,

                productSpeAr = specificationNameListAr,
                productSpeEn = specificationNameListEn,
                productSpeFeatureEn = specificationFeatureListEn,
                productSpeFeatureAr = specificationFeatureListAr,
                )
            viewModel.editProduct(request)
        }
    }
}