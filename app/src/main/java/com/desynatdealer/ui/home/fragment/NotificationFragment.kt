package com.desynatdealer.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
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
import kotlin.text.compareTo

@AndroidEntryPoint
class NotificationFragment : BaseFragment() {
    private var binding: FragmentNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isFromSwipe=false
    private var notificationList = ArrayList<GetUserNotificationResponse.Data.Data>()
    private var notificationAdapter: NotificationAdapter?=null
    private var selectedPos = -1

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false

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
                pagination = false
                currentPage = 1
                lastPage = 1

                getNotification()
            }
            nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if ( !pagination && currentPage < lastPage) {
                            pagination = true
                            isFromSwipe = false
                            currentPage += 1
                            getNotification()
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
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
        map["page"] = currentPage
        map["limit"] = pageLimit
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
                    if (!isFromSwipe&&!pagination){
                        ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    }
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    binding?.progressBar?.isVisible = false
                    if (isFromSwipe) {
                        binding?.swipeRefreshLayout?.isRefreshing = false
                        isFromSwipe = false
                    }

                    currentPage = result.data?.data?.currentPage?:0
                    totalItem = result.data?.data?.total?:0

                    lastPage =  result.data?.data?.lastPage?:0

                    val oldSize = notificationList.size
                    if (!pagination){
                        notificationList.clear()
                    }
                    val newItems =  result.data?.data?.data ?: emptyList()

                    notificationList.addAll(newItems)
                    checkData()

                    if (pagination){

                        result.data?.data?.data?.size?.let {
                            notificationAdapter?.notifyItemRangeInserted(oldSize,
                                it
                            )
                        }
                    }
                    else{
                        notificationAdapter?.notifyDataSetChanged()
                    }

                    pagination = false
                    isFromSwipe = false

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