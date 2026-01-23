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
import com.desynatdealer.ui.home.dealManagement.response.GetAllDealResponse
import com.desynatdealer.utils.CommonMethod

class DealAdapter(
    var context: Context,
    var dealList: ArrayList<GetAllDealResponse.Data.ListedDeal>,
    var onClick: OnClick
) : RecyclerView.Adapter<DealAdapter.ViewHolder>() {

    private var dealType = "1"

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
                    when {
                        CommonMethod.isUpcoming(dealData?.from.toString()) -> {
                            tvStatusValue.text = "Upcoming"
                            tvStatusValue.paint.shader = null
                            tvStatusValue.setTextColor(Color.parseColor("#000000"))
                        }

                        CommonMethod.isActive(dealData?.from.toString(), dealData?.to.toString()) -> {
                            tvStatusValue.text = "Active"
                            CommonMethod.applyGradientText(tvStatusValue)
                            tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        }

                        CommonMethod.isExpired(dealData?.from.toString()) -> {
                            tvStatusValue.paint.shader = null
                            tvStatusValue.text = "Expired"
                            tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        }
                    }
                }

                "3" -> {
                    tvStartFrom.isVisible = false
                    clDealInfo.isVisible = true
                    tvStatusValue.text = "Expired"
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                }
            }

            ivDeal.load(dealData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            CommonMethod.applyGradientText(tvStartFrom)
            tvStartFrom.text = CommonMethod.customizeChangeDateFormat(dealData?.from.toString(),"yyyy-MM-dd","dd MMM yyyy")
            tvStartsOnValue.text = CommonMethod.customizeChangeDateFormat(dealData?.from.toString(),"yyyy-MM-dd","dd MMM yyyy")
            tvFromValue.text = ""
            tvProductsAddedValue.text = dealData?.sellerDealCount

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
