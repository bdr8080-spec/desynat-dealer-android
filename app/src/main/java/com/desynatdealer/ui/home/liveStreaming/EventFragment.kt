package com.desynatdealer.ui.home.liveStreaming

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.EventAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentEventBinding
import com.desynatdealer.ui.home.liveStreaming.response.GetLiveStreamingRoomResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetOtherLiveStreamingRoomResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.getValue

@AndroidEntryPoint
class EventFragment : BaseFragment() {
    private var binding: FragmentEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var eventAdapter : EventAdapter?=null
    var isLive = false
    var isLiveIndexData:GetLiveStreamingRoomResponse.Data.CurrentRequest?=null

    private var eventList = ArrayList<GetOtherLiveStreamingRoomResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            getLiveRoomRecords()
            eventList.clear()
            eventAdapter = EventAdapter(requireActivity(),eventList,object : EventAdapter.OnCLick{
                override fun onClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("eventData", Gson().toJson(eventList[position]))
                    }
                    findNavController().navigate(R.id.action_eventFragment_to_watchLiveFragment,bundle)
                }

            })
            rvEvents.adapter = eventAdapter

        }

        getOtherLiveStreaming()

    }

    override fun initControl() {
        binding?.apply {
            tvGoLive.setOnClickListener {
                if (isLiveIndexData!=null){
                    val bundle = Bundle().apply {
                        putString("eventData", Gson().toJson(isLiveIndexData))
                    }
                        findNavController().navigate(R.id.action_eventFragment_to_liveEventFragment,bundle)
                    }
                }
        }
    }

    override fun observe() {

        viewModel.getLiveRoomRecordsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()

                    val currentList = result.data?.data?.currentRequests

                    if (!currentList.isNullOrEmpty()) {

                        val liveIndex = currentList.indexOfFirst { item ->
                            isLiveTime(item.timeslotDetails)
                        }

                        if (liveIndex != -1) {
                            isLive = true

                            isLiveIndexData = currentList[liveIndex]

                            showGoLiveButton(isLive)

                        } else {
                            isLive = false
                            showGoLiveButton(isLive)
                        }

                    } else {
                        showGoLiveButton(false)
                    }
                    viewModel.getLiveRoomRecordsResponse.value = null

                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                 //   showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getLiveRoomRecordsResponse.value = null
                }

                else -> {}
            }
        }


        viewModel.getWatchLiveRoomRecordsResponse.observe(viewLifecycleOwner) { result ->
                when (result) {
                    ApiResult.Loading -> {
                        ProgressDialogUtils.getInstance().showProgress(requireActivity(),false)
                    }

                    is ApiResult.Success -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                           eventList.clear()
                        result?.data?.data?.let { eventList.addAll(it) }
                        checkData()
                        eventAdapter?.notifyDataSetChanged()
                        viewModel.getWatchLiveRoomRecordsResponse.value = null

                    }

                    is ApiResult.Error -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        showToast(result.message ?: getString(R.string.generic_error))
                        viewModel.getWatchLiveRoomRecordsResponse.value = null
                    }

                    else -> {}
                }
            }

        }


    private fun getOtherLiveStreaming(){
        viewModel.getOtherLiveStreaming()
    }

    private fun  checkData(){
        binding?.tvNoDataFound?.isVisible = eventList.isEmpty()
    }

    private fun showGoLiveButton(isShow: Boolean) {
        binding?.tvGoLive?.apply {
            alpha = if (isShow) 1f else 0.4f
            isClickable = isShow
            isEnabled = isShow
        }

        isLive = false
    }

    private fun getLiveRoomRecords() {

        viewModel.getLiveRoomRecords()
    }
    private fun isLiveTime(timeslot: GetLiveStreamingRoomResponse.Data.CurrentRequest.TimeslotDetails): Boolean {
        return try {
            val date = timeslot.date
            val fromTime = timeslot.from
            val toTime = timeslot.to

            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.ENGLISH)

            val fromTimeDefault = CommonMethod.customizeChangeDateFormatZone(fromTime,"hh:mm a","hh:mm a")
            val toTimeDefault = CommonMethod.customizeChangeDateFormatZone(toTime,"hh:mm a","hh:mm a")

            val startDateTime = formatter.parse("$date $fromTimeDefault")
            val endDateTime = formatter.parse("$date $toTimeDefault")

            val currentTime = Date()

            currentTime.after(startDateTime) && currentTime.before(endDateTime)

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}