package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemAddBannerBinding
import com.desynatdealer.ui.home.profile.response.GetStoryResponse
import com.desynatdealer.utils.CommonMethod
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(position: Int) = with(binding) {
            val storyData = storyList[position]

            Glide.with(context)
                .load(if (storyData.thumbnail.isNullOrEmpty()) storyData.file else storyData.thumbnail)
                .placeholder(R.drawable.ic_placeholder).into(ivBanner)

            tvRequestId.text = "#${storyData.id}"

            // Amount is not applicable for stories usually, or at least not in this response
            tvAmount.isVisible = false
            tvTotalAmount.isVisible = false

            tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(
                storyData.createdAt.toString(),
                outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a"
            )

            when (storyData.adminVerify) {

                // Pending
                "0" -> {
                    tvStatusValue.paint.shader = null
                    tvStatusValue.text = context.getString(R.string.pending)
                    tvStatusValue.setTextColor(Color.parseColor("#E28F12"))

                    btnPay.isVisible = false
                    tvReason.isVisible = false
                    tvRejectedReason.isVisible = false
                }

                // Admin Approved
                "1" -> {

                    when (storyData.paymentStatus) {

                        // Payment Pending
                        "0" -> {
                            tvStatusValue.paint.shader = null
                            tvStatusValue.text = context.getString(R.string.confirmed)
                            tvStatusValue.setTextColor(Color.parseColor("#68CA44"))

                            btnPay.isVisible = true
                            tvReason.isVisible = false
                            tvRejectedReason.isVisible = false
                        }

                        // Payment Completed
                        "1" -> {

                            btnPay.isVisible = false
                            tvReason.isVisible = false
                            tvRejectedReason.isVisible = false

                            val today = LocalDate.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                            val start = try {
                                LocalDate.parse(storyData.startDate, formatter)
                            } catch (e: Exception) {
                                null
                            }

                            val end = try {
                                LocalDate.parse(storyData.endDate, formatter)
                            } catch (e: Exception) {
                                null
                            }

                            if (start != null && end != null) {

                                when {

                                    // Future -> Confirmed
                                    today.isBefore(start) -> {
                                        tvStatusValue.paint.shader = null
                                        tvStatusValue.text = context.getString(R.string.confirmed)
                                        tvStatusValue.setTextColor("#68CA44".toColorInt())
                                    }

                                    // Active
                                    (today.isEqual(start) || today.isAfter(start)) &&
                                            (today.isEqual(end) || today.isBefore(end)) -> {

                                        tvStatusValue.paint.shader = null
                                        tvStatusValue.text = context.getString(R.string.active)
                                        tvStatusValue.setTextColor("#C4A176".toColorInt())
                                    }

                                    // Expired
                                    today.isAfter(end) -> {
                                        tvStatusValue.paint.shader = null
                                        tvStatusValue.text = context.getString(R.string.expired)
                                        tvStatusValue.setTextColor("#9E9E9E".toColorInt())
                                    }
                                }

                            } else {
                                // Fallback (same as iOS)
                                tvStatusValue.paint.shader = null
                                tvStatusValue.text = context.getString(R.string.confirmed)
                                tvStatusValue.setTextColor("#68CA44".toColorInt())
                            }
                        }

                        // Payment Rejected
                        "2" -> {
                            tvStatusValue.paint.shader = null
                            tvStatusValue.text = context.getString(R.string.rejected)
                            tvStatusValue.setTextColor("#D53B36".toColorInt())

                            btnPay.isVisible = false
                            tvReason.isVisible = true
                            tvRejectedReason.isVisible = true
                            tvRejectedReason.text =
                                storyData.rejectReason
                                    ?: context.getString(R.string.rejected_reason)
                        }
                    }
                }

                // Admin Rejected
                "2" -> {
                    tvStatusValue.paint.shader = null
                    tvStatusValue.text = context.getString(R.string.rejected)
                    tvStatusValue.setTextColor("#D53B36".toColorInt())

                    btnPay.isVisible = false
                    tvReason.isVisible = true
                    tvRejectedReason.isVisible = true
                    tvRejectedReason.text =
                        storyData.rejectReason ?: context.getString(R.string.rejected_reason)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseApiDate(date: String?): LocalDate? {
        return try {
            if (date.isNullOrBlank()) return null
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStoryStatus(startDate: String?, endDate: String?): Int {
        val today = LocalDate.now()

        val start = parseApiDate(startDate)
        val end = parseApiDate(endDate)

        if (start == null || end == null) {
            return 0 // Confirmed (fallback like iOS)
        }

        return when {
            today.isBefore(start) -> 0      // Confirmed
            !today.isBefore(start) && !today.isAfter(end) -> 1 // Active
            else -> 2                       // Expired
        }
    }
}
