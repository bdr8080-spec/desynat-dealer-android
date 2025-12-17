package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemAddBannerBinding
import com.desynatdealer.utils.CommonMethod

class BannerAdapter(var context:Context, var bannerList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemAddBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddBannerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            var status = "1"
            when(status){
                "1" ->{
                    tvStatusValue.text = "Pending"
                    tvStatusValue.setTextColor(Color.parseColor("#E28F12"))
                    btnPay.isVisible = false
                    tvReason.isVisible = false
                    tvRejectedReason.isVisible = false
                }
                "2" ->{
                    tvStatusValue.text = "Confirmed"
                    tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                    btnPay.isVisible = true
                    tvReason.isVisible = false
                    tvRejectedReason.isVisible = false
                }
                "3" ->{
                    tvStatusValue.text = "Active"
                    CommonMethod.applyGradientText(tvStatusValue)
                    btnPay.isVisible = false
                    tvReason.isVisible = false
                    tvRejectedReason.isVisible = false
                }
                "4" ->{
                    tvStatusValue.text = "Rejected"
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                    btnPay.isVisible = false
                    tvReason.isVisible = true
                    tvRejectedReason.isVisible = true
                }
            }

            btnPay.setOnClickListener {
                onClick.onItemClick(position,2)
            }
        }

        holder.itemView.setOnClickListener {
            onClick.onItemClick(position,1)
        }

    }


}
