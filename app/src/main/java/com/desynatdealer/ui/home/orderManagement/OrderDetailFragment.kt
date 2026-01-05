package com.desynatdealer.ui.home.orderManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.adapter.OrderAdapter
import com.desynatdealer.adapter.ProductOrderAdapter
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentOrderDetailBinding
import com.desynatdealer.ui.home.productManagement.response.GetAllProductResponse
import com.desynatdealer.utils.ProgressDialogUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {
    private var binding : FragmentOrderDetailBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var productOrderAdapter: ProductOrderAdapter?=null
    private var productOrderList = ArrayList<GetAllProductResponse.Data.AllProductData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater, container,false)
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
            productOrderAdapter = ProductOrderAdapter(requireActivity(), productOrderList)
            rvProduct.adapter = productOrderAdapter
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observe() {

    }

    private fun setData(){
        binding?.apply {
        /*    when(status){
                "1"->{
                    tvStatus.text = "Confirmed"
                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                    etStatus.isVisible = true
                    btnUpdateStatus.isVisible = true
                    llComplete.isVisible = false
                }
                "2"->{
                    tvStatus.text = "Ongoing"
                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                    etStatus.isVisible = false
                    btnUpdateStatus.isVisible = false
                    llComplete.isVisible = false
                }
                "3"->{
                    tvStatus.text = "Delivered"
                    tvStatus.setTextColor(Color.parseColor("#047BD5"))
                    etStatus.isVisible = false
                    btnUpdateStatus.isVisible = false
                    llComplete.isVisible = true
                }
                "4"->{
                    tvStatus.text = "Cancelled"
                    tvStatus.setTextColor(Color.parseColor("#E70000"))
                    etStatus.isVisible = false
                    btnUpdateStatus.isVisible = false
                    llComplete.isVisible = false
                    tvCancelReason.isVisible = true
                    tvCancellationReason.isVisible = true
                }
                "5"->{
                    tvStatus.text = "Returned"
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    etStatus.isVisible = false
                    btnUpdateStatus.isVisible = false
                    llComplete.isVisible = false
                    clReturn.isVisible = true
                }
                "7"->{
                    tvStatus.text = "Ongoing"
                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                    etStatus.isVisible = false
                    btnUpdateStatus.isVisible = false
                    llComplete.isVisible = false
                }
                "8"->{
                    tvStatus.text = "Not Delivered"
                    tvStatus.setTextColor(Color.parseColor("#E70000"))
                    etStatus.isVisible = false
                    btnUpdateStatus.isVisible = false
                    llComplete.isVisible = false
                }
                "9"->{
                    tvStatus.text = "Exchange"
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    etStatus.isVisible = false
                    btnUpdateStatus.isVisible = false
                    llComplete.isVisible = false
                    clReturn.isVisible = true
                }
            }*/
        }
    }
}