package com.desynatdealer.ui.home.productManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.desynatdealer.adapter.ProductAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentProductManagementBinding
import com.desynatdealer.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductManagementFragment : BaseFragment() {
    private var binding : FragmentProductManagementBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var productAdapter: ProductAdapter?=null
    private var productList = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentProductManagementBinding.inflate(layoutInflater, container,false)
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

            productAdapter = ProductAdapter(requireActivity(),productList,object:ProductAdapter.ProductClick{
                override fun onProductClick(position: Int, type: Int) {
                    when(type){
                        1->{

                        }
                        2->{

                        }
                    }
                }

            })
            rvProduct.adapter = productAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            ivAdd.setOnClickListener {

            }
        }
    }


    override fun observe() {
    }


}