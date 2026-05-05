package com.desynatdealer.ui.home.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentEditProfileBinding
import com.desynatdealer.model.country.GetCountryResponse
import com.desynatdealer.ui.UpdateLocationActivity
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.CommonMethod.toMultiPartBody
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ImagePicker
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : BaseFragment() {
    private var binding : FragmentEditProfileBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var userInfo : GetSellerInfo.Data?=null
    private var selectedType = 1 //1-personal,2-bank
    private var imageType = -1
    private var clickType = -1
    private var isEdit = false
    private var isBank = false
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

    private var profileImage: String? = null
    private var storeImage: String? = null
    private var bankDoc: String? = null

    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var lat: String? = null
    private var long: String? = null
    private var address: String? = null
    private var selectedCountryCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProgressDialogUtils.getInstance().hideProgress()
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        setCountry()
        userInfo = SharedPreference.get(requireActivity()).getUser()
        setData()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.edit)
         //   setupScrollBlocker()
            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    val img = CommonMethod.uriToFile(requireActivity(), it)
                    when(imageType){
                        1->{
                            ivProfileImage.setImageURI(it)
                        }
                        2->{
                            ivStoreImage.setImageURI(it)
                        }
                        3->{
                            ivBank.setImageURI(it)
                        }
                    }

                    val image = img?.toMultiPartBody("file")
                    viewModel.upload(image)
                }
        }

            when(selectedType){
                1->{
                        selectedType = 1
                        tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.white))
                        tvBank.setTextColor(requireActivity().getColor(R.color.black))
                        tvPersonalInfo.setBackgroundResource(R.drawable.selected_left_tab)
                        tvBank.background = null
                        clPersonalInfo.isVisible = true
                        clBankInfo.isVisible = false
                    scrollView.post {
                       scrollView.smoothScrollTo(0, 0)
                    }
                    }

                 2-> {
                        selectedType = 2
                        tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                        tvBank.setTextColor(requireActivity().getColor(R.color.white))
                        tvBank.setBackgroundResource(R.drawable.selected_right_tab)
                        tvPersonalInfo.background = null
                        clPersonalInfo.isVisible = false
                        clBankInfo.isVisible = true
                     scrollView.post {
                         scrollView.smoothScrollTo(0, 0)
                     }
                    }
            }
        }
    }

    private fun setData() {
        binding?.apply {
            if(profileImage.isNullOrEmpty()) {
                ivProfileImage.load(userInfo?.profilePic) {
                    crossfade(false)
                    placeholder(R.drawable.ic_user)
                    error(R.drawable.ic_user)
                }
                profileImage = userInfo?.profilePic
            }

            if(storeImage.isNullOrEmpty()) {
                ivStoreImage.load(userInfo?.storeInfo?.storeImage) {
                    crossfade(false)
                    placeholder(R.color.grey)
                    error(R.color.grey)
                }
                storeImage = userInfo?.storeInfo?.storeImage
            }

            if(bankDoc.isNullOrEmpty()) {
                ivBank.load(userInfo?.sellerBankDetails?.uploadProof) {
                    crossfade(false)
                    placeholder(R.color.grey)
                    error(R.color.grey)
                }
                bankDoc = userInfo?.sellerBankDetails?.uploadProof
            }

            if (lat==null){
                lat = userInfo?.storeInfo?.let
                long = userInfo?.storeInfo?.long
            }

            etStoreName.setText(userInfo?.storeInfo?.storeName)
            etStoreEmail.setText(userInfo?.storeInfo?.storeMailId)
            etStoreMobile.setText(userInfo?.storeInfo?.storePhone)
            etStoreAddress.setText(userInfo?.storeInfo?.storeAddress)
            etAddress1.setText(userInfo?.storeInfo?.addressLine1)
            etAddress2.setText(userInfo?.storeInfo?.addressLine2)
            etBio.setText(userInfo?.storeInfo?.bio)
            tvStoreCountryPicker.text = userInfo?.storeInfo?.countryCode

            tvCountryPicker.text = userInfo?.countryCode
            etMobile.setText(userInfo?.mobile)
            etEmail.setText(userInfo?.email)
            etFirstName.setText(userInfo?.firstName)
            etLastName.setText(userInfo?.lastName)

            etIban.setText(userInfo?.sellerBankDetails?.iban)
            etSwift.setText(userInfo?.sellerBankDetails?.swift)
            etAccountHolderName.setText(userInfo?.sellerBankDetails?.accountHolderName)
            etBankName.setText(userInfo?.sellerBankDetails?.bankName)
            if (userInfo?.sellerBankDetails?.accountType=="0"){
                binding?.cbPersonal?.isChecked = true
            }
            else if (userInfo?.sellerBankDetails?.accountType=="1"){
                binding?.cbBusiness?.isChecked = true
            }
            if (selectedCountryCode.isNullOrEmpty()) {
                selectedCountryCode = userInfo?.storeInfo?.country
                binding?.etCountry?.setText(countryList?.find { it.id.toString() == selectedCountryCode }?.name)
            }
            lat = userInfo?.storeInfo?.let
            long = userInfo?.storeInfo?.long
            address = userInfo?.storeInfo?.storeAddress
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvStoreCountryPicker.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(),clMobile,countryList,true){countryData->
                    tvStoreCountryPicker.text = countryData.dialCode
                }
            }

            btnSaveChanges.setOnClickListener {
                when(selectedType){
                    1-> if(storeValidation()){updateStoreInfo()}
                    2-> if(bankValidation()){updateBankDetail()}
                }
            }

            tvChangeImage.setOnClickListener {
                imageType = 1
                imagePicker.showImageSourceDialog()
            }

            tvChangeStoreImage.setOnClickListener {
                imageType = 2
                imagePicker.showImageSourceDialog()
            }

            tvChangeEmail.setOnClickListener {
                val map = HashMap<String,Any>()
                map["for"] = "email" //mobile,email
                map["email"] = binding?.etEmail?.text?.trim().toString()
                map["type"] = 0 //0-old,1-new
                clickType = 1
                changeMobileOrEmail(map)
            }

            tvChangeNumber.setOnClickListener {
                val map = HashMap<String,Any>()
                map["for"] = "mobile" //mobile,email
                map["country_code"] = userInfo?.countryCode.toString()
                map["mobile"] = userInfo?.mobile.toString()
                map["type"] = 0 //0-old,1-new
                clickType = 0
                changeMobileOrEmail(map)
            }

            tvPersonalInfo.setOnClickListener {
                scrollView.post {
                    scrollView.smoothScrollTo(0, 0)
                }
                selectedType = 1
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvPersonalInfo.setBackgroundResource(R.drawable.selected_left_tab)
                tvBank.background = null
                clPersonalInfo.isVisible = true
                clBankInfo.isVisible = false
            }

            tvBank.setOnClickListener {
                scrollView.post {
                    scrollView.smoothScrollTo(0, 0)
                }
                selectedType = 2
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setBackgroundResource(R.drawable.selected_right_tab)
                tvPersonalInfo.background = null
                clPersonalInfo.isVisible = false
                clBankInfo.isVisible = true
            }

            etStoreAddress.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                intent.putExtra("savedLat", lat?.toDoubleOrNull())
                intent.putExtra("savedLng", long?.toDoubleOrNull())
                intent.putExtra("savedAddress", address)
                Log.e("send", "initControl: $address $lat $address" )
                locationResultLauncher.launch(intent)
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(),etCountry,countryList,false){countryData->
                    binding?.etCountry?.setText(countryData.name)
                    selectedCountryCode = countryData.id.toString()
                }            }


            ivBank.setOnClickListener {
                imageType = 3
                imagePicker.showImageSourceDialog()
            }

        }
    }

    override fun observe() {
        viewModel.uploadResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    when(imageType){
                        1->{
                            profileImage = result.data?.data?.url
                        }
                        2->{
                            storeImage = result.data?.data?.url
                        }
                        3->{
                            bankDoc = result.data?.data?.url
                        }
                    }

                    imageType = -1

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

        viewModel.updateStoreInfoResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    isEdit = true
                    val token = if (SharedPreference.get(requireActivity()).userType=="1") SharedPreference.get(requireActivity()).staffToken else SharedPreference.get(requireActivity()).accessToken

                    viewModel.getSellerInformation("Bearer ${token}")
                    viewModel.updateStoreInfoResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateStoreInfoResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.updateBankDetailResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())                  }

                is ApiResult.Success -> {
                    isBank = true
                    val token = if (SharedPreference.get(requireActivity()).userType=="1") SharedPreference.get(requireActivity()).staffToken else SharedPreference.get(requireActivity()).accessToken

                    viewModel.getSellerInformation("Bearer ${token}")
                    viewModel.updateBankDetailResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.updateBankDetailResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getSellerInfoResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    result.data?.data?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    if (isEdit) {
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.personal_information_updated_successfully)) {
                            isEdit = false
                            viewModel.sellerInfoResponse.value = null
                            findNavController().popBackStack()
                        }
                    }
                    else if (isBank) {
                        CommonMethod.showSuccessDialog(requireActivity(),
                            getString(R.string.bank_details_updated_successfully)) {
                            isBank = false
                            viewModel.getSellerInfoResponse.value = null
                            findNavController().popBackStack()
                        }
                    }
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getSellerInfoResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.changeNumberResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.data?.message.toString())
                    if (clickType==0){
                        val bundle = Bundle().apply {
                            putString("mobileNumber", userInfo?.mobile)
                            putString("countryCode", userInfo?.countryCode)
                            putString("from", "edit")
                           // putString("userId", response.data.id)
                        }
                        findNavController().navigate(R.id.action_editProfileFragment_to_otpFragment,bundle)
                        viewModel.changeNumberResponse.value = null
                    }
                    else if (clickType==1){
                        val bundle = Bundle().apply {
                            putString("email", userInfo?.email)
                            putString("from", "edit")
                      //      putString("userId",response.data.id)
                        }
                        findNavController().navigate(R.id.action_editProfileFragment_to_otpFragment,bundle)
                        viewModel.changeNumberResponse.value = null
                    }
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.changeNumberResponse.value = null
                }
                else ->{

                }
            }
        }

    }

   private fun updateStoreInfo(){
       binding?.apply {
           val map = HashMap<String, Any>()
           map["profile_pic"] = profileImage.toString()
           map["first_name"] = etFirstName.text.toString()
           map["last_name"] = etLastName.text.toString()
           map["store_name"] = etStoreName.text.toString()
           map["store_image"] = storeImage.toString()
           map["country_code"] = tvStoreCountryPicker.text.toString()
           map["country"] = selectedCountryCode.toString()
           map["store_address"] = etStoreAddress.text.toString()
           map["address_line1"] = etAddress1.text.toString()
           map["address_line2"] = etAddress2.text.toString()
           map["store_phone"] = etStoreMobile.text.toString()
           map["store_mail_id"] = etStoreEmail.text.toString()
           map["bio"] = etBio.text.toString()
           map["let"] = lat.toString()
           map["long"] = long.toString()
           viewModel.updateStoreInfo(map)
       }
    }

    private fun changeMobileOrEmail(map:HashMap<String,Any>){
        viewModel.changeNumber(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

    }
    private fun updateBankDetail(){
        binding?.apply {
            val map = HashMap<String, Any>()
            map["bank_name"] = etBankName.text.toString()
            map["account_holder_name"] = etAccountHolderName.text.toString()
            map["account_type"] = if (cbPersonal.isChecked) "0" else "1"
            map["user_id"] = userInfo?.id.toString()
            map["upload_proof"] = bankDoc.toString()
            map["swift"] = etSwift.text.toString()
            map["iban"] = etIban.text.toString()
            viewModel.updateBankDetail(map)
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
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            lat = (data?.getDoubleExtra("lat", 0.0)?:0.0).toString()
            long = (data?.getDoubleExtra("lng", 0.0)?:0.0).toString()

            Log.e("send", "getnew: $lat $long" )

            address = addressH

            val cleaned = cleanAddress(addressH, city, state)

            val (line1, line2) = splitIntoTwoLines(cleaned)

            binding?.etStoreAddress?.setText(address)
            binding?.etAddress1?.setText(line1)
            binding?.etAddress2?.setText(line2)

        }
    }

    private fun cleanAddress(addressH: String, city: String, state: String): String {
        var cleaned = addressH

        // Remove city & state if contained
        if (city.isNotBlank()) cleaned = cleaned.replace(city, "", true)
        if (state.isNotBlank()) cleaned = cleaned.replace(state, "", true)

        // Remove country patterns commonly seen
        cleaned = cleaned
            .replace("United Arab Emirates", "", true)
            .replace("UAE", "", true)

        // Remove lat/lng numeric patterns
        cleaned = cleaned.replace(Regex("\\b\\d+\\.\\d+\\b"), "")

        // Remove double spaces, extra hyphens, commas
        cleaned = cleaned
            .replace(", ,", ",")
            .replace(",,", ",")
            .replace("--", "-")
            .replace(" - ", "-")
            .trim().trim(',', '-', ' ')

        return cleaned
    }

    private fun splitIntoTwoLines(cleaned: String): Pair<String, String> {

        // Split by comma OR hyphen OR space-group
        val parts = cleaned.split(Regex("[,-]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (parts.size < 2) {
            return Pair(cleaned, "")   // fallback
        }

        val half = parts.size / 2

        val line1 = parts.take(half).joinToString(", ")
        val line2 = parts.drop(half).joinToString(", ")

        return Pair(line1, line2)
    }

    private fun setCountry() {
        val data = SharedPreference.get(requireActivity()).getCountryList()
        countryList.clear()
        if (data != null) {
            countryList.addAll(data)
        }
    }

    private fun bankValidation(): Boolean {
        binding?.apply {
            return when {
                etIban.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_iban_number))
                    false
                }

                etSwift.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_swift_bic_number))
                    false
                }

                etBankName.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_bank_name))
                    false
                }

                etAccountHolderName.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_account_holder_name))
                    false
                }

                bankDoc == null -> {
                    showToast(getString(R.string.please_upload_proof_of_account))
                    false
                }

                else -> true
            }
        }
        return false
    }

    private fun storeValidation(): Boolean {
        binding?.apply {
            val email = etEmail.text.toString().trim()

            val storeMobile = etStoreMobile.text.toString().trim()
            val storeEmail = etStoreEmail.text.toString().trim()

            return when {

                profileImage == null -> {
                    showToast(getString(R.string.please_upload_profile_image))
                    false
                }

                etFirstName.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_first_name))
                    false
                }

                etLastName.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_last_name))
                    false
                }

                email.isEmpty() -> {
                    showToast(getString(R.string.please_enter_email_address))
                    false
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showToast(getString(R.string.please_enter_a_valid_email_address))
                    false
                }


                storeImage == null -> {
                    showToast(getString(R.string.please_upload_store_image))
                    false
                }

                etStoreName.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_store_name))
                    false
                }

                etStoreAddress.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_select_address))
                    false
                }

                etAddress1.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_store_address_line_1))
                    false
                }

                etAddress2.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_store_address_line_2))
                    false
                }

                storeMobile.isEmpty() -> {
                    showToast(getString(R.string.please_enter_store_phone_number))
                    false
                }

                storeMobile.length < 7 -> {
                    showToast(getString(R.string.store_mobile_number_must_be_at_least_7_digits_long))
                    false
                }

                storeEmail.isEmpty() -> {
                    showToast(getString(R.string.please_enter_store_email_id))
                    false
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(storeEmail).matches() -> {
                    showToast(getString(R.string.please_enter_a_valid_store_email_address))
                    false
                }

                etBio.text.toString().isEmpty() -> {
                    showToast(getString(R.string.please_enter_bio))
                    false
                }

                else -> true
            }
        }
        return false
    }

    private fun setupScrollBlocker() {
        binding?.scrollView?.setOnScrollChangeListener { _, _, _, _, _ ->
            disableTabsWhileScrolling()
        }
    }

    private var scrollHandler = android.os.Handler()
    private var isScrolling = false

    private fun disableTabsWhileScrolling() {
        if (!isScrolling) {
            isScrolling = true
            setTabsEnabled(false)
        }

        scrollHandler.removeCallbacksAndMessages(null)

        scrollHandler.postDelayed({
            isScrolling = false
            setTabsEnabled(true)
        }, 200)
    }

    private fun setTabsEnabled(enable: Boolean) {
        binding?.apply {
            tvPersonalInfo.isEnabled = enable
            tvBank.isEnabled = enable
        }
    }

}