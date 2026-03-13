package com.desynatdealer.ui.home.mySubscriptionPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentPlanDetailBinding
import com.desynatdealer.utils.CommonMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PlanDetailFragment : BaseFragment() {

    private var binding: FragmentPlanDetailBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanDetailBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Details"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnUpgrade.setOnClickListener {

            }
        }
    }

    override fun observe() {
    }

    private fun setData(){
        binding?.apply {
//            if (status=="Active"){
//                tvStatus.text = "Active"
//                btnUpgrade.text = "Upgrade Plan"
//                CommonMethod.applyGradientText(tvStatus)
//            }
//            else{
//                tvStatus.paint.shader = null
//                tvStatus.setTextColor(requireActivity().getColor(R.color.black))
//                tvStatus.text = "Expired"
//                btnUpgrade.text = "Renew Plan"
//            }
        }

    }

}