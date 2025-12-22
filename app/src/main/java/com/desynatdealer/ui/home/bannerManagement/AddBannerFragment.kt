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
import androidx.activity.result.contract.ActivityResultContracts
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
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddBannerFragment : BaseFragment() {
    private var binding : FragmentAddBannerBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    var bannerPos = 0
    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp = 0L
    var toTimestamp = 0L
    var days = 0
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
                        getBannerPrice()
                    }
                    R.id.cbBottom -> {
                        bannerPos = 2
                        getBannerPrice()
                    }
                    R.id.cbCategory -> {
                        bannerPos = 3
                        getBannerPrice()
                    }
                }
            }

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSendRequest.setOnClickListener {
                addBanner()
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()){date,timestamp->
                    etFrom.setText(date)
                    startDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                    fromTimestamp=timestamp
                    calculateDays()
                }

            }
            etTo.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = fromTimestamp){date,timestamp->
                    etTo.setText(date)
                    endDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                    toTimestamp=timestamp
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
            category = null,
            subcatgory = null
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

    }

    private fun setBannerPriceData() {
        binding?.apply {
            if (bannerPriceList.isNotEmpty()) {
                val perDayPrice = bannerPriceList?.get(0)?.perDayPrice ?: 0.0
                val totalPrice = if (days > 0) days * perDayPrice else perDayPrice

                tvTotalPrice.text = "${CommonMethod.formatPrice(totalPrice)} KD"
                clDays.isVisible = true
            }
            else{
                clDays.isVisible = false
            }
        }
    }


    private fun calculateDays() {
        if (fromTimestamp > 0 && toTimestamp > 0 && toTimestamp >= fromTimestamp) {
            val diff = toTimestamp - fromTimestamp
            days = ((diff / (1000 * 60 * 60 * 24)) + 1).toInt()
            binding?.tvTotalDays?.text = days.toString()
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

}