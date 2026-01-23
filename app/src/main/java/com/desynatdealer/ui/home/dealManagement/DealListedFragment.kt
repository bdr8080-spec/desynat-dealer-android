package com.desynatdealer.ui.home.dealManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.DealAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentDealListedBinding
import com.desynatdealer.ui.home.dealManagement.response.GetAllDealResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DealListedFragment : BaseFragment() {
    private var binding : FragmentDealListedBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var dealAdapter: DealAdapter?=null
    private var dealList = ArrayList<GetAllDealResponse.Data.ListedDeal>()
    private var dealType = "1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDealListedBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "Listed Deals"



            dealList.clear()
            dealAdapter = DealAdapter(requireActivity(),dealList,object : DealAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    val bundle = Bundle().apply {
                        putString("dealData", Gson().toJson(dealList[position]))
                        putString("dealType", dealType)
                    }
                    findNavController().navigate(R.id.action_dealListedFragment_to_DealDetailFragment,bundle)
                }

            })
            rvDeal.adapter = dealAdapter

            when(dealType){
                "1"->{
                    listedDeal()
                }
                "2"->{
                   enrolled()
                }
                "3"->{
                    expired()
                }
            }

            getDeal()
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvListed.setOnClickListener {
               listedDeal()
            }

            tvEnrolled.setOnClickListener {
               enrolled()
            }

            tvExpired.setOnClickListener {
                expired()
            }

        }
    }

    private fun listedDeal() {
        binding?.apply {
            dealType = "1"
            clSearch.isVisible = false
            getDeal()
            tvListed.setTextColor(requireActivity().getColor(R.color.white))
            tvEnrolled.setTextColor(requireActivity().getColor(R.color.black))
            tvExpired.setTextColor(requireActivity().getColor(R.color.black))

            tvListed.setBackgroundResource(R.drawable.selected_left_tab)
            tvEnrolled.background = null
            tvExpired.background = null
        }
    }

    private fun expired() {
        binding?.apply {
            clSearch.isVisible = true
            dealType = "3"
            getDeal()
            tvListed.setTextColor(requireActivity().getColor(R.color.black))
            tvEnrolled.setTextColor(requireActivity().getColor(R.color.black))
            tvExpired.setTextColor(requireActivity().getColor(R.color.white))

            tvExpired.setBackgroundResource(R.drawable.selected_right_tab)
            tvEnrolled.background = null
            tvListed.background = null
        }
    }

    private fun enrolled() {
        binding?.apply {  clSearch.isVisible = true
            dealType = "2"
           getDeal()
            tvListed.setTextColor(requireActivity().getColor(R.color.black))
            tvEnrolled.setTextColor(requireActivity().getColor(R.color.white))
            tvExpired.setTextColor(requireActivity().getColor(R.color.black))

            tvEnrolled.setBackgroundResource(R.drawable.selected_mid_tab)
            tvExpired.background = null
            tvListed.background = null }
    }

    override fun observe() {
        viewModel.getDealResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    dealList.clear()
                    when(dealType){
                        "1"->{
                            result.data?.data?.listedDeal?.let {
                                dealList.addAll(it)
                            }
                        }
                        "2"->{
                            result.data?.data?.enrolled?.let {
                                dealList.addAll(it)
                            }
                        }
                        "3"->{
                            result.data?.data?.expiredDeal?.let {
                                dealList.addAll(it)
                            }
                        }
                    }

                    dealAdapter?.notifyDataSetChanged()
                    dealAdapter?.setDealType(dealType)
                    checkData()
                    viewModel.getDealResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getDealResponse.value = null
                }

                else -> {}
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = dealList.isEmpty()
    }

    private fun getDeal() {
        val map = HashMap<String,Any>()
        viewModel.getDeal(map)
    }

}