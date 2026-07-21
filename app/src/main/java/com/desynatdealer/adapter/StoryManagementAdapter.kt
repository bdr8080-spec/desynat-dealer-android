package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAddBannerBinding
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.utils.CommonMethod

class StoryManagementAdapter(
    var context: Context,
    var storyList: ArrayList<GetStoryResponse.Data>,
    var onClick: OnClick
) : RecyclerView.Adapter<StoryManagementAdapter.ViewHolder>() {

    interface OnClick {
        fun onItemClick(position: Int, type: Int)
    }

    inner class ViewHolder(val binding: ItemAddBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val storyData = storyList[position]

            Glide.with(context).load(if (storyData.thumbnail.isNullOrEmpty()) storyData.file else storyData.thumbnail)
                .placeholder(R.drawable.ic_placeholder).into(ivBanner)

            tvRequestId.text = "#${storyData.id}"
            
            // Amount is not applicable for stories usually, or at least not in this response
            tvAmount.isVisible = false
            tvTotalAmount.isVisible = false
            
            tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(
                storyData.createdAt.toString(),
                outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a"
            )

            // 0-pending, 1-approved, 2-rejected
            when (storyData.status) {
                "0" -> {
                    tvStatusValue.text = context.getString(R.string.pending)
                    tvStatusValue.paint.shader = null
                    tvStatusValue.setTextColor(Color.parseColor("#E28F12"))
                    btnPay.isVisible = false
                    tvReason.isVisible = false
                    tvRejectedReason.isVisible = false
                }

                "1" -> {
                    tvStatusValue.text = context.getString(R.string.approved)
                    tvStatusValue.paint.shader = null
                    tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                    btnPay.isVisible = false
                    tvReason.isVisible = false
                    tvRejectedReason.isVisible = false
                }

                "2" -> {
                    tvStatusValue.text = context.getString(R.string.rejected)
                    tvStatusValue.paint.shader = null
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                    btnPay.isVisible = false
                    tvReason.isVisible = true
                    tvRejectedReason.isVisible = true
                    tvRejectedReason.text = storyData.caption ?: "" // Using caption as reason if that's where it is, or maybe there's another field
                }
                else -> {
                    tvStatusValue.text = storyData.status
                }
            }

            tvCategory.isVisible = false
            tvCategoryValue.isVisible = false
            tvSubCategory.isVisible = false
            tvSubCategoryValue.isVisible = false

            btnPay.setOnClickListener {
                onClick.onItemClick(position, 2)
            }

            root.setOnClickListener {
                onClick.onItemClick(position, 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddBannerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
