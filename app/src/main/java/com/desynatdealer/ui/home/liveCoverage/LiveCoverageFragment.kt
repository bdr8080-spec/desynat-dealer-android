package com.desynatdealer.ui.home.liveCoverage

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.desynatdealer.adapter.LiveCoverageRequestAdapter
import com.desynatdealer.adapter.ReasonAdapter
import com.desynatdealer.utils.customCalendar.CalendarAdapter
import com.desynatdealer.utils.customCalendar.CalendarDay
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.CancelRequestDialogBinding
import com.desynatdealer.databinding.FragmentLiveCoverageBinding
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.getValue
import com.desynatdealer.R
import com.desynatdealer.adapter.TimeSlotAdapter
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.databinding.SendLiveRequestDialogBinding
import com.desynatdealer.ui.home.liveCoverage.response.GetLiveRequestResponse
import com.desynatdealer.ui.home.liveCoverage.response.GetTimeSlotResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.openCustomDatePicker

@AndroidEntryPoint
class LiveCoverageFragment : BaseFragment() {
    private var binding : FragmentLiveCoverageBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var liveCoverageRequestAdapter: LiveCoverageRequestAdapter?=null
    private var liveCoverageRequestList = ArrayList<GetLiveRequestResponse.Data.Data>()
    private var cancelDialog: Dialog?=null
    private var dialogBinding: SendLiveRequestDialogBinding?=null
    private var sendRequestDialog: Dialog?=null
    private var reasonList = ArrayList<String>()
    private var reasonAdapter: ReasonAdapter? = null
    private var timeSlotList = ArrayList<GetTimeSlotResponse.Data>()
    private var timeSlotAdapter: TimeSlotAdapter? = null
    var currentTimeStamp = 0L

    private val calendar = Calendar.getInstance()
    private lateinit var calendarAdapter: CalendarAdapter
    private val daysList = mutableListOf<CalendarDay>()
    private val months = listOf(
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    )
    private var selectedCalendar: Calendar? = null
    private var timeSlotDate: String? = null
    private var type = 0
    private var selectedTimeSlot : GetTimeSlotResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveCoverageBinding.inflate(layoutInflater, container,false)
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
        binding?.apply {
            toolbar.tvToolbar.text = "Live Coverage"

            calendarAdapter = CalendarAdapter(daysList,calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)) { selectedDay ->
                val selectedCal = calendar.clone() as Calendar
                selectedCal.set(Calendar.DAY_OF_MONTH, selectedDay.day)

                val format = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

                tvCalendarDate.text = format.format(selectedCal.time)
                timeSlotDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCal.time)
            //    getTimeSlot(timeSlotDate.toString())
            }

            rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
            rvCalendar.adapter = calendarAdapter

            setupCalendarAdapter()
            loadCalendar()

            val todayFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            binding?.tvCalendarDate?.text = todayFormat.format(Calendar.getInstance().time)
            timeSlotDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
         //   getTimeSlot(timeSlotDate.toString())

            liveCoverageRequestAdapter = LiveCoverageRequestAdapter(requireActivity(),liveCoverageRequestList,object :
                LiveCoverageRequestAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    when(type){
                        1->{

                        }
                        2->{
                            cancelRequestDialog(liveCoverageRequestList[position].id)
                        }
                    }
                }

            })
            rvRequest.adapter = liveCoverageRequestAdapter
        }

        getLiveRequest()

    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivFilter.setOnClickListener {

            }

            ivAddRequest.setOnClickListener {
                sendRequestDialog()
            }
        }
    }

    private fun getLiveRequest(){
        viewModel.getLiveRequest()
    }

    private fun getTimeSlot(date: String){
        viewModel.getTimeSlot(date)
    }

    private fun sendLiveRequest(id: String){
        val map = HashMap<String,Any>()
        map["timeslot_id"] = id
        viewModel.sendLiveRequest(map)
    }

    private fun cancelLiveRequest(reason: String,id:String){
        val map = HashMap<String,Any>()
        map["id"] = id
        map["cancel_reason"] = reason
        viewModel.cancelLiveRequest(map)
    }

    override fun observe() {

        viewModel.getLiveRequestResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {

                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    when(type){
                        1->{
                            CommonMethod.showSuccessDialog(requireActivity(),"Request Sent Successfully !")
                        }
                        2->{
                            CommonMethod.showSuccessDialog(requireActivity(),"Request Cancelled Successfully !")
                        }
                    }
                    type = 0
                    liveCoverageRequestList.clear()
                    result.data?.data?.data?.let { liveCoverageRequestList.addAll(it) }
                    liveCoverageRequestAdapter?.notifyDataSetChanged()
                    viewModel.getLiveRequestResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getLiveRequestResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.getTimeSlotResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    timeSlotList.clear()
                    result.data?.data?.let { timeSlotList.addAll(it) }
                    timeSlotAdapter?.notifyDataSetChanged()
                    dialogBinding?.tvNoDataFound?.isVisible = timeSlotList.isEmpty()
                    viewModel.getTimeSlotResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getTimeSlotResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.cancelLiveRequestResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    type = 2
                    getLiveRequest()
                    viewModel.cancelLiveRequestResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.cancelLiveRequestResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.sendLiveRequestResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    sendRequestDialog?.dismiss()
                    type = 1
                    getLiveRequest()
                    viewModel.sendLiveRequestResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.sendLiveRequestResponse.value = null
                }
                else ->{

                }
            }
        }
    }

    private fun loadCalendar() {
        daysList.clear()

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val today = Calendar.getInstance()
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val maxDays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        currentTimeStamp = today.timeInMillis
        repeat(firstDayOfWeek) {
            daysList.add(CalendarDay(0, false))
        }

        for (day in 1..maxDays) {

            val dateCal = calendar.clone() as Calendar
            dateCal.set(Calendar.DAY_OF_MONTH, day)
            dateCal.set(Calendar.HOUR_OF_DAY, 0)
            dateCal.set(Calendar.MINUTE, 0)
            dateCal.set(Calendar.SECOND, 0)
            dateCal.set(Calendar.MILLISECOND, 0)

            val todayCal = today.clone() as Calendar
            todayCal.set(Calendar.HOUR_OF_DAY, 0)
            todayCal.set(Calendar.MINUTE, 0)
            todayCal.set(Calendar.SECOND, 0)
            todayCal.set(Calendar.MILLISECOND, 0)

            val isPastDate = dateCal.before(todayCal)

            val isSelected =
                selectedCalendar?.let {
                    it.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                            it.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                            it.get(Calendar.DAY_OF_MONTH) == day
                } ?: false

            val isToday =
                selectedCalendar == null &&
                        day == today.get(Calendar.DAY_OF_MONTH) &&
                        calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)

            daysList.add(
                CalendarDay(
                    day = day,
                    isCurrentMonth = true,
                    isSelected = isSelected || isToday,
                    isDisabled = isPastDate
                )
            )
        }

        calendarAdapter.notifyDataSetChanged()
        updateMonthTitle()
    }

    private fun updateMonthTitle() {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding?.tvMonthYear?.text = monthYearFormat.format(calendar.time)
    }

    private fun setupCalendarAdapter() {
        calendarAdapter = CalendarAdapter(
            daysList,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        ) { selectedDay ->

            selectedCalendar = calendar.clone() as Calendar
            selectedCalendar?.set(Calendar.DAY_OF_MONTH, selectedDay.day)

            val format = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            binding?.tvCalendarDate?.text = format.format(selectedCalendar?.time)
            timeSlotDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar?.time)
           // getTimeSlot(timeSlotDate.toString())
        }

        binding?.rvCalendar?.adapter = calendarAdapter
    }

    private fun openMonthPopup(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)

        months.forEachIndexed { index, month ->
            popup.menu.add(0, index, index, month)
        }

        popup.setOnMenuItemClickListener { item ->
            calendar.set(Calendar.MONTH, item.itemId)

            loadCalendar()
            true
        }

        popup.show()
    }

    private fun cancelRequestDialog(id:String) {
        var reason = ""
        cancelDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = CancelRequestDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { cancelDialog?.setContentView(it) }

        val window: Window = cancelDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        cancelDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            reasonList.clear()
          //  reasonList.add("Other")
            etReason.isVisible = true
            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    if (position==reasonList.size-1){
                        etReason.isVisible = true
                        reason = ""
                    }
                    else{
                        etReason.isVisible = false
                        reason = reasonList[position]
                    }
                }

            })
            rvReason.adapter = reasonAdapter

            tvToolbar.setOnClickListener {
                cancelDialog?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (etReason.isVisible){
                    if (etReason.text?.trim().toString().isEmpty()){
                        showToast("Please enter reason")
                    }
                    else{
                        reason = etReason.text?.trim().toString()
                        cancelLiveRequest(reason,id)
                    }
                }
                else if(reason==""){
                    showToast("Please select reason")
                }
                else {
                    cancelLiveRequest(reason,id)
                }
            }
        }

        cancelDialog?.show()
    }

    private fun sendRequestDialog() {
        var newDate = ""
        sendRequestDialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialogBinding = SendLiveRequestDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding?.root.let { it?.let { it1 -> sendRequestDialog?.setContentView(it1) } }

        val window: Window = sendRequestDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        cancelDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params


        dialogBinding?.apply {
           etFrom.setText(CommonMethod.formatDate(timeSlotDate.toString(),"yyyy-MM-dd","dd/MM/yyy"))
            timeSlotAdapter = TimeSlotAdapter(requireActivity(), timeSlotList, object :
                TimeSlotAdapter.OnClick {
                override fun onClickItem(position: Int) {
                    selectedTimeSlot = timeSlotList[position]
                }

            })
            rvTimeSlot.adapter = timeSlotAdapter

            getTimeSlot(timeSlotDate.toString())

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = currentTimeStamp) { date, timestamp ->
                    etFrom.setText(date)
                    newDate = CommonMethod.formatDate(date, "dd/MM/yyyy", "yyyy-MM-dd")
                    getTimeSlot(newDate.toString())
                }
            }

            tvToolbar.setOnClickListener {
                sendRequestDialog?.dismiss()
            }

            btnSend.setOnClickListener {
                if (etFrom.text?.isEmpty() == true){
                    showToast("Please select date")
                }
                else{
                    sendLiveRequest(selectedTimeSlot?.id.toString())
                }
            }
        }

        sendRequestDialog?.show()
    }

}