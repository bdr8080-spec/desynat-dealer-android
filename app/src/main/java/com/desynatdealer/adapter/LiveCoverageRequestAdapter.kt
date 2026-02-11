package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemLiveCoverageRequestBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.utils.CommonMethod

class LiveCoverageRequestAdapter(var context:Context, var liveRequestList: ArrayList<GetAllBannerResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<LiveCoverageRequestAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemLiveCoverageRequestBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val liveRequestData = liveRequestList[position]

                tvRequestId.text = "#${liveRequestData?.orderId}"
                tvLiveRequest.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
                tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
                when(liveRequestData?.adminVerify){
                    "0" ->{
                        tvStatusValue.text = "Pending"
                        tvStatusValue.setTextColor(Color.parseColor("#E28F12"))
                        btnCancelRequest.isVisible = true
                        tvRejectedOn.isVisible = false
                        tvRejectedOnValue.isVisible = false
                        tvReason.isVisible = false
                        tvRejectedReason.isVisible = false
                    }
                    "1" ->{
                        tvStatusValue.text = "Completed"
                        tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                        btnCancelRequest.isVisible = false
                        tvRejectedOn.isVisible = false
                        tvRejectedOnValue.isVisible = false
                        tvReason.isVisible = false
                        tvRejectedReason.isVisible = false
                    }
                    "2"->{
                        tvStatusValue.text = "Rejected"
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        btnCancelRequest.isVisible = false
                        tvRejectedOn.isVisible = true
                        tvRejectedOnValue.isVisible = true
                        if (liveRequestData?.cancelledReason!=null) {
                            tvReason.isVisible = true
                            tvRejectedReason.isVisible = true
                            tvRejectedReason.text = liveRequestData?.cancelledReason
                        }
                        tvRejectedOnValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")

                    }
                }

                btnCancelRequest.setOnClickListener {
                    onClick.onItemClick(position,2)
                }

            root.setOnClickListener {
                onClick.onItemClick(position,1)
            }

        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLiveCoverageRequestBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return liveRequestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(position)
    }


}
