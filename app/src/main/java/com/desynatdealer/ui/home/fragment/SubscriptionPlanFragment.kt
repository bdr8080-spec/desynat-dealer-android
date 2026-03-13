package com.desynatdealer.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.SubscriptionAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentSubscriptionPlanBinding
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.google.gson.Gson

class SubscriptionPlanFragment : BaseFragment() {
    private var binding: FragmentSubscriptionPlanBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var subscriptionList = ArrayList<GetAllPlanResponse.Data>()
    private var subscriptionAdapter: SubscriptionAdapter?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubscriptionPlanBinding.inflate(layoutInflater, container, false)
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
           if (!SharedPreference.get(requireActivity()).isSubscription){
               ivBack.isVisible = false
               tvSkip.isVisible = true
           }
            else{
               ivBack.isVisible = true
               tvSkip.isVisible = false
           }

            subscriptionAdapter = SubscriptionAdapter(requireActivity(),subscriptionList,object :SubscriptionAdapter.SubscriptionClick{
                override fun onSubscriptionClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("subscriptionData", Gson().toJson(subscriptionList[position]))
                        putString("from", "subscription")
                    }
                    findNavController().navigate(R.id.action_subscriptionFragment_to_paymentMethodFragment,bundle)
                }

            })
            rvPlan.adapter = subscriptionAdapter
        }

        getSubscriptionPlan()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvSkip.setOnClickListener {
                SharedPreference.get(requireActivity()).isSubscription = true
                findNavController().navigate(R.id.action_subscriptionFragment_to_HomeFragment)
            }
        }
    }

    override fun observe() {

        viewModel.subscriptionPlanResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    subscriptionList.clear()
                    result.data?.data?.let { subscriptionList.addAll(it) }
                    subscriptionAdapter?.notifyDataSetChanged()
                    if (result.data?.data?.isEmpty() == true){
                        SharedPreference.get(requireActivity()).isSubscription = true
                        findNavController().navigate(R.id.action_subscriptionFragment_to_HomeFragment)
                    }

                    viewModel.subscriptionPlanResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.subscriptionPlanResponse.value = null
                }
                else ->{

                }
            }
        }

    }

    private fun getSubscriptionPlan() {

        viewModel.getSubscriptionPlan()

    }


}