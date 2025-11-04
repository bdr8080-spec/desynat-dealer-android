package com.desynatdealer.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.AuthViewModel
import com.desynatdealer.data.view_model.DealerRegistrationViewModel
import com.desynatdealer.databinding.FragmentDocAndBankInfoBinding
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq
import com.desynatdealer.ui.auth.fragment.PreviewFragment.Companion.storeDocumentAndBankDetailsReq
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.FilePickerPicker
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.setImage
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DocAndBankInfoFragment : BaseFragment() {

    private var binding: FragmentDocAndBankInfoBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private val sharedViewModel: DealerRegistrationViewModel by activityViewModels()

    private lateinit var imagePicker: ImagePicker
    private lateinit var filePicker: FilePickerPicker
    private var ownerDoc1: File? = null
    private var ownerDoc2: File? = null
    private var storeDoc1: File? = null
    private var storeDoc2: File? = null
    private var bankDoc: File? = null
    private var imageType = 0
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



    private val cameraLauncherFile =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            filePicker.onCameraResult(success)
        }

    private val galleryLauncherFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            filePicker.onGalleryResult(uri)
        }

    private val permissionLauncherFile =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            filePicker.onPermissionResult(granted)
        }

    private val pdfLauncherFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        filePicker.onPdfResult(uri)
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
        sharedViewModel.documentAndBankDetails?.let { data ->
            bankDocUrl = data.uploadProof
            ownerDoc1Url = data.ownerDocument.getOrNull(0)
            ownerDoc2Url = data.ownerDocument.getOrNull(1)
            storeDoc1Url = data.storeDocument.getOrNull(0)
            storeDoc2Url = data.storeDocument.getOrNull(1)
            binding?.etIban?.setText(data.iban)
            binding?.etSwift?.setText(data.swift)
            binding?.ivBank?.load(bankDocUrl)
            binding?.ivDoc1?.load(ownerDoc1)
            binding?.ivDoc2?.load(ownerDoc2)
            binding?.ivStoreDoc1?.load(storeDoc1)
            binding?.ivStoreDoc2?.load(storeDoc2)

            binding?.ivBank?.setImage(storeDocumentAndBankDetailsReq.uploadProof)
            binding?.ivDoc1?.setImage(storeDocumentAndBankDetailsReq.ownerDocument.getOrNull(0))
            binding?.ivDoc2?.setImage(storeDocumentAndBankDetailsReq.ownerDocument.getOrNull(1))
            binding?.ivStoreDoc1?.setImage(storeDocumentAndBankDetailsReq.storeDocument.getOrNull(0))
            binding?.ivStoreDoc2?.setImage(storeDocumentAndBankDetailsReq.storeDocument.getOrNull(1))
            binding?.etAccountHolderName?.setText(data.accountHolderName)
            binding?.etBankName?.setText(data.bankName)
            userId = data.userId
            if (data.accountType=="0"){
                binding?.cbPersonal?.isChecked = true
            }
            else if (data.accountType=="1"){
                binding?.cbBusiness?.isChecked = true
            }
        }
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            if (arguments?.getString("id").toString()!=null&&arguments?.getString("id").toString()!="null"){
                userId = arguments?.getString("id").toString()
            }
            CommonMethod.removeSpace(etBankName)
            CommonMethod.removeSpace(etAccountHolderName)
            CommonMethod.removeSpace(etSwift)
            CommonMethod.removeSpace(etIban)
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


            filePicker = FilePickerPicker(
                context = requireContext(),
                cameraLauncher = cameraLauncherFile,
                galleryLauncher = galleryLauncherFile,
                filePickerLauncher = pdfLauncherFile,
                permissionLauncher = permissionLauncherFile,
            ) { uri ->
                uri?.let {
                    when (imageType) {

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
                filePicker.showFileSourceDialog()
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    val bundle = Bundle().apply {
                        putString("id", userId.toString())
                    }
                    val req = StoreDocumentAndBankDetailsReq(
                        userId = userId.toString(),
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
                    sharedViewModel.documentAndBankDetails = req
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
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    when (imageType) {
                        1 -> ownerDoc1Url = result.data?.data?.url
                        2 -> ownerDoc2Url = result.data?.data?.url
                        3 -> storeDoc1Url = result.data?.data?.url
                        4 -> storeDoc2Url = result.data?.data?.url
                        5 -> {
                            bankDocUrl = result.data?.data?.url
                            binding?.ivBank?.load(bankDocUrl)
                        }
                    }
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

               /* etIban.text.toString().isEmpty() -> {
                    showToast("Please enter IBAN")
                    false
                }

                etSwift.text.toString().isEmpty() -> {
                    showToast("Please enter SWIFT/BIC")
                    false
                }*/

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