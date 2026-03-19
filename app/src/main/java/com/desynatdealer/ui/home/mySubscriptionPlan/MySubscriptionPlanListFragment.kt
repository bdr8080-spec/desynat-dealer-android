package com.desynatdealer.ui.home.mySubscriptionPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.MySubscriptionAdapter
import com.desynatdealer.adapter.SubscriptionAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentMySubscriptionPlanListBinding
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MySubscriptionPlanListFragment : BaseFragment() {

    private var binding: FragmentMySubscriptionPlanListBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()
    private var subscriptionList = ArrayList<GetAllPlanResponse.Data>()
    private var mySubscriptionList = ArrayList<GetAllPlanResponse.Data>()
    private var subscriptionAdapter: SubscriptionAdapter? = null
    private var mySubscriptionAdapter: MySubscriptionAdapter? = null
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

            subscriptionAdapter = SubscriptionAdapter(
                requireActivity(),
                subscriptionList,
                object : SubscriptionAdapter.SubscriptionClick {
                    override fun onSubscriptionClick(position: Int) {
                        val bundle = Bundle().apply {
                            putString("subscriptionData", Gson().toJson(subscriptionList[position]))
                            putString("from", "mySubscription")
                        }
                        findNavController().navigate(R.id.action_mySubscription_to_paymentMethodFragment,bundle)
                    }

                })
            //  rvPlan.adapter = subscriptionAdapter

            rvPlan.adapter = subscriptionAdapter
            rvPlan.set3DItem(false)
            rvPlan.setIntervalRatio(0.4f)
            rvPlan.setInfinite(false)
            rvPlan.setAlpha(false)
            rvPlan.setFlat(true)
            rvPlan.setIsScrollingEnabled(true)

            val carouselLayoutManager = rvPlan.getCarouselLayoutManager()
            val currentlyCenterPosition = rvPlan.getSelectedPosition()

            rvPlan.setItemSelectListener(object : CarouselLayoutManager.OnSelected {
                override fun onItemSelected(position: Int) {

                }
            })


            mySubscriptionAdapter = MySubscriptionAdapter(
                requireActivity(),
                mySubscriptionList,
                object : MySubscriptionAdapter.SubscriptionClick {
                    override fun onSubscriptionClick(position: Int) {

                    }

                })
            rvMyPlan.adapter = mySubscriptionAdapter
        }

        getSubscriptionPlan()
    }

    override fun initControl() {
        binding?.apply {

            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

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
        viewModel.subscriptionPlanResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    subscriptionList.clear()
                    result.data?.data?.let { subscriptionList.addAll(it) }
                    subscriptionAdapter?.notifyDataSetChanged()

                    viewModel.subscriptionPlanResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.subscriptionPlanResponse.value = null
                }

                else -> {

                }
            }
        }
    }

    private fun getSubscriptionPlan() {

        viewModel.getSubscriptionPlan()

    }

}