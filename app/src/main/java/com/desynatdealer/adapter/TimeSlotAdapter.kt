package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemTimeSlotBinding
import com.desynatdealer.ui.home.liveCoverage.response.GetTimeSlotResponse

class TimeSlotAdapter(var context:Context, var timeSlotList: ArrayList<GetTimeSlotResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {
    var isSelected = -1
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemTimeSlotBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){

            tvTime.text = "${timeSlotList[position]?.from} - ${timeSlotList[position]?.to}"
            if (isSelected==position){
                tvTime.setTextColor(context.getColor(R.color.white))
                tvTime.background = context.getDrawable(R.drawable.selected_time_slot_gradient_bg)
            }
            else{
                tvTime.setTextColor(context.getColor(R.color.black))
                tvTime.background = context.getDrawable(R.drawable.stroke_gradient_bg)
            }
            root.setOnClickListener {
                isSelected = position
                notifyDataSetChanged()

                onClick.onClickItem(position)

            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return timeSlotList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


}
