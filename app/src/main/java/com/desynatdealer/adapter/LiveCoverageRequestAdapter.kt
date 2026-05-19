package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemLiveCoverageRequestBinding
import com.desynatdealer.ui.home.liveCoverage.response.GetLiveRequestResponse
import com.desynatdealer.utils.CommonMethod
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class LiveCoverageRequestAdapter(var context:Context, var liveRequestList: ArrayList<GetLiveRequestResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<LiveCoverageRequestAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemLiveCoverageRequestBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val liveRequestData = liveRequestList[position]

                tvRequestId.text = "#${liveRequestData?.orderId}"
                tvLiveRequest.text = "${CommonMethod.customizeChangeDateFormat(liveRequestData?.timeslotDetails?.date.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy")} ${context.getString(R.string.at)} ${CommonMethod.customizeChangeDateFormatZone(liveRequestData?.timeslotDetails?.from.toString(),"hh:mm a","hh:mm a")}"

                tvRequestedOnValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.createdAt.toString(), outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a")
                when(liveRequestData?.adminVerify){
                    "0" ->{
                        tvStatusValue.text = context.getString(R.string.pending)
                        tvStatusValue.setTextColor(Color.parseColor("#E28F12"))
                        btnCancelRequest.isVisible = true
                        tvRejectedOn.isVisible = false
                        tvRejectedOnValue.isVisible = false
                        tvReason.isVisible = false
                        tvRejectedReason.isVisible = false
                    }
                    "1" ->{
                        val status = getSessionStatus(
                            liveRequestData?.timeslotDetails?.date.toString(),
                            liveRequestData?.timeslotDetails?.from.toString(),
                            liveRequestData?.timeslotDetails?.to.toString()
                        )
                        tvStatusValue.text = status
                        tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                        btnCancelRequest.isVisible = false
                        tvRejectedOn.isVisible = false
                        tvRejectedOnValue.isVisible = false
                        tvReason.isVisible = false
                        tvRejectedReason.isVisible = false
                    }
                    "2"->{
                        tvStatusValue.text = context.getString(R.string.rejected)
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        btnCancelRequest.isVisible = false
                        tvRejectedOn.isVisible = true
                        tvRejectedOnValue.isVisible = true
                        if (liveRequestData?.rejectReason!=null) {
                            tvReason.isVisible = true
                            tvRejectedReason.isVisible = true
                            tvRejectedReason.text = liveRequestData?.rejectReason
                        }
                        tvRejectedOnValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a")
                    }
                    "3"->{
                        tvStatusValue.text = context.getString(R.string.cancelled)
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        btnCancelRequest.isVisible = false
                        tvRejectedOn.text = context.getString(R.string.cancelled_on)
                        tvReason.text = context.getString(R.string.cancelled_reason)
                        tvRejectedOn.isVisible = true
                        tvRejectedOnValue.isVisible = true
                        if (liveRequestData?.cancelReason!=null) {
                            tvReason.isVisible = true
                            tvRejectedReason.isVisible = true
                            tvRejectedReason.text = liveRequestData?.cancelReason
                        }
                        tvRejectedOnValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a")
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

    fun getSessionStatus(
        dateStr: String,
        fromTimeStr: String,
        toTimeStr: String
    ): String {

        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

            val sessionDate = LocalDate.parse(dateStr, dateFormatter)
            val fromTime = LocalTime.parse(fromTimeStr, timeFormatter)
            val toTime = LocalTime.parse(toTimeStr, timeFormatter)

            val startDateTime = LocalDateTime.of(sessionDate, fromTime)
            val endDateTime = LocalDateTime.of(sessionDate, toTime)

            val currentDateTime = LocalDateTime.now()

            when {
                currentDateTime.isBefore(startDateTime) -> context.getString(R.string.confirmed)
                currentDateTime.isAfter(endDateTime) -> context.getString(R.string.completed)
                else -> context.getString(R.string.live)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            context.getString(R.string.confirmed) // fallback (you can change this)
        }
    }

}
