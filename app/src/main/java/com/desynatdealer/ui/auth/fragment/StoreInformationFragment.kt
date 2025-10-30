package com.desynatdealer.ui.auth.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentStoreInformationBinding
import com.desynatdealer.ui.UpdateLocationActivity
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.showToast
import java.io.File

class StoreInformationFragment : BaseFragment() {

    private var binding: FragmentStoreInformationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var imagePicker: ImagePicker
    private var storeImage: File? = null
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
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = "Store Information"
            CommonMethod.removeAllTextSpaces(etEmail)

            ccpCountryPicker.setCustomMasterCountries("KW,BH,QA,SA,AE,OM")

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
                ccpCountryPicker.launchCountrySelectionDialog()
            }

            ccpCountryPicker.setOnCountryChangeListener {
                val countryName = ccpCountryPicker.selectedCountryName
                selectedCountryCode = ccpCountryPicker.selectedCountryNameCode
                etCountry.setText(countryName)
            }


            etStoreAddress.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                locationResultLauncher.launch(intent)
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    val bundle = Bundle().apply {
                        putString("id", arguments?.getString("id").toString())
                    }
                    val req = hashMapOf(
                        "user_id" to arguments?.getString("id").toString(),
                        "store_image" to storePhotoUrl.toString(),
                        "store_name" to etStoreName.text.toString(),
                        "country" to getCountryIndex(selectedCountryCode),
                        "store_address" to address.toString(),
                        "address_line1" to etAddress1.text.toString(),
                        "address_line2" to etAddress2.text.toString(),
                        "store_phone" to etMobile.text.toString(),
                        "store_mail_id" to etEmail.text.toString(),
                        "bio" to etBio.text.toString(),
                        "let" to lat.toString(),
                        "long" to long.toString(),
                    )
                    PreviewFragment.storeInformationReq = req
                    findNavController().navigate(
                        R.id.action_storeInfoFragment_to_DocBankFragment,
                        bundle
                    )
                }
            }
        }
    }

    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val city = data?.getStringExtra("city") ?: "N/A"
            val street = data?.getStringExtra("street") ?: "N/A"
            val state = data?.getStringExtra("state") ?: "N/A"
            val pincode = data?.getStringExtra("pincode") ?: "N/A"
            val houseno = data?.getStringExtra("houseno") ?: "N/A"
            address = data?.getStringExtra("addressH") ?: "N/A"
            Constants.latitude = data?.getStringExtra("latitudeD")?.toDoubleOrNull() ?: 0.0
            Constants.longitude = data?.getStringExtra("longitudeD")?.toDoubleOrNull() ?: 0.0
            Constants.location = address.toString()
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            lat = data?.getDoubleExtra("lat", 0.0).toString()
            long = data?.getDoubleExtra("lng", 0.0).toString()
            binding?.etStoreAddress?.setText(address)
        }
    }


    override fun observe() {

        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
//                    binding.loader.setVisible()
                }

                is ApiResult.Success -> {
                    storePhotoUrl = result.data?.data?.url
                    viewModel.uploadResponse.value = null
                }

                is ApiResult.Error -> {
//                    binding.loader.setGone()
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
                    showToast("Please upload Profile Photo")
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

                mobile.length < 10 -> {
                    showToast("Please enter a valid mobile number")
                    false
                }

                email.isEmpty() -> {
                    showToast("Please enter your email address")
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


}