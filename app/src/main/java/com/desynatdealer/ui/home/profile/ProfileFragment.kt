package com.desynatdealer.ui.home.profile

import android.content.Intent
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
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentProfileBinding
import com.desynatdealer.ui.auth.AuthActivity
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    private var binding : FragmentProfileBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var userInfo : GetSellerInfo.Data?=null

    private var storyList = ArrayList<GetStoryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
        getStory()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        userInfo = SharedPreference.get(requireActivity()).getUser()
        binding?.apply {

            toolbar.tvToolbar.text = "Profile"
            tvName.text = userInfo?.firstName +" "+userInfo?.lastName
            tvEmail.text = userInfo?.email
            ivProfileImage.load(userInfo?.profilePic){
                crossfade(false)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivProfileImage.setOnClickListener {
                if (storyList.size!=0) {
                    val bundle = Bundle().apply {
                        putString("from", "story")
                        putString("storyData", Gson().toJson(storyList))
                    }
                    findNavController().navigate(
                        R.id.action_profileFragment_to_viewStoryFragment,
                        bundle
                    )
                }
            }

            ivViewProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_viewProfileFragment)
            }

            rlInventory.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_inventoryProductManagementFragment)
            }

            rlAffiliate.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_affiliatedCommissionFragment)
            }

            rlBanner.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_bannerManagementFragment)
            }

            rlDeal.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_dealListedFragment)
            }

            rlLiveCoverage.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_liveCoverageFragment)
            }

            rlSubscription.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_mySubscriptionFragment)
            }

            relLogout.setOnClickListener {
                    viewModel.logout()
            }

//            rlFaq.setOnClickListener {
//                findNavController().navigate(R.id.action_profileFragment_to_faqFragment)
//            }

//            rlLanguage.setOnClickListener {
//                val bundle = Bundle().apply {
//                    putString("from", "profile")
//                }
//                findNavController().navigate(R.id.action_profileFragment_to_languageFragment,bundle)
//            }

            rlSupport.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_supportFragment)
            }

//            rlSetting.setOnClickListener {
//                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
//            }

//            rlRefund.setOnClickListener {
//                findNavController().navigate(R.id.action_profileFragment_to_refundFragment)
//            }

//            rlStaffManagement.setOnClickListener {
//                findNavController().navigate(R.id.action_profileFragment_to_staffManagementFragment)
//            }
        }
    }

    override fun observe() {

        viewModel.getStoryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    storyList.clear()
                    result?.data?.data?.let { storyList.addAll(it) }
                    setStory()
                    viewModel.getStoryResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    //       showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getStoryResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.logoutResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast("Logout successfully")
                    Constants.isLogout = true
                    CommonMethod.clearData(requireActivity())
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            AuthActivity::class.java)
                    )
                    requireActivity().finish()
                    viewModel.logoutResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.logoutResponse.value = null
                }
                else ->{

                }
            }
        }
    }

    private fun setStory() {
//        binding?.storyRing?.apply {
//            segmentCount = storyList.size
//            invalidate()
//        }
           binding?.ivHighlightStory?.isVisible = storyList.isNotEmpty()

    }

    private fun getStory() {
        val map = HashMap<String,Any>()

        viewModel.getStory(map)
    }

}