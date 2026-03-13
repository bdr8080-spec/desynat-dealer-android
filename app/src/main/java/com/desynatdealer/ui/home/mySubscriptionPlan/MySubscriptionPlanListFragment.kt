package com.desynatdealer.ui.home.mySubscriptionPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.desynatdealer.R
import com.desynatdealer.adapter.MySubscriptionAdapter
import com.desynatdealer.adapter.SubscriptionAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentMySubscriptionPlanListBinding
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MySubscriptionPlanListFragment : BaseFragment() {

    private var binding: FragmentMySubscriptionPlanListBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var subscriptionList = ArrayList<GetAllPlanResponse.Data>()
    private var mySubscriptionList = ArrayList<GetAllPlanResponse.Data>()
    private var subscriptionAdapter: SubscriptionAdapter?= null
    private var mySubscriptionAdapter: MySubscriptionAdapter?= null
    private var selectedType = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySubscriptionPlanListBinding.inflate(layoutInflater, container, false)
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
            subscriptionAdapter = SubscriptionAdapter(requireActivity(),subscriptionList,object :SubscriptionAdapter.SubscriptionClick{
                override fun onSubscriptionClick(position: Int) {

                }

            })
            rvPlan.adapter = subscriptionAdapter

            mySubscriptionAdapter = MySubscriptionAdapter(requireActivity(),mySubscriptionList,object :MySubscriptionAdapter.SubscriptionClick{
                override fun onSubscriptionClick(position: Int) {

                }

            })
            rvMyPlan.adapter = mySubscriptionAdapter
        }

    }

    override fun initControl() {
        binding?.apply {
            tvSubscriptionPlan.setOnClickListener {
                llSubscriptionPlan.isVisible = true
                llMyPlan.isVisible = false
                selectedType = 1
                tvSubscriptionPlan.setTextColor(requireActivity().getColor(R.color.white))
                tvMyPlan.setTextColor(requireActivity().getColor(R.color.black))
                tvSubscriptionPlan.setBackgroundResource(R.drawable.selected_left_tab)
                tvMyPlan.background = null
            }

            tvMyPlan.setOnClickListener {
                llSubscriptionPlan.isVisible = false
                llMyPlan.isVisible = true
                selectedType = 2
                tvSubscriptionPlan.setTextColor(requireActivity().getColor(R.color.black))
                tvMyPlan.setTextColor(requireActivity().getColor(R.color.white))
                tvMyPlan.setBackgroundResource(R.drawable.selected_right_tab)
                tvSubscriptionPlan.background = null
            }
        }
    }

    override fun observe() {
    }

}