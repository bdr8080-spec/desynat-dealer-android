package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemTicketBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatdealer.utils.Constants

class TicketAdapter(var context:Context, var ticketList: ArrayList<GetAllRaisedTicketResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val ticketData = ticketList[position]

            tvTicketIdValue.text = ticketData?.ticketId
            tvTicketType.text = if(Constants.language=="ar") ticketData?.ticketTypeDetails?.nameArabic else ticketData?.ticketTypeDetails?.name
            tvRaisedOn.text = CommonMethod.customizeChangeDateFormat(ticketData?.createdAt.toString(), outputFormat = "dd MMM yy, hh:mm a")
            tvResolvedOn.text = CommonMethod.customizeChangeDateFormat(ticketData?.updatedAt.toString(), outputFormat = "dd MMM yy, hh:mm a")

            when(ticketData?.status){
                0->{
                    tvResolved.isVisible = false
                    tvResolvedOn.isVisible = false
                    tvStatusValue.text = context.getString(R.string.in_progress)
                    tvStatusValue.setTextColor(Color.parseColor("#D72E04"))

                }
                1->{
                    tvResolved.isVisible = true
                    tvResolvedOn.isVisible = true
                    tvStatusValue.text = context.getString(R.string.resolved)
                    tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                }
            }

            root.setOnClickListener {
                onClick.click(position,1)
            }
        }
        }

    interface OnClick{
        fun click(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(position)
    }

}
