package com.desynatdealer.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentEditProfileBinding
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : BaseFragment() {
    private var binding : FragmentEditProfileBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var userInfo : GetSellerInfo.Data?=null

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
        userInfo = SharedPreference.get(requireActivity()).getUser()
        setData()
        binding?.apply {
            toolbar.tvToolbar.text = "Edit"
        }
    }

    private fun setData() {
        binding?.apply {
            ivProfileImage.load(userInfo?.profilePic){
                crossfade(false)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            ivStoreImage.load(userInfo?.storeInfo?.storeImage){
                crossfade(false)
                placeholder(R.color.grey)
                error(R.color.grey)
            }

            ivBank.load(userInfo?.sellerBankDetails?.uploadProof){
                crossfade(false)
                placeholder(R.color.grey)
                error(R.color.grey)
            }

            etStoreName.setText(userInfo?.storeInfo?.storeName)
            etCountry.setText(userInfo?.storeInfo?.country)
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
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSaveChanges.setOnClickListener {

            }

            tvChangeImage.setOnClickListener {

            }

            tvChangeStoreImage.setOnClickListener {

            }

            tvPersonalInfo.setOnClickListener {
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvPersonalInfo.setBackgroundResource(R.drawable.selected_left_tab)
                tvBank.background = null
                clPersonalInfo.isVisible = true
                clBankInfo.isVisible = false
            }

            tvBank.setOnClickListener {
                tvPersonalInfo.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setBackgroundResource(R.drawable.selected_right_tab)
                tvPersonalInfo.background = null
                clPersonalInfo.isVisible = false
                clBankInfo.isVisible = true
            }
        }
    }

    override fun observe() {
    }


}