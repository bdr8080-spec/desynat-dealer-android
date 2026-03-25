package com.desynatdealer.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.NotificationAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentNotificationBinding
import com.desynatdealer.ui.home.fragment.response.GetUserNotificationResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue
@AndroidEntryPoint
class NotificationFragment : BaseFragment() {
    private var binding: FragmentNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isFromSwipe=false
    private var notificationList = ArrayList<GetUserNotificationResponse.Data.Data>()
    private var notificationAdapter: NotificationAdapter?=null
    private var selectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {

            notificationAdapter = NotificationAdapter(requireActivity(),notificationList,object :
                NotificationAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    selectedPos = position
                    readNotification()
                }
            })
            rvNotification.adapter = notificationAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getNotification()
            }
        }

        getNotification()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvReadAll.setOnClickListener {
                readAllNotification()
            }
        }
    }

    private fun getNotification(){
        val map = HashMap<String,Any>()
        viewModel.getNotificationList(map)
    }

    private fun readNotification() {
       val id = notificationList[selectedPos].id.toString()
        val idList = arrayListOf(id)
        val map = HashMap<String,Any>()
        map["notification_id"] = idList
        viewModel.readNotification(map)
    }

    private fun readAllNotification(){
        val map = HashMap<String,Any>()
        map["receiver_type"] = 3
        viewModel.readAllNotification(map)
    }

    override fun observe() {

        viewModel.getNotificationListResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    if (!isFromSwipe){
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    if (isFromSwipe) {
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }
                    notificationList.clear()
                    result.data?.data?.data?.let { notificationList.addAll(it) }
                    notificationAdapter?.notifyDataSetChanged()
                    checkData()

                    viewModel.getNotificationListResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getNotificationListResponse.value = null
                }

                else -> {

                }
            }
        }

        viewModel.readNotificationResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    notificationList[selectedPos].isRead = 1
                    notificationAdapter?.notifyItemChanged(selectedPos)
                    selectedPos = -1
                    viewModel.readNotificationResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.readNotificationResponse.value = null
                }

                else -> {

                }
            }
        }

        viewModel.readAllNotificationResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    notificationList.forEach {
                        it.isRead = 1
                    }
                    notificationAdapter?.notifyDataSetChanged()
                    viewModel.readAllNotificationResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.readAllNotificationResponse.value = null
                }

                else -> {

                }
            }
        }

    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = notificationList.isEmpty()
        binding?.tvReadAll?.isVisible = notificationList.isNotEmpty()
    }
}