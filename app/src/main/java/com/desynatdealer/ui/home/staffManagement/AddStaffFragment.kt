package com.desynatdealer.ui.home.staffManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.adapter.AddAccessModuleAdapter
import com.desynatdealer.adapter.StaffRoleAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentAddStaffBinding
import com.desynatdealer.ui.home.staffManagement.response.AddNewStaffRequest
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
import com.desynatdealer.ui.home.staffManagement.response.GetSellerAllMenuResponse
import com.desynatdealer.ui.home.staffManagement.response.GetStaffCategoryResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.desynatdealer.utils.togglePasswordVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class AddStaffFragment : BaseFragment() {
    private var binding : FragmentAddStaffBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isEdit: Boolean = false

    private var accessModuleAdapter: AddAccessModuleAdapter?=null
    private var accessModuleList = ArrayList<AddNewStaffRequest.MenuId>()
    private var previousAccessModuleList = ArrayList<GetAllStaffResponse.Data.Data.RoleAction>()

    private var staffData: GetAllStaffResponse.Data.Data?=null

    private var staffRoleAdapter: StaffRoleAdapter?=null
    private var staffRoleList = ArrayList<GetStaffCategoryResponse.Data>()
    private var staffRoleId: String=""
    private var profileImage: File? = null
    private var profileImageUrl: String?=null
    private var image: MultipartBody.Part?=null
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

        val itemJson = arguments?.getString("staffData")
        if (!itemJson.isNullOrEmpty()) {
            staffData = Gson().fromJson(itemJson, GetAllStaffResponse.Data.Data::class.java)
            setData()
        }
        binding?.apply {

            CommonMethod.applyArabicOnlyFilter(etFullNameArabic)
            CommonMethod.removeSpace(etFullNameEnglish)
            CommonMethod.removeSpace(etEmailID)

            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)

            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
            if (SharedPreference.get(requireActivity()).userType=="1"&&SharedPreference.get(requireActivity()).getStaff()?.id.toString()==staffData?.id.toString()) {
                tvPassword.isVisible = false
                tvConfirmPassword.isVisible = false
                relPassword.isVisible = false
                relConfirmPassword.isVisible = false
            }

            if (staffData!=null){
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
                    image = profileImage?.toMultiPartBody("file")

                }
            }

            staffRoleAdapter = StaffRoleAdapter(requireActivity(),staffRoleList,object :
                StaffRoleAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {
                    staffRoleId = staffRoleList[position].id
                }

            })
            rvRole.adapter = staffRoleAdapter

            accessModuleAdapter = AddAccessModuleAdapter(requireActivity(),accessModuleList,object :AddAccessModuleAdapter.OnClick{
                override fun ClickOnItem(position: Int, type: String) {
                    val item = accessModuleList[position]

                    when (type) {

                        "module" -> {
                            item.isSelected = !item.isSelected

                            if (!item.isSelected) {
                                item.canView = 0
                                item.canAction = 0
                            }
                        }

                        "view" -> {
                            item.isSelected = true
                            item.canView = if (item.canView == 1) 0 else 1

                            if (item.canView == 1) {
                                item.canAction = 0
                            }
                        }

                        "action" -> {
                            item.isSelected = true
                            item.canAction = if (item.canAction == 1) 0 else 1

                            if (item.canAction == 1) {
                                item.canView = 0
                            }
                        }
                    }

                    accessModuleAdapter?.notifyItemChanged(position)
                }

            })
            rvAccessModule.adapter = accessModuleAdapter
        }

        viewModel.getStaffRole()
        viewModel.getSellerAllMenu()
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
                if (isValid()){
                    if (profileImage!=null){
                        viewModel.upload(image)
                    }
                    else{
                        addStaff()
                    }

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
                    profileImageUrl = result.data?.data?.url
                    addStaff()
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

        viewModel.getStaffRoleResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    staffRoleList.clear()
                    result?.data?.data?.let { staffRoleList.addAll(it) }
                    staffRoleAdapter?.notifyDataSetChanged()
                    for (i in 0 until staffRoleList.size){
                        if (staffRoleList[i].id==staffRoleId){
                            staffRoleAdapter?.setSelected(i)
                        }
                    }
                    viewModel.getStaffRoleResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getStaffRoleResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getSellerAllMenuResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    result?.data?.data?.let { addAccessModuleData(it) }
                    viewModel.getSellerAllMenuResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getSellerAllMenuResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.addStaffResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireActivity(),"Staff added successfully !"){
                        findNavController().popBackStack()
                    }
                    viewModel.addStaffResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.addStaffResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.updateStaffResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (SharedPreference.get(requireActivity()).userType=="1"&&SharedPreference.get(requireActivity()).getStaff()?.id.toString()==staffData?.id.toString()) {
                        CommonMethod.showSuccessDialog(requireActivity(),"Profile updated successfully !"){
                            findNavController().popBackStack()
                        }
                    }

                    else{
                        CommonMethod.showSuccessDialog(requireActivity(),"Staff updated successfully !"){
                            findNavController().popBackStack()
                        }
                    }

                    viewModel.updateStaffResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateStaffResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun addStaff(){
        val selectedModules = accessModuleList.filter { it.isSelected }

        val request = AddNewStaffRequest(
            countryCode = binding?.tvCountryPicker?.text.toString(),
            email = binding?.etEmailID?.text.toString(),
            fullName = binding?.etFullNameEnglish?.text.toString(),
            nameAr = binding?.etFullNameArabic?.text.toString(),
            mobile = binding?.etMobile?.text.toString(),
            password = binding?.etPassword?.text.toString(),
            profilePic = profileImageUrl.toString(),
            selectCategory = staffRoleId,
            menuId = selectedModules,
            id = if (staffData!=null) staffData?.id.toString() else null
        )
        if (staffData!=null) {
            viewModel.updateStaff(request)
        }
        else{
            viewModel.addNewStaff(request)
        }
    }

    private fun setData(){
        binding?.apply {
            profileImageUrl = staffData?.profilePic.toString()
            previousAccessModuleList.clear()
            Glide.with(requireActivity()).load(profileImageUrl).into(ivProfile)
            Glide.with(requireActivity()).load(profileImageUrl).into(ivProfileImage)
            staffData?.roleAction?.let { previousAccessModuleList.addAll(it) }
            etEmailID.setText(staffData?.email)
            etMobile.setText(staffData?.mobile)
            etPassword.setText(staffData?.savePassword)
            etConfirmPassword.setText(staffData?.savePassword)
            etFullNameEnglish.setText(staffData?.firstName)
          //  etFullNameEnglish.setText(staffData?.firstName+" "+staffData?.lastName)
            etFullNameArabic.setText(staffData?.nameAr)
            staffRoleId = staffData?.selectCategory.toString()
        }
    }

    private fun addAccessModuleData(data: List<GetSellerAllMenuResponse.Data>) {
        accessModuleList.clear()

        for (module in data) {

                val matched = previousAccessModuleList.firstOrNull {
                    it.menuId == module.id
                }

            if (SharedPreference.get(requireActivity()).userType=="1") {
                if (matched != null) {
                    accessModuleList.add(
                        AddNewStaffRequest.MenuId(
                            title = module.title,
                            canView = matched.canView,
                            canAction = matched.canAction,
                            id = module.id,
                            isSelected = true
                        )
                    )
                }
            }
            else{
                accessModuleList.add(
                    AddNewStaffRequest.MenuId(
                        title = module.title,
                        canView = matched?.canView ?: 0,
                        canAction = matched?.canAction ?: 0,
                        id = module.id,
                        isSelected = matched != null
                    )
                )
            }
        }

        accessModuleAdapter?.notifyDataSetChanged()
    }

    private fun isValid(): Boolean {

        binding?.apply {

            if (profileImage == null&&profileImageUrl==null) {
                showToast("Please Upload Profile Image")
                return false
            }
            if (etFullNameEnglish.text.toString().trim().isEmpty()) {
                showToast("Please Enter Full Name in English")
                return false
            }

            if (etFullNameArabic.text.toString().trim().isEmpty()) {
                showToast("Please Enter Full Name in Arabic")
                return false
            }

            val email = etEmailID.text.toString().trim()
            if (email.isEmpty()) {
                showToast("Please Enter Email Address")
                return false
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Please Enter Valid Email Address")
                return false
            }
            val mobile = etMobile.text.toString().trim()
            if (mobile.isEmpty()) {
                showToast("Please Enter Mobile Number")
                return false
            }
            if (mobile.length < 7 ) {
                showToast("Mobile Number in Between 7-15 digits ")
                return false
            }
            if (staffRoleId.isEmpty()) {
                showToast("Please Select Role")
                return false
            }
            val pass = etPassword.text.toString()
            if (pass.isEmpty()) {
                showToast("Please Enter Password")
                return false
            }
            if (pass.length<6) {
                showToast("Password must be 6 digits long")
                return false
            }

            val cPass = etConfirmPassword.text.toString()
            if (cPass.isEmpty()) {
                showToast("Please Enter Confirm Password")
                return false
            }

            if (pass != cPass) {
                showToast("Password does not match")
                return false
            }
            val selectedModules = accessModuleList.filter { it.isSelected }
            if (selectedModules.isEmpty()) {
                showToast("Please select at-least one module")
                return false
            }

        }

        return true
    }

}