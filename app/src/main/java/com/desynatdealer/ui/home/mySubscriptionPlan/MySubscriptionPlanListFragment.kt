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
import com.desynatdealer.ui.home.mySubscriptionPlan.response.GetMyPlanResponse
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.utils.CommonMethod
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
    private var mySubscriptionList = ArrayList<GetMyPlanResponse.Data.Data>()
    private var subscriptionAdapter: SubscriptionAdapter? = null
    private var mySubscriptionAdapter: MySubscriptionAdapter? = null
    private var selectedType = 1
    private var selectedPos = -1
    private var mySelectedPos = -1
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
                        selectedPos = position
                        createSubscriptionPlan(subscriptionList[position].id.toString())
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
                    override fun onSubscriptionClick(position: Int,type: String) {
                        when(type){
                            "1"->{
                                val bundle = Bundle().apply {
                                    putString("mySubscriptionData", Gson().toJson(mySubscriptionList[position]))
                                }
                                findNavController().navigate(R.id.action_mySubscriptionFragment_to_subscriptionDetailFragment,bundle)
                            }
                            "2"->{
                                mySelectedPos = position
                                renewSubscriptionPlan()
                            }
                        }
                    }

                })
            rvMyPlan.adapter = mySubscriptionAdapter

            when(selectedType){
                1->{
                    allPlan()
                }
                2->{
                    myPlan()
                }
            }
        }

    }

    override fun initControl() {
        binding?.apply {

            tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvSubscriptionPlan.setOnClickListener {
               allPlan()
            }

            tvMyPlan.setOnClickListener {
                myPlan()
            }
        }
    }

    private fun myPlan() {
        binding?.apply {
            llSubscriptionPlan.isVisible = false
            tvNoDataFound.isVisible = false
            llMyPlan.isVisible = true
            selectedType = 2
            getMyPlan()
            tvSubscriptionPlan.setTextColor(requireActivity().getColor(R.color.black))
            tvMyPlan.setTextColor(requireActivity().getColor(R.color.white))
            tvMyPlan.setBackgroundResource(R.drawable.selected_right_tab)
            tvSubscriptionPlan.background = null
        }
    }

    private fun allPlan(){
        binding?.apply {
            llSubscriptionPlan.isVisible = true
            tvNoDataFound.isVisible = false
            llMyPlan.isVisible = false
            selectedType = 1
            getSubscriptionPlan()
            tvSubscriptionPlan.setTextColor(requireActivity().getColor(R.color.white))
            tvMyPlan.setTextColor(requireActivity().getColor(R.color.black))
            tvSubscriptionPlan.setBackgroundResource(R.drawable.selected_left_tab)
            tvMyPlan.background = null
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
                //    getMyPlan()
                    checkData()
                    viewModel.subscriptionPlanResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
               //     showToast(result.message ?: getString(R.string.generic_error))
                    checkData()
                    viewModel.subscriptionPlanResponse.value = null
                }

                else -> {

                }
            }
        }
        viewModel.getMyPlanResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    mySubscriptionList.clear()
                    result.data?.data?.data?.let { mySubscriptionList.addAll(it) }
                    mySubscriptionAdapter?.notifyDataSetChanged()

//                    val purchasedPlanIds = mySubscriptionList.map { it.planId }
//
//                    // Update subscription list purchase status
//                    subscriptionList.forEach { plan ->
//                        plan.isPurchased = purchasedPlanIds.contains(plan.id)
//                    }
//                    subscriptionAdapter?.notifyDataSetChanged()

                    checkData()

                    viewModel.getMyPlanResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                  //  showToast(result.message ?: getString(R.string.generic_error))
                    checkData()
                    viewModel.getMyPlanResponse.value = null
                }

                else -> {

                }
            }}

        viewModel.createSubscriptionPlanResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    val bundle = Bundle().apply {
                        putString("subscriptionData", Gson().toJson(subscriptionList[selectedPos]))
                        putString("transactionId", result.data?.data?.transactionId)
                        putString("from", "mySubscription")
                    }
                    findNavController().navigate(R.id.action_mySubscription_to_paymentMethodFragment,bundle)
                    selectedPos = -1
                    viewModel.createSubscriptionPlanResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.createSubscriptionPlanResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.renewSubscriptionPlanResponse.observe(viewLifecycleOwner){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }
                is ApiResult.Success -> {
                    val bundle = Bundle().apply {
                        putString("mySubscriptionData", Gson().toJson(mySubscriptionList[mySelectedPos]))
                        putString("transactionId", result.data?.data?.transactionId)
                        putString("from", "renew")
                    }
                    findNavController().navigate(R.id.action_mySubscription_to_paymentMethodFragment,bundle)
                    viewModel.renewSubscriptionPlanResponse.value = null
                }
                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.renewSubscriptionPlanResponse.value = null
                }
                else ->{

                }
            }
        }

    }

    private fun getSubscriptionPlan() {
        viewModel.getSubscriptionPlan()
    }

    private fun getMyPlan() {
        val map = HashMap<String,Any>()
        viewModel.getMyPlan(map)
    }

    fun createSubscriptionPlan(id: String){
        val map = HashMap<String,Any>()
        map["plan_id"]=id
        viewModel.createSubscriptionPlan(map)
    }

    private fun renewSubscriptionPlan() {
        val map = HashMap<String,Any>()
        map["plan_id"] = mySubscriptionList[mySelectedPos]?.planId.toString()
        viewModel.renewSubscriptionPlan(map)
    }

    private fun checkData(){
        when(selectedType){
            1->{
                binding?.tvNoDataFound?.isVisible = subscriptionList.isEmpty()
            }
            2->{
                binding?.tvNoDataFound?.isVisible = mySubscriptionList.isEmpty()
            }
        }


    }
}