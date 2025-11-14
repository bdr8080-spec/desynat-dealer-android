package com.desynatdealer.ui.auth.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.adapter.CountryPickerAdapter
import com.desynatdealer.adapter.CustomCountryPickerAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.data.view_model.DealerRegistrationViewModel
import com.desynatdealer.databinding.CustomCountryPickerBinding
import com.desynatdealer.databinding.FragmentStoreInformationBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.UpdateLocationActivity
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class StoreInformationFragment : BaseFragment() {

    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var binding: FragmentStoreInformationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private val sharedViewModel: DealerRegistrationViewModel by activityViewModels()
    private lateinit var imagePicker: ImagePicker
    private var storeImage: File? = null
    var userId:String?=null
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

    private var storePhotoUrl: String? = null
    private var lat: String? = null
    private var long: String? = null
    private var address: String? = null
    private var selectedCountryCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreInformationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCountry()
        sharedViewModel.storeInformation?.let { data ->
            storePhotoUrl = data["store_image"]
            lat = data["let"]
            long = data["long"]
            userId = data["user_id"]
            binding?.etStoreName?.setText(data["store_name"])
            binding?.etStoreAddress?.setText(data["store_address"])
            binding?.etMobile?.setText(data["store_phone"])
            binding?.tvCountryPicker?.text = data["country_code"]

            binding?.etEmail?.setText(data["store_mail_id"])
            binding?.etBio?.setText(data["bio"])
            binding?.ivStoreImage?.load(storePhotoUrl)


            selectedCountryCode = data["country"]
            binding?.etCountry?.setText(countryList?.find { it.id.toString() == data["country"] }?.name)
        }
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            if (arguments?.getString("id").toString()!=null&&arguments?.getString("id").toString()!="null"){
                userId=arguments?.getString("id").toString()
            }
            toolbar.tvToolbar.text = "Store Information"
            CommonMethod.removeAllTextSpaces(etEmail)
            CommonMethod.removeSpace(etStoreName)
            CommonMethod.removeSpace(etAddress1)
            CommonMethod.removeSpace(etAddress2)


            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    storeImage = CommonMethod.uriToFile(requireActivity(), it)
                    ivStoreImage.setImageURI(it)
                    val image = storeImage?.toMultiPartBody("image")
                    viewModel.upload(image)
                }
            }

        }
    }

    override fun initControl() {

        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivStoreImage.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            etCountry.setOnClickListener {
                showCustomCountryDropdown(etCountry)
             //   ccpCountryPicker.launchCountrySelectionDialog()
            }

            tvCountryPicker.setOnClickListener {
                showCustomDropdown(etMobile)
            }

            /*ccpCountryPicker.setOnCountryChangeListener {
                val countryName = ccpCountryPicker.selectedCountryName
                selectedCountryCode = ccpCountryPicker.selectedCountryNameCode
                etCountry.setText(countryName)
            }*/

            etStoreAddress.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                locationResultLauncher.launch(intent)
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        "Store information save successfully "
                    ) {
                        val bundle = Bundle().apply {
                            putString("id", userId.toString())
                        }

                        val req = hashMapOf(
                            "user_id" to userId.toString(),
                            "store_image" to storePhotoUrl.toString(),
                            "store_name" to etStoreName.text.toString(),
                            "country" to selectedCountryCode.toString(),
                            "store_address" to address.toString(),
                            "address_line1" to etAddress1.text.toString(),
                            "address_line2" to etAddress2.text.toString(),
                            "country_code" to tvCountryPicker.text.toString(),
                            "store_phone" to etMobile.text.toString(),
                            "store_mail_id" to etEmail.text.toString(),
                            "bio" to etBio.text.toString(),
                            "let" to lat.toString(),
                            "long" to long.toString(),
                        )
                        PreviewFragment.storeInformationReq = req
                        sharedViewModel.storeInformation = req
                        findNavController().navigate(
                            R.id.action_storeInfoFragment_to_DocBankFragment,
                            bundle
                        )
                    }
                }
            }
        }
    }

    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val city = data?.getStringExtra("city") ?: ""
            val street = data?.getStringExtra("street") ?: ""
            val state = data?.getStringExtra("state") ?: ""
            val pincode = data?.getStringExtra("pincode") ?: ""
            val houseno = data?.getStringExtra("houseno") ?: ""
            address = data?.getStringExtra("addressH") ?: ""
            Constants.latitude = data?.getStringExtra("latitudeD")?.toDoubleOrNull() ?: 0.0
            Constants.longitude = data?.getStringExtra("longitudeD")?.toDoubleOrNull() ?: 0.0
            Constants.location = address.toString()
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            lat = data?.getStringExtra("latitudeD")?.toDoubleOrNull().toString()
            long = data?.getStringExtra("longitudeD")?.toDoubleOrNull().toString()
            binding?.etStoreAddress?.setText(address)

            val addressLine1 = "$houseno, $street".trim().trim(',')
            val addressLine2 = "$city, $state - $pincode".trim().trim(',')

            binding?.etAddress1?.setText(addressLine1)
            binding?.etAddress2?.setText(addressLine2)

            val country = getCountryName(requireActivity(), Constants.latitude,Constants.longitude)
         //   binding?.etCountry?.setText(country)
        }
    }

    fun getCountryName(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            var result: Address

            if (addresses != null && !addresses.isEmpty()) {
                return addresses[0].countryName
            }
            return null
        } catch (ignored: IOException) {
            return ""
        }
    }

    override fun observe() {

        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    storePhotoUrl = result.data?.data?.url
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

    }

    private fun getCountryIndex(countryCode: String?): String {
        return when (countryCode) {
            "KW" -> "1"
            "BH" -> "2"
            "QA" -> "3"
            "SA" -> "4"
            "AE" -> "5"
            "OM" -> "6"
            else -> "0"
        }
    }

    private fun validation(): Boolean {
        binding?.apply {
            val mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()

            return when {

                storePhotoUrl == null -> {
                    showToast("Please upload store image")
                    false
                }

                etStoreName.text.toString().isEmpty() -> {
                    showToast("Please enter store name")
                    false
                }

                address == null -> {
                    showToast("Please select address")
                    false
                }

                etAddress1.text.toString().isEmpty() -> {
                    showToast("Please enter store address line 1")
                    false
                }

                etAddress2.text.toString().isEmpty() -> {
                    showToast("Please enter store address line 2")
                    false
                }

                mobile.isEmpty() -> {
                    showToast("Please enter store phone number")
                    false
                }

                mobile.length < 7 -> {
                    showToast("Number must be at least 7 digits long")
                    false
                }

                email.isEmpty() -> {
                    showToast("Please enter store email id")
                    false
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showToast("Please enter a valid email address")
                    false
                }

                etBio.text.toString().isEmpty() -> {
                    showToast("Please enter bio")
                    false
                }

                else -> true
            }
        }
        return false
    }

    private fun setCountry() {
         val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        if (data != null) {
            countryList.addAll(data)
        }
        if (!countryList.isNullOrEmpty()) {
            val countryCodes = countryList?.map { it.isoCode }
            val countryCodeString = countryCodes?.joinToString(",")
          //  binding?.ccp?.setCustomMasterCountries(countryCodeString)
        //    binding?.ccpCountryPicker?.setCustomMasterCountries(countryCodeString)

        }
    }

    private fun showCustomDropdown(anchor: View) {

        val dropdownBinding = CustomCountryPickerBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            dropdownBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f

        popupWindow.showAsDropDown(anchor)

        dropdownBinding.apply {

            rvCountry.adapter = CountryPickerAdapter(requireActivity(),countryList,object :
                CountryPickerAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    binding?.tvCountryPicker?.text = countryList[position].dialCode
                    popupWindow.dismiss()
                }

            })
        }
    }

    private fun showCustomCountryDropdown(anchor: View) {

        val dropdownBinding = CustomCountryPickerBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            dropdownBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f

        popupWindow.showAsDropDown(anchor)

        dropdownBinding.apply {

            rvCountry.adapter = CustomCountryPickerAdapter(requireActivity(),countryList,object :
                CustomCountryPickerAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    binding?.etCountry?.setText(countryList[position].name)
                    selectedCountryCode = countryList[position].id.toString()
                    popupWindow.dismiss()
                }

            })
        }
    }


}