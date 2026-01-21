package com.desynatdealer.ui.home.dealManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.adapter.DealProductAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentSelectDealProductBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.utils.CommonMethod
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectDealProductFragment : BaseFragment() {
    private var binding : FragmentSelectDealProductBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var dealProductAdapter: DealProductAdapter?=null
    private var productList = ArrayList<GetAllProductResponse.Data.AllProductData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectDealProductBinding.inflate(layoutInflater, container,false)
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
            toolbar.tvToolbar.text = "Select Product"

            dealProductAdapter = DealProductAdapter(requireActivity(),productList,"1",object :
                DealProductAdapter.OnClick{
                override fun onItemClick(position: Int, type: Int) {

                }

            })

            rvProduct.adapter = dealProductAdapter
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
        }
    }

    override fun observe() {
    }


}