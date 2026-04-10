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
import com.desynatdealer.ui.home.liveStreaming.response.GetOtherLiveStreamingRoomResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class EventFragment : BaseFragment() {
    private var binding: FragmentEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var eventAdapter : EventAdapter?=null
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
                findNavController().navigate(R.id.action_eventFragment_to_liveEventFragment)
            }
        }
    }

    override fun observe() {
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
}