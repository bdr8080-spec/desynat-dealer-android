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
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.openCustomDatePicker
import com.desynatdealer.utils.showToast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
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

    var analytics = "1"
    var filter = "1"

    private var fromTimestamp: Long? = null
    private var toTimestamp: Long? = null
    private var fromDateText: String? = null
    private var toDateText: String? = null

    private var fromAnalyticTimestamp: Long? = null
    private var toAnalyticTimestamp: Long? = null
    private var fromAnalyticDateText: String? = null
    private var toAnalyticDateText: String? = null

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

            radioAnalyticsGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbOrderConversion -> {
                        analytics = "1"
                        setLineChartData()
                    }
                    R.id.rbCustomerVisit ->{
                        analytics ="2"
                        setLineChartData()
                    }
                    else -> {
                        analytics = "1"
                        setLineChartData()
                    }
                }
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbToday -> {
                        filter = "1"
                        getDashboardData()
                    }
                    R.id.rbWeek ->{
                        filter = "2"
                        getDashboardData()
                    }
                    R.id.rbMonth ->{
                        filter = "3"
                        getDashboardData()
                    }
                    R.id.rbYear ->{
                        filter = "4"
                        getDashboardData()
                    }

                }
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    fromTimestamp = timestamp
                    fromDateText = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                    etFrom.setText(date)

                    toTimestamp = null
                    toDateText = null
                    etTo.setText("")
                }
            }

            etTo.setOnClickListener {
                if (fromTimestamp == null) {
                    showToast(getString(R.string.please_select_from_date_first))
                    return@setOnClickListener
                }

                openCustomDatePicker(
                    requireActivity(),
                    minDate = fromTimestamp
                ) { date, timestamp ->
                    toTimestamp = timestamp
                    toDateText = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                    etTo.setText(date)
                    getDashboardData()
                    tvReset.isVisible = true
                }
            }


            etAnalyticsFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    toAnalyticTimestamp = timestamp
                    toAnalyticDateText = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                    etAnalyticsFrom.setText(date)

                    toAnalyticTimestamp = null
                    toAnalyticDateText = null
                    etAnalyticsTo.setText("")
                }
            }

            etAnalyticsTo.setOnClickListener {
                if (fromAnalyticTimestamp == null) {
                    showToast(getString(R.string.please_select_from_date_first))
                    return@setOnClickListener
                }

                openCustomDatePicker(
                    requireActivity(),
                    minDate = fromAnalyticTimestamp
                ) { date, timestamp ->
                    toAnalyticTimestamp = timestamp
                    toAnalyticDateText = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                    etAnalyticsTo.setText(date)
                    tvAnalyticsReset.isVisible = true
                    getDashboardData()
                }
            }

            tvReset.setOnClickListener {
                fromTimestamp = null
                fromDateText = null
                toDateText=null
                toTimestamp = null
                etTo.setText("")
                etFrom.setText("")
                tvReset.isVisible = false
                getDashboardData()
            }

            tvAnalyticsReset.setOnClickListener {
                fromAnalyticTimestamp = null
                fromAnalyticDateText = null
                toAnalyticDateText=null
                toAnalyticTimestamp = null
                etAnalyticsTo.setText("")
                etAnalyticsFrom.setText("")
                getDashboardData()
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
                    setLineChartData()
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
                    binding?.progressBar?.isVisible = false
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
          /*  if(userInfo?.storeInfo?.storeAvailability==1){
                tvSwitch.text = getString(R.string.busy_on)
                tvSwitch.setTextColor(Color.parseColor("#8B5E3C"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = getString(R.string.busy_off)
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )

            }*/

            if(userInfo?.storeInfo?.storeAvailability==1){
                tvSwitch.text = getString(R.string.open)
                tvSwitch.setTextColor(Color.parseColor("#8B5E3C"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
            }
            else{
                tvSwitch.text = getString(R.string.busy)
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
        if (fromDateText!=null&&toDateText!=null){
            map["from_date"] = fromDateText.toString()
            map["to_date"] = toDateText.toString()
        }

        map["filter"] = filter
        if (fromAnalyticDateText!=null&&toAnalyticDateText!=null) {
            map["from_date_analytics"] = fromAnalyticDateText.toString()
            map["to_date_analytics"] = toAnalyticDateText.toString()
        }
        viewModel.getDashboardData(map)
    }

    private fun getAdminStory() {
        val map = HashMap<String,Any>()

        viewModel.getAdminStory(map)
    }

    private fun getOrderNotification() {

        viewModel.getOrderNotification()
    }

    fun setLineChartData() {

        val entries = ArrayList<Entry>()
        val months = ArrayList<String>()

        val isArabic = Constants.language == "ar"

        val monthMapArabic = mapOf(
            "Jan" to "يناير",
            "Feb" to "فبراير",
            "Mar" to "مارس",
            "Apr" to "أبريل",
            "May" to "مايو",
            "Jun" to "يونيو",
            "Jul" to "يوليو",
            "Aug" to "أغسطس",
            "Sep" to "سبتمبر",
            "Oct" to "أكتوبر",
            "Nov" to "نوفمبر",
            "Dec" to "ديسمبر"
        )

        when (analytics) {
            "1" -> {
                dashboardData?.orderAnalytics?.forEachIndexed { index, item ->

                    val monthName =
                        if (isArabic)
                            monthMapArabic[item.month] ?: item.month
                        else
                            item.month

                    entries.add(Entry(index.toFloat(), item.orders.toFloat()))
                    months.add(monthName)
                }
            }

            "2" -> {
                dashboardData?.visitorAnalytics?.forEachIndexed { index, item ->

                    val monthName =
                        if (isArabic)
                            monthMapArabic[item.month] ?: item.month
                        else
                            item.month

                    entries.add(Entry(index.toFloat(), item.totalVisitors.toFloat()))
                    months.add(monthName)
                }
            }
        }

        val dataSet = LineDataSet(
            entries,
            if (analytics == "1") getString(R.string.orders) else getString(R.string.visitor_analytics)
        )


        dataSet.color = resources.getColor(R.color.black)
        dataSet.setCircleColor(Color.parseColor("#F3D1A2"))
        dataSet.circleRadius = 7f
        dataSet.lineWidth = 2f
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = resources.getColor(R.color.white)

        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.cubicIntensity = 0.2f

        val lineData = LineData(dataSet)

        binding?.getTheGraph?.apply {

            data = lineData
            description.isEnabled = false
            legend.isEnabled = false

            setTouchEnabled(true)
            setPinchZoom(true)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(months)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 10f
            }

            axisRight.isEnabled = false

            var maxValue = dashboardData?.orderAnalytics?.maxOfOrNull { it.orders } ?: 0

            val stepSize = when {
                maxValue <= 10 -> 2f
                maxValue <= 40 -> 5f
                maxValue <= 50 -> 10f
                else -> 20f
            }

            val axisMax = ((maxValue / stepSize).toInt() + 2) * stepSize

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = axisMax
                spaceTop = 20f

                granularity = stepSize

                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }


            animateX(1000)
            invalidate()
        }
    }
}
