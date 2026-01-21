package com.desynatdealer.ui.home.dealManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.DealProductAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentDealDetailBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.ProgressDialogUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DealDetailFragment : BaseFragment() {
    private var binding : FragmentDealDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var dealType = "1"
    private var dealData :GetAllBannerResponse.Data?=null
    private var dealProductAdapter: DealProductAdapter?=null
    private var productList = ArrayList<GetAllProductResponse.Data.AllProductData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDealDetailBinding.inflate(layoutInflater, container,false)
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
            val itemJson = arguments?.getString("dealData")
            dealType = arguments?.getString("dealType").toString()
            if (!itemJson.isNullOrEmpty()) {
                dealData = Gson().fromJson(itemJson, GetAllBannerResponse.Data::class.java)
                setData()
            }

            when(dealType){
                "1"->{
                    toolbar.tvToolbar.text = "Listed Deals"
                    btnAddMore.isVisible = false
                    btnAdd.isVisible = true
                    clRevenue.isVisible = false
                }
                "2"->{
                    toolbar.tvToolbar.text = "Enrolled Deals"
                    btnAddMore.isVisible = true
                    btnAdd.isVisible = false
                    clRevenue.isVisible = false
                }
                "3"->{
                    toolbar.tvToolbar.text = "Expired Deals"
                    btnAddMore.isVisible = false
                    btnAdd.isVisible = false
                    clRevenue.isVisible = true
                }
            }

            dealProductAdapter = DealProductAdapter(requireActivity(),productList,dealType,object :DealProductAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {

                }

            })

            rvProduct.adapter = dealProductAdapter
        }

    }

    private fun setData() {
        binding?.apply {

        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAdd.setOnClickListener {
                CommonMethod.showSuccessDialog(requireActivity(),"Items added to deals successfully !")
            }

            btnAddMore.setOnClickListener {
                findNavController().navigate(R.id.action_dealDetailFragment_to_selectDealProductFragment)
            }
        }
    }

    override fun observe() {
    }

}