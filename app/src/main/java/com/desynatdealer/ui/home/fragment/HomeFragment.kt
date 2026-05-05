package com.desynatdealer.ui.home.fragment

import android.graphics.Color
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
import com.desynatdealer.databinding.FragmentHomeBinding
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.ui.home.response.GetDashboardDataResponse
import com.desynatdealer.ui.home.response.GetSellerInfo
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import me.leolin.shortcutbadger.ShortcutBadger
import kotlin.math.log
import kotlin.text.get

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding : FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var status = 1
    var adminStoryList = ArrayList<GetStoryResponse.Data>()
    var dashboardData: GetDashboardDataResponse.Data?=null
    private var userInfo : GetSellerInfo.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentHomeBinding.inflate(layoutInflater, container,false)
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
        getOrderNotification()
        getNotificationCount()
        getAdminStory()
        getDashboardData()
        userInfo = SharedPreference.get(requireActivity()).getUser()
        updateStatus()
        binding?.apply {
            profilePic.load(userInfo?.profilePic){
                crossfade(false)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            logoImage.setOnClickListener {
                if (adminStoryList.size!=0) {
                    val bundle = Bundle().apply {
                        putString("from", "admin")
                        putString("storyData", Gson().toJson(adminStoryList))
                    }
                    findNavController().navigate(
                        R.id.action_homeFragment_to_viewStoryFragment,
                        bundle
                    )
                }
            }

            tvSwitch.setOnClickListener {
                storeAvailability()
            }

            profilePic.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }

            chatIcon.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_chatListFragment)
            }

            notificationIcon.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }

            ivSupportBot.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_TidioChatFragment)
            }

        }
    }

    override fun observe() {

        viewModel.getAdminStoryResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {

                }

                is ApiResult.Success -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    adminStoryList.clear()
                    result?.data?.data?.let { adminStoryList.addAll(it) }
                    setStoryData()
                    viewModel.getAdminStoryResponse.value = null
                }

                is ApiResult.Error -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    // showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getAdminStoryResponse.value = null
                }

                else -> {}
            }
        }

        viewModel.getOrderNotificationResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {

                }

                is ApiResult.Success -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                        (requireActivity() as HomeActivity).updateAdapterCount(
                            result.data?.data ?: 0
                        )
                    viewModel.getOrderNotificationResponse.value = null
                }

                is ApiResult.Error -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()

                    viewModel.getOrderNotificationResponse.value = null
                }

                else -> {}
            }
        }


        viewModel.getDashboardDataResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {

                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    binding?.progressBar?.isVisible = false
                    dashboardData = result?.data?.data
                    setDashboardData()
                    viewModel.getDashboardDataResponse.value = null
                }

                is ApiResult.Error -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    viewModel.getDashboardDataResponse.value = null
                }

                else -> {}
            }
        }


        viewModel.storeOnOffResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    binding?.progressBar?.isVisible = true
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.data?.message.toString())
                    if (userInfo?.storeInfo?.storeAvailability==1){
                        userInfo?.storeInfo?.storeAvailability = 0
                    }
                    else{
                        userInfo?.storeInfo?.storeAvailability = 1
                    }
                    userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    updateStatus()
                    viewModel.storeOnOffResponse.value = null
                }
                is ApiResult.Error -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.storeOnOffResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.getNotificationCountResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    binding?.progressBar?.isVisible = true
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (result.data?.data?.notificationCount!=0){
                        setNotificationCount(result.data?.data?.notificationCount?:0)
                    }
                    else{
                        SharedPreference.get(requireActivity()).badgeCount = 0
                        ShortcutBadger.removeCount(requireActivity())

                    }

                    if (result.data?.data?.chatUnreadCount!=0){
                        setChatCount(result.data?.data?.chatUnreadCount?:0)
                    }

                    viewModel.getNotificationCountResponse.value = null
                }
                is ApiResult.Error -> {
                    binding?.progressBar?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getNotificationCountResponse.value = null
                }
                else ->{

                }
            }
        }
    }

    private fun setDashboardData() {
        binding?.apply {
            tvAmount.text = "${CommonMethod.formatPrice(dashboardData?.totalRevenue?.toDoubleOrNull()?:0.0)} KD"
            tvTotalProductCount.text = "(${dashboardData?.totalProducts})"
            tvTotalOrdersCount.text = "(${dashboardData?.totalOrders})"
            tvTotalStaffCount.text = "(${dashboardData?.totalStaff})"

            tvActiveProduct.text = dashboardData?.totalActiveProducts.toString()
            tvInActiveProduct.text = dashboardData?.totalInactiveProducts.toString()
            tvNewOrder.text = dashboardData?.totalNewOrders.toString()
            tvCompleteOrder.text = dashboardData?.totalCompletedOrders.toString()
            tvOnGoingOrder.text = dashboardData?.totalOngoingOrders.toString()
            tvCancelledOrder.text = dashboardData?.totalCancelledOrders.toString()
            tvActiveStaffCount.text = dashboardData?.totalActiveStaff.toString()
            tvInActiveStaffCount.text = dashboardData?.totalInactiveStaff.toString()
        }
    }

    private fun setStoryData() {
//        binding?.apply {
//            if (adminStoryList.size!=0){
//                logoImage.setImageResource(R.drawable.ic_admin_highlight_story)
//            }
//            else{
//                logoImage.setImageResource(R.drawable.logo)
//            }
//        }

        binding?.ivHighlightStory?.isVisible = adminStoryList.isNotEmpty()

    }

    private fun updateStatus(){
        binding?.apply {
            if(userInfo?.storeInfo?.storeAvailability==1){
                tvSwitch.text = getString(R.string.busy_on)
                tvSwitch.setTextColor(Color.parseColor("#8B5E3C"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = getString(R.string.busy_off)
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )

            }

        }
    }

    private fun storeAvailability() {
        viewModel.storeOnOff()
    }

    private fun getNotificationCount(){
        viewModel.getNotificationCount()
    }

    private fun setNotificationCount(data: Int) {
        binding?.relCount?.isVisible = true
        binding?.tvCount?.text = data.toString()
        ShortcutBadger.applyCount(requireActivity(), data)
    }

    private fun setChatCount(data: Int) {
        binding?.relChatCount?.isVisible = true
        binding?.tvChatCount?.text = data.toString()
    }

    private fun getDashboardData() {
        val map = HashMap<String, String>()

        viewModel.getDashboardData(map)
    }

    private fun getAdminStory() {
        val map = HashMap<String,Any>()

        viewModel.getAdminStory(map)
    }

    private fun getOrderNotification() {

        viewModel.getOrderNotification()
    }

}
