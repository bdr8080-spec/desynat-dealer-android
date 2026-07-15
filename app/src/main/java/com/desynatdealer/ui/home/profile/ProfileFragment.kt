package com.desynatdealer.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isInvisible
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
import com.desynatdealer.ui.home.staffManagement.response.GetAllStaffResponse
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
    private var staffData: GetAllStaffResponse.Data.Data?=null


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
        getSellerInfo()
        getSellerInfo()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            val navController = findNavController()

            if (!navController.popBackStack()) {
                navController.navigate(R.id.home)
            }
        }
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        userInfo = SharedPreference.get(requireActivity()).getUser()
        setData()

        if (SharedPreference.get(requireActivity()).userType=="1"){
            getStaffDetail()
        }
    }

    private fun setData() {
        binding?.apply {

            toolbar.tvToolbar.text = getString(R.string.profile)
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
                val navController = findNavController()

                if (!navController.popBackStack()) {
                    navController.navigate(R.id.home)
                }
            }

            ivProfileImage.setOnClickListener {
                if (SharedPreference.get(requireActivity()).userType=="1"){
                    if (storyList.size != 0) {
                        val bundle = Bundle().apply {
                            putString("from", "story")
                            putString("storyData", Gson().toJson(storyList))
                        }
                        findNavController().navigate(
                            R.id.action_profileFragment_to_viewStoryFragment,
                            bundle
                        )
                    }
                    else {
                        val bundle = Bundle().apply {
                            putString("staffId", userInfo?.id.toString())
                        }
                        findNavController().navigate(
                            R.id.action_profileFragment_to_staffDetailFragment,
                            bundle
                        )
                    }
                }
                else {
                    if (storyList.size != 0) {
                        val bundle = Bundle().apply {
                            putString("from", "story")
                            putString("storyData", Gson().toJson(storyList))
                        }
                        findNavController().navigate(
                            R.id.action_profileFragment_to_viewStoryFragment,
                            bundle
                        )
                    }
                    else{
                        findNavController().navigate(R.id.action_profileFragment_to_viewProfileFragment)
                    }
                }
            }

            ivViewProfile.setOnClickListener {
                if (SharedPreference.get(requireActivity()).userType=="1"){
                    val bundle = Bundle().apply {
                        putString("staffId", userInfo?.id.toString())
                    }
                    findNavController().navigate(R.id.action_profileFragment_to_staffDetailFragment,bundle)
                }
                else{
                    findNavController().navigate(R.id.action_profileFragment_to_viewProfileFragment)
                }
            }

            rlInventory.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_inventoryProductManagementFragment)
            }

            rlAffiliate.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_affiliatedCommissionFragment)
            }

            rlStory.setOnClickListener {
                findNavController().navigate(R.id.action_profile_to_storyManagementFragment)
            }

            rlBanner.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_bannerManagementFragment)
            }

            rlDeal.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_dealListedFragment)
            }

            rlProduct.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_productManagementFragment)
            }

            rlOrder.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_orderManagementFragment)
            }

            rlLiveEvent.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_eventFragment)
            }

            rlPayment.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_paymentFragment)
            }

            rlLiveCoverage.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_liveCoverageFragment)
            }

            rlSubscription.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_mySubscriptionFragment)
            }

            relLogout.setOnClickListener {
                val token = if (SharedPreference.get(requireActivity()).userType=="1") SharedPreference.get(requireActivity()).staffToken else SharedPreference.get(requireActivity()).accessToken

                viewModel.logout("Bearer ${token}")
            }

            rlFaq.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_faqFragment)
            }

            rlTerms.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("type", 1)
                }
                findNavController().navigate(R.id.action_profileFragment_to_cmsContentFragment,bundle)
            }

            relAbout.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("type", 3)
                }
                findNavController().navigate(R.id.action_profileFragment_to_cmsContentFragment,bundle)
            }

            rlCancellationPolicy.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("type", 6)
                }
                findNavController().navigate(R.id.action_profileFragment_to_cmsContentFragment,bundle)
            }

            rlExchange.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("type", 5)
                }
                findNavController().navigate(R.id.action_profileFragment_to_cmsContentFragment,bundle)
            }

            rlReturnPolicy.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("type", 4)
                }
                findNavController().navigate(R.id.action_profileFragment_to_cmsContentFragment,bundle)
            }

            rlLanguage.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("from", "profile")
                }
                findNavController().navigate(R.id.action_profileFragment_to_languageFragment,bundle)
            }

            rlSupport.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_supportFragment)
            }

            rlSetting.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
            }

            rlRefund.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_refundFragment)
            }

            rlStaffManagement.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_staffManagementFragment)
            }
        }
    }

    override fun observe() {

        viewModel.getSellerInfoResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    result.data?.data?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    userInfo = result.data?.data
                    setData()
                    viewModel.getSellerInfoResponse.value = null
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
                    showToast(getString(R.string.logout_successfully))
                    Constants.isLogout = true
                    SharedPreference.get(requireActivity()).isSubscription = false
                    SharedPreference.get(requireActivity()).accessToken = ""
                    SharedPreference.get(requireActivity()).staffToken = ""
                    SharedPreference.get(requireActivity()).userType = ""
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

        viewModel.getStaffDetailResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    staffData = result.data?.data
                    staffData?.let { SharedPreference.get(requireActivity()).saveStaff(it) }
                    showMenuOptionForStaff()
                    viewModel.getStaffDetailResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getStaffDetailResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun setStory() {
//        binding?.storyRing?.apply {
//            segmentCount = storyList.size
//            invalidate()
//        }
        binding?.apply {
            if (storyList.size!=0){
                binding?.ivHighlightStory?.isVisible = true
            }
            else{
                binding?.ivHighlightStory?.isInvisible = true
            }
        }
    }

    private fun getStory() {
        val map = HashMap<String,Any>()

        viewModel.getStory(map)
    }

    private fun getSellerInfo() {
        val map = HashMap<String,Any>()
        val token = if (SharedPreference.get(requireActivity()).userType=="1") SharedPreference.get(requireActivity()).staffToken else SharedPreference.get(requireActivity()).accessToken

        viewModel.getSellerInformation("Bearer ${token}")
    }


    private fun showMenuOptionForStaff() {
        binding?.apply {

            tvName.text = staffData?.firstName
            tvEmail.text = staffData?.email

            ivProfileImage.load(staffData?.profilePic) {
                crossfade(false)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            rlInventory.isVisible = hasPermission("seller.Inventory_Management")
            rlStaffManagement.isVisible = hasPermission("seller.staff-management")
            rlAffiliate.isVisible = hasPermission("seller.update_commison")
            rlTerms.isVisible = hasPermission("seller.termCondition")
            relAbout.isVisible = hasPermission("seller.aboutUs")
            rlFaq.isVisible = hasPermission("seller.faq")
            rlReturnPolicy.isVisible = hasPermission("seller.returnPolicy")
            rlExchange.isVisible = hasPermission("seller.exchangePolicy")
            rlCancellationPolicy.isVisible = hasPermission("seller.cancellationPolicy")
            rlRefund.isVisible = hasPermission("seller.refundManagement")
            rlSetting.isVisible = hasPermission("seller.setting")
            rlDeal.isVisible = hasPermission("seller.deal_management")
            rlBanner.isVisible = hasPermission("seller.show_banner")
            rlLiveCoverage.isVisible = hasPermission("seller.liveRequests")
            rlSubscription.isVisible = hasPermission("seller.subscriptionPlan")
            rlOrder.isVisible = hasPermission("seller.order_management")
            rlLiveEvent.isVisible = hasPermission("sellerLiveRequest")
            rlProduct.isVisible = hasPermission("seller.products")
            rlPayment.isVisible = hasPermission("seller.payment")
            rlLanguage.isVisible = hasPermission("seller.language")
            rlSupport.isVisible = hasPermission("seller.support")
        }
    }

    private fun hasPermission(route: String): Boolean {
        val permission = staffData?.roleAction?.find {
            it.sellermenu.any { menu -> menu.route == route }
        }

        return permission?.let { it.canView == 1 || it.canAction == 1 } == true
    }

    private fun getStaffDetail(){
        val map = HashMap<String, Any>()
        map["id"] = userInfo?.id.toString()
        viewModel.getStaffDetail(map)
    }

}