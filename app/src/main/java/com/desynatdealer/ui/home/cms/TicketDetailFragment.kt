package com.desynatdealer.ui.home.cms

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentTicketDetailBinding
import com.desynatdealer.utils.CommonMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TicketDetailFragment : BaseFragment(){
private var binding: FragmentTicketDetailBinding? = null
private val viewModel: HomeViewModel by viewModels()
var isObserver = false

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    binding = FragmentTicketDetailBinding.inflate(layoutInflater, container, false)
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
            tvDescription.text = ""
            tvTicketIdValue.text = ""
            tvRaisedOn.text = CommonMethod.customizeChangeDateFormat("", outputFormat = "dd MMM yy, hh:mm a")
          /*  when(){
                0->{
                    tvStatusValue.text = "In Progress"
                    tvStatusValue.setTextColor(Color.parseColor("#D72E04"))

                }
                1->{
                    tvStatusValue.text = "Resolved"
                    tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                }
            }*/
        }
    }
}