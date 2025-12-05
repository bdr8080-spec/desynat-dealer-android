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
import com.desynatdealer.databinding.FragmentViewProfileBinding
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileFragment : BaseFragment() {
    private var binding : FragmentViewProfileBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var userInfo : GetSellerInfo.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewProfileBinding.inflate(layoutInflater, container,false)
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

    }

    private fun setData() {
        binding?.apply {
            ivProfileImage.load(userInfo?.profilePic){
                crossfade(false)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            ivStore.load(userInfo?.storeInfo?.storeImage){
                crossfade(false)
                placeholder(R.color.grey)
                error(R.color.grey)
            }

            tvName.text = "${userInfo?.firstName} ${userInfo?.lastName}"
            tvMobile.text = "${userInfo?.countryCode} ${userInfo?.mobile}"
            tvEmail.text = userInfo?.email
            tvStore.text = userInfo?.storeInfo?.storeName
            tvStoreEmail.text = userInfo?.storeInfo?.storeMailId
            tvStoreMobile.text = "${userInfo?.storeInfo?.countryCode} ${userInfo?.storeInfo?.storePhone}"

        }
    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivEdit.setOnClickListener {
                findNavController().navigate(R.id.action_viewProfileFragment_to_editProfileFragment)
            }

            tvReel.setOnClickListener {
                tvReel.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setTextColor(requireActivity().getColor(R.color.black))
                tvReel.setBackgroundResource(R.drawable.selected_left_tab)
                tvBank.background = null
                rvReel.isVisible = true
                llBankInfo.isVisible = false
            }

            tvBank.setOnClickListener {
                tvReel.setTextColor(requireActivity().getColor(R.color.black))
                tvBank.setTextColor(requireActivity().getColor(R.color.white))
                tvBank.setBackgroundResource(R.drawable.selected_right_tab)
                tvReel.background = null
                llBankInfo.isVisible = true
                rvReel.isVisible = false
            }
        }
    }

    override fun observe() {
    }


}