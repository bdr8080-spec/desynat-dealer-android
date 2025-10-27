package com.desynatdealer.ui.auth.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentStoreInformationBinding
import com.desynatdealer.ui.UpdateLocationActivity
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class StoreInformationFragment : BaseFragment() {
    private var binding: FragmentStoreInformationBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
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

            ccpCountryPicker.setCustomMasterCountries("KW,SA,BH,AE,OM,QA")

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    storeImage = CommonMethod.uriToFile(requireActivity(), it)
                    ivStoreImage.setImageURI(it)
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
                etCountry.setText(countryName)
            }


            etStoreAddress.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                locationResultLauncher.launch(intent)
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    findNavController().navigate(
                        R.id.action_storeInfoFragment_to_DocBankFragment
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
            val address = data?.getStringExtra("addressH") ?: "N/A"
            Constants.latitude = data?.getStringExtra("latitudeD")?.toDoubleOrNull() ?: 0.0
            Constants.longitude = data?.getStringExtra("longitudeD")?.toDoubleOrNull() ?: 0.0
            Constants.location = address
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            val lat = data?.getDoubleExtra("lat", 0.0)
            val lng = data?.getDoubleExtra("lng", 0.0)
            binding?.etStoreAddress?.setText(address)
        }
    }



    override fun observe() {
    }

    private fun validation(): Boolean {
        binding?.apply {
            return when {
                etStoreName.text.toString().isEmpty() -> {
                    showToast("Please enter store name")
                    false
                }

                else -> true
            }
        }
        return false
    }


}