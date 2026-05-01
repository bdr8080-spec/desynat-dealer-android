package com.desynatdealer.ui.home.cms

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.base.BaseFragment
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.FragmentTicketDetailBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.ui.home.cms.response.GetAllRaisedTicketResponse
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TicketDetailFragment : BaseFragment() {
    private var binding: FragmentTicketDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var ticketData: GetAllRaisedTicketResponse.Data.Data?=null


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
        val itemJson = arguments?.getString("ticketData")
        if (!itemJson.isNullOrEmpty()) {
            ticketData = Gson().fromJson(itemJson, GetAllRaisedTicketResponse.Data.Data::class.java)
            setData()
        }
        binding?.apply {

        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDoc.setOnClickListener {
                CommonMethod.imagePreviewDialog(requireActivity(),arrayListOf(ticketData?.file.toString()))
            }
        }
    }

    override fun observe() {
    }

    private fun setData() {
        binding?.apply {
            tvDescription.text = ticketData?.description
            tvTitle.text = ticketData?.title
            tvTicketType.text = ticketData?.ticketTypeDetails?.name
            tvTicketIdValue.text = ticketData?.ticketId
            tvRaisedOn.text =
                CommonMethod.customizeChangeDateFormat(ticketData?.createdAt.toString(), outputFormat = "dd MMM yy, hh:mm a")
            when(ticketData?.status){
                0->{
                    tvStatusValue.text = getString(R.string.in_progress)
                    tvStatusValue.setTextColor(Color.parseColor("#D72E04"))

                }
                1->{
                    tvStatusValue.text = getString(R.string.resolved)
                    tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                }
            }

            if (ticketData?.file!=null){
                ivDoc.isVisible = true
                tvAttachment.isVisible = true
                Glide.with(requireActivity()).load(ticketData?.file).placeholder(R.drawable.ic_placeholder).into(ivDoc)
            }
        }
    }

}