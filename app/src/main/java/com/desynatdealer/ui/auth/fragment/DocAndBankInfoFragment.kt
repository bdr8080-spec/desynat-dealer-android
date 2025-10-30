package com.desynatdealer.ui.auth.fragment

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
import com.desynatdealer.databinding.FragmentDocAndBankInfoBinding
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.showToast
import java.io.File

class DocAndBankInfoFragment : BaseFragment() {

    private var binding: FragmentDocAndBankInfoBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
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

    private var ownerDoc1Url: String? = null
    private var ownerDoc2Url: String? = null
    private var storeDoc1Url: String? = null
    private var storeDoc2Url: String? = null
    private var bankDocUrl: String? = null

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
                    when (imageType) {
                        1 -> {
                            ownerDoc1 = CommonMethod.uriToFile(requireActivity(), it)
                            ivDoc1.setImageURI(it)
                            val image = ownerDoc1?.toMultiPartBody("image")
                            viewModel.upload(image)
                        }

                        2 -> {
                            ownerDoc2 = CommonMethod.uriToFile(requireActivity(), it)
                            ivDoc2.setImageURI(it)
                            val image = ownerDoc2?.toMultiPartBody("image")
                            viewModel.upload(image)
                        }

                        3 -> {
                            storeDoc1 = CommonMethod.uriToFile(requireActivity(), it)
                            ivStoreDoc1.setImageURI(it)
                            val image = storeDoc1?.toMultiPartBody("image")
                            viewModel.upload(image)
                        }

                        4 -> {
                            storeDoc2 = CommonMethod.uriToFile(requireActivity(), it)
                            ivStoreDoc2.setImageURI(it)
                            val image = storeDoc2?.toMultiPartBody("image")
                            viewModel.upload(image)
                        }

                        5 -> {
                            bankDoc = CommonMethod.uriToFile(requireActivity(), it)
                            ivBank.setImageURI(it)
                            val image = bankDoc?.toMultiPartBody("image")
                            viewModel.upload(image)
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
                    val bundle = Bundle().apply {
                        putString("id", arguments?.getString("id").toString())
                    }
                    val req = StoreDocumentAndBankDetailsReq(
                        userId = arguments?.getString("id").toString(),
                        ownerDocument = arrayListOf(
                            ownerDoc1Url.toString(),
                            ownerDoc2Url.toString()
                        ),
                        storeDocument = arrayListOf(
                            storeDoc1Url.toString(),
                            storeDoc2Url.toString()
                        ),
                        accountType = if (cbPersonal.isChecked) "0" else "1",
                        iban = etIban.text.toString(),
                        swift = etSwift.text.toString(),
                        bankName = etBankName.text.toString(),
                        accountHolderName = etAccountHolderName.text.toString(),
                        uploadProof = bankDocUrl.toString()
                    )
                    PreviewFragment.storeDocumentAndBankDetailsReq = req
                    findNavController().navigate(
                        R.id.action_docAndBankFragment_to_previewFragment, bundle
                    )
                }

            }
        }
    }

    override fun observe() {

        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
//                    binding.loader.setVisible()
                }

                is ApiResult.Success -> {
                    when (imageType) {
                        1 -> ownerDoc1Url = result.data?.data?.url
                        2 -> ownerDoc2Url = result.data?.data?.url
                        3 -> storeDoc1Url = result.data?.data?.url
                        4 -> storeDoc2Url = result.data?.data?.url
                        5 -> bankDocUrl = result.data?.data?.url
                    }
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

    private fun validation(): Boolean {
        binding?.apply {
            return when {

                ownerDoc1Url == null -> {
                    showToast("Please upload owner document 1")
                    false
                }

                ownerDoc2Url == null -> {
                    showToast("Please upload owner document 2")
                    false
                }

                storeDoc1Url == null -> {
                    showToast("Please upload store document 1")
                    false
                }

                storeDoc2Url == null -> {
                    showToast("Please upload store document 2")
                    false
                }

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

                bankDocUrl == null -> {
                    showToast("Please upload proof of account")
                    false
                }

                else -> true
            }
        }
        return false
    }


}