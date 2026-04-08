package com.desynatdealer.ui.home.staffManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.AccessModuleAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddStaffBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class AddStaffFragment : BaseFragment() {
    private var binding : FragmentAddStaffBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isEdit: Boolean = false

    private var accessModuleAdapter: AccessModuleAdapter?=null
    private var accessModuleList = ArrayList<String>()
    private var profileImage: File? = null
    private var profileImageUrl: String?=null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStaffBinding.inflate(layoutInflater, container,false)
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

            CommonMethod.applyArabicOnlyFilter(etFullNameArabic)
            CommonMethod.removeSpace(etFullNameEnglish)
            CommonMethod.removeSpace(etEmailID)

            if (isEdit){
                toolbar.tvToolbar.text = "Edit"
                tvAdd.text = "Save Changes"
                ivProfileImage.isVisible = true
                tvChangeImage.isVisible = true
                ivProfile.isVisible = false
            }
            else{
                toolbar.tvToolbar.text = "Add New Staff"
                tvAdd.text = "Add"
                ivProfileImage.isVisible = false
                tvChangeImage.isVisible = false
                ivProfile.isVisible = true
            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    profileImage = CommonMethod.uriToFile(requireActivity(), it)
                    if (isEdit){
                        ivProfileImage.setImageURI(it)
                    }
                    else{
                        ivProfile.setImageURI(it)
                    }
                    val image = profileImage?.toMultiPartBody("file")
                    viewModel.upload(image)
                }
            }

            accessModuleAdapter = AccessModuleAdapter(requireActivity(),accessModuleList,object :AccessModuleAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {

                }

            })
            rvAccessModule.adapter = accessModuleAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvChangeImage.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            ivProfile.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            tvCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            tvAdd.setOnClickListener {

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
                    profileImageUrl = result.data?.data?.url
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

}