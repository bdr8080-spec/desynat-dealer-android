package com.desynatdealer.ui.home.bannerManagement

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.BannerAppprovalDialogBinding
import com.desynatdealer.databinding.FragmentAddBannerBinding
import com.desynatdealer.ui.home.bannerManagement.response.AddBannerRequest
import com.desynatdealer.ui.home.bannerManagement.response.GetBannerPriceResponse
import com.desynatdealer.ui.home.productManagement.response.GetCategoryResponse
import com.desynatdealer.ui.home.productManagement.response.GetSubCategoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddBannerFragment : BaseFragment() {
    private var binding : FragmentAddBannerBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var bannerPos = 1
    var startDate:String?=null
    var endDate:String?=null
    var currentTimeStamp = 0L
    var fromTimestamp = 0L
    var toTimestamp = 0L
    var days = 0
    var selectedCategory : GetCategoryResponse.Data?=null
    var selectedSubCategory : GetSubCategoryResponse.Data?=null
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data>()
    private var bannerPriceList = ArrayList<GetBannerPriceResponse.Data>()
    private lateinit var imagePicker: ImagePicker
    private var bannerImage: File? = null
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

    private var bannerUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBannerBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "Add New Banner"

            CommonMethod.applyGradientText(tvDays)
            CommonMethod.applyGradientText(tvTotalDays)
            setDefaultFromDate()

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    bannerImage = CommonMethod.uriToFile(requireActivity(), it)
                    ivBanner.setImageURI(it)
                    val image = bannerImage?.toMultiPartBody("image")
                    viewModel.upload(image)
                }
            }

            rgPosition.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.cbTop -> {
                        bannerPos = 1
                        tvSelectCategory.isVisible=false
                        etCategory.isVisible=false
                        tvSelectSubCategory.isVisible=false
                        etSubCategory.isVisible=false
                        resetBannerData()
                    }
                    R.id.cbBottom -> {
                        bannerPos = 2
                        tvSelectCategory.isVisible=false
                        etCategory.isVisible=false
                        tvSelectSubCategory.isVisible=false
                        etSubCategory.isVisible=false
                        resetBannerData()
                    }
                    R.id.cbCategory -> {
                        bannerPos = 3
                        tvSelectCategory.isVisible=true
                        etCategory.isVisible=true
                        resetBannerData()
                        if (categoryList.isEmpty()) {
                            getCategory()
                        }
                    }
                }
            }

        }
        getBannerPrice()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSendRequest.setOnClickListener {
                if (validation()){
                    addBanner()
                }
            }

            etCategory.setOnClickListener {
                showCategoryDropdown(etCategory)
            }

            etSubCategory.setOnClickListener {
                showSubCategoryDropdown(etSubCategory)
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = currentTimeStamp) { date, timestamp ->
                    etFrom.setText(date)
                    startDate = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                    fromTimestamp = normalizeTime(timestamp)
                    toTimestamp = 0
                    etTo.setText("")
                    days = 0
                }
            }

            etTo.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = fromTimestamp) { date, timestamp ->
                    etTo.setText(date)
                    endDate = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                    toTimestamp = normalizeTime(timestamp)
                    calculateDays()
                }
            }

            ivBanner.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }
        }
    }

    private fun addBanner() {
        if (startDate == null || endDate == null) {
            showToast("Please select date")
            return
        }

        val perDayPrice = bannerPriceList.firstOrNull()?.perDayPrice ?: 0.0
        val totalPrice = days * perDayPrice

        val request = AddBannerRequest(
            bannerPosition = bannerPos.toString(),
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            price = totalPrice.toString(),
            totalDays = days.toString(),
            uploadBanner = bannerUrl.toString(),
            category = if (bannerPos==3) selectedCategory?.id.toString() else null,
            subcatgory = if (bannerPos==3) selectedSubCategory?.id.toString() else null
        )

        viewModel.addBanner(request)
    }


    private fun getBannerPrice(){
        val map = HashMap<String,Any>()
        map["type"] = bannerPos
        viewModel.getBannerPrice(map)
    }

    override fun observe() {
        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    bannerUrl = result.data?.data?.url
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

        viewModel.addBannerResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    waitingDialog()
                    viewModel.addBannerResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addBannerResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getBannerPriceResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    bannerPriceList.clear()
                    result.data?.data?.let { bannerPriceList.addAll(it) }
                    setBannerPriceData()
                    viewModel.getBannerPriceResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getBannerPriceResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.categoryResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {

                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
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

    private fun setBannerPriceData() {
        binding?.apply {
            if (bannerPriceList.isNotEmpty() && days > 0) {
                val price = bannerPriceList[0].perDayPrice * days
                tvTotalPrice.text = "${CommonMethod.formatPrice(price)} KD"
                clDays.isVisible = true
                clPrice.isVisible = true
            }
        }
    }

    private fun resetBannerData() {
        binding?.apply {
            clPrice.isVisible = false
            clDays.isVisible = false
            etCategory.setText("")
            etSubCategory.setText("")
            endDate = null
            etTo.setText("")
            setDefaultFromDate()
            days = 0
            bannerUrl = null
            ivBanner.setImageResource(R.drawable.ic_upload_banner)
        }
    }

    private fun calculateDays() {
        if (fromTimestamp > 0 && toTimestamp > 0 && toTimestamp >= fromTimestamp) {
            val diff = toTimestamp - fromTimestamp
            days = ((diff / (1000 * 60 * 60 * 24)) + 1).toInt()
            if (days<10){
                binding?.tvTotalDays?.text = "0"+days.toString()
            }
            else{
                binding?.tvTotalDays?.text = days.toString()
            }

            setBannerPriceData()
        }
    }

    fun waitingDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = BannerAppprovalDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        CommonMethod.applyGradientText(dialogBinding.tvThanks)

        dialog.show()
            Handler(Looper.getMainLooper()).postDelayed({
                if (dialog.isShowing) {
                    findNavController().popBackStack()
                    dialog.dismiss()
                }
            }, 3000)

    }

    private fun getCategory(){
        viewModel.getCategory()
    }

    private fun showCategoryDropdown(view: AppCompatEditText) {
        val list = categoryList.map { it.name }
        val popup = androidx.appcompat.widget.ListPopupWindow(requireActivity())
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            selectedCategory = categoryList[pos]
            view.setText(selectedCategory?.name)
            getSubCategory()
            binding?.tvSelectSubCategory?.isVisible = true
            binding?.etSubCategory?.isVisible = true
            popup.dismiss()
        }
        popup.show()
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

    private fun getSubCategory() {
        viewModel.getSubCategory(selectedCategory?.id.toString())
    }

    private fun validation(): Boolean {

        if (binding?.rgPosition?.checkedRadioButtonId == -1) {
            showToast("Please select banner position")
            return false
        }

        if (binding?.cbCategory?.isChecked == true) {
            if (binding?.etCategory?.text.isNullOrBlank()) {
                showToast("Please select category")
                return false
            }
        }

        if (binding?.cbCategory?.isChecked == true) {
            if (binding?.etSubCategory?.text.isNullOrBlank()) {
                showToast("Please select sub-category")
                return false
            }
        }

        if (binding?.etFrom?.text.isNullOrBlank()) {
            showToast("Please select start date")
            return false
        }

        if (binding?.etTo?.text.isNullOrBlank()) {
            showToast("Please select end date")
            return false
        }

        if (binding?.tvTotalDays?.text.isNullOrBlank()) {
            showToast("Please select valid date range")
            return false
        }

        if (bannerUrl.isNullOrEmpty()) {
            showToast("Please upload banner image")
            return false
        }

        return true
    }

    private fun setDefaultFromDate() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        currentTimeStamp = cal.timeInMillis
        fromTimestamp = cal.timeInMillis

        val display = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
        val api = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

        startDate = api
        binding?.etFrom?.setText(display)
    }


    private fun normalizeTime(time: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}