package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemDealBinding
import com.desynatdealer.ui.home.bannerManagement.response.GetAllBannerResponse
import com.desynatdealer.utils.CommonMethod

class DealAdapter(
    var context: Context,
    var dealList: ArrayList<GetAllBannerResponse.Data>,
    var onClick: OnClick
) : RecyclerView.Adapter<DealAdapter.ViewHolder>() {

    var dealType = "1"

    interface OnClick {
        fun onItemClick(position: Int, type: Int)
    }

    inner class ViewHolder(val binding: ItemDealBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val dealData = dealList[position]

            when (dealType) {
                "1" -> {
                    tvStartFrom.isVisible = true
                    clDealInfo.isVisible = false
                }

                "2" -> {
                    tvStartFrom.isVisible = false
                    clDealInfo.isVisible = true
                    tvStatusValue.text = "Active"
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))

                    tvStatusValue.text = "Upcoming"
                    tvStatusValue.setTextColor(Color.parseColor("#000000"))
                }

                "3" -> {
                    tvStartFrom.isVisible = false
                    clDealInfo.isVisible = true
                    tvStatusValue.text = "Expired"
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                }
            }

            ivDeal.load(dealData?.uploadBanner) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            CommonMethod.applyGradientText(tvStartFrom)
            tvStartFrom.text = ""
            tvStartsOnValue.text = ""
            tvFromValue.text = ""
            tvProductsAddedValue.text = ""

            root.setOnClickListener {
                onClick.onItemClick(position, 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDealBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return dealList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setDealType(type: String) {
        dealType = type
        notifyDataSetChanged()
    }

}
