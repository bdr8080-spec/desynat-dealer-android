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
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentNotificationBinding
import com.desynatdealer.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue
@AndroidEntryPoint
class NotificationFragment : BaseFragment() {
    private var binding: FragmentNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isFromSwipe=false
    private var notificationList = ArrayList<String>()
    private var notificationAdapter: NotificationAdapter?=null

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
            toolbar.tvToolbar.text = "My Notifications"

            notificationAdapter = NotificationAdapter(requireActivity(),notificationList,object :
                NotificationAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }
            })
            rvNotification.adapter = notificationAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )
            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getNotification(false)
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun getNotification(isLoading: Boolean=true){
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
    }
    override fun observe() {
    }
}