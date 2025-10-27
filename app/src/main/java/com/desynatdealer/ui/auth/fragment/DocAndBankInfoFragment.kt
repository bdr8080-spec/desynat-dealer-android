package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.databinding.FragmentDocAndBankInfoBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DocAndBankInfoFragment : BaseFragment() {
    private var binding: FragmentDocAndBankInfoBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    private lateinit var imagePicker: ImagePicker
    private var ownerDoc1: File? = null
    private var ownerDoc2: File? = null
    private var storeDoc1: File? = null
    private var storeDoc2: File? = null
    private var bankDoc: File? = null
    private var imageType = 0
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
        binding = FragmentDocAndBankInfoBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Upload documents & Bank details"

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    when(imageType){
                        1->{
                            ownerDoc1 = CommonMethod.uriToFile(requireActivity(), it)
                            ivDoc1.setImageURI(it)
                        }
                        2->{
                            ownerDoc2 = CommonMethod.uriToFile(requireActivity(), it)
                            ivDoc2.setImageURI(it)
                        }

                        3->{
                            storeDoc1 = CommonMethod.uriToFile(requireActivity(), it)
                            ivStoreDoc1.setImageURI(it)
                        }
                        4->{
                            storeDoc2 = CommonMethod.uriToFile(requireActivity(), it)
                            ivStoreDoc2.setImageURI(it)
                        }
                        5->{
                            bankDoc = CommonMethod.uriToFile(requireActivity(), it)
                            ivBank.setImageURI(it)
                        }
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

            ivDoc1.setOnClickListener {
                imageType = 1
                imagePicker.showImageSourceDialog()
            }

            ivDoc2.setOnClickListener {
                imageType = 2
                imagePicker.showImageSourceDialog()
            }

            ivStoreDoc1.setOnClickListener {
                imageType = 3
                imagePicker.showImageSourceDialog()
            }

            ivStoreDoc2.setOnClickListener {
                imageType = 4
                imagePicker.showImageSourceDialog()
            }

  ivBank.setOnClickListener {
                imageType = 5
                imagePicker.showImageSourceDialog()
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    findNavController().navigate(
                        R.id.action_docAndBankFragment_to_previewFragment
                    )
                }

            }
        }
    }

    override fun observe() {
    }

    private fun validation(): Boolean {
        binding?.apply {
            return when {
                etIban.text.toString().isEmpty() -> {
                    showToast("Please enter iban")
                    false
                }
                etSwift.text.toString().isEmpty() -> {
                    showToast("Please enter swift")
                    false
                }
                etBankName.text.toString().isEmpty() -> {
                    showToast("Please enter bank name")
                    false
                }
                etAccountHolderName.text.toString().isEmpty() -> {
                    showToast("Please enter account holder name")
                    false
                }

                else -> true
            }
        }
        return false
    }


}