package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemTicketBinding
import com.desynatdealer.utils.CommonMethod

class TicketAdapter(var context:Context, var ticketList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val ticketData = ticketList[position]

           /* tvTicketIdValue.text = ticketData?.sellerDetails?.firstName
            tvTicketType.text = ticketData?.firstName
            tvResolvedOn.text = CommonMethod.customizeChangeDateFormat(ticketData?.createdAt.toString(), outputFormat = "dd MMM yy, hh:mm a")
            tvRaisedOn.text = CommonMethod.customizeChangeDateFormat(ticketData?.createdAt.toString(), outputFormat = "dd MMM yy, hh:mm a")

                when(ticketData?.adminVerify){
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
