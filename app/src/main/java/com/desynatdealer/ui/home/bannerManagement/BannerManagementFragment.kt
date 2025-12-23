package com.desynatdealer.ui.home.bannerManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.BannerAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentBannerManagementBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BannerManagementFragment : BaseFragment() {
    private var binding : FragmentBannerManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()

    private var search: Job? = null
    private var lastQuery: String? = null

    private var bannerList = ArrayList<GetAllBannerResponse.Data>()
    private var bannerAdapter : BannerAdapter?=null
    private var selectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerManagementBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "Banner Management"

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        getBanner()
                        delay(600)

                    }
                }
            })

            bannerList.clear()
            bannerAdapter = BannerAdapter(requireActivity(),bannerList,object :BannerAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("bannerData", Gson().toJson(bannerList[position]))
                            }
                            findNavController().navigate(R.id.action_bannerManagementFragment_to_bannerDetailFragment,bundle)
                        }
                        2->{
                            val bundle = Bundle().apply {
                                putString("bannerData", Gson().toJson(bannerList[position]))
                                putString("from", "banner")
                            }
                            findNavController().navigate(R.id.action_bannerManagementFragment_to_paymentMethodFragment,bundle)
                        }
                    }
                }

            })
            rvBanner.adapter = bannerAdapter

           getBanner()

        }
    }

    private fun getBanner() {
        val map = HashMap<String,Any>()
        if (lastQuery!=null) {
            map["search"] = lastQuery.toString()
        }
        viewModel.getBanner(map)
    }



    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_bannerManagementFragment_to_addBannerFragment)
            }

            ivFilter.setOnClickListener {

            }
        }
    }

    override fun observe() {
        viewModel.getBannerResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                }

                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    bannerList.clear()
                    result.data?.data?.let { bannerList.addAll(it) }
                    bannerAdapter?.notifyDataSetChanged()
                    checkData()
                    viewModel.getBannerResponse.value = null
                }

                is ApiResult.Error -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.getBannerResponse.value = null
                }

                else -> {}
            }
        }


    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = bannerList.isEmpty()

    }

}