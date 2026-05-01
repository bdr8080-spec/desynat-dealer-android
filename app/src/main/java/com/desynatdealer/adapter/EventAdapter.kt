package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemEventBinding
import com.desynatdealer.ui.home.liveStreaming.response.GetLiveStreamingRoomResponse
import com.desynatdealer.ui.home.liveStreaming.response.GetOtherLiveStreamingRoomResponse
import com.desynatdealer.utils.CommonMethod
import java.text.SimpleDateFormat
import java.util.Date

class EventAdapter(var context: Context, var list : List<GetOtherLiveStreamingRoomResponse.Data>, val click : OnCLick) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventAdapter.ViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            tvName.text = item?.sellerDetails?.firstName+" "+item?.sellerDetails?.lastName
            tvLocation.text = item?.timeslotDetails?.countryInfo?.name
            Glide.with(ivProfileImage).load(item?.sellerDetails?.profilePic).transform(CircleCrop()).into(ivProfileImage)
            tvJoinedOn.text = "${context.getString(R.string.joined_on)} ${CommonMethod.customizeChangeDateFormat(item?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"
            tvLiveTiming.text = "${context.getString(R.string.live_timing)} ${CommonMethod.customizeChangeDateFormat(item?.timeslotDetails?.date.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yy")}"

            val isShow = isLiveTime(item.timeslotDetails)
            btnWatchLive.apply {
                alpha = if (isShow) 1f else 0.4f
                isClickable = isShow
                isEnabled = isShow
            }
            tvTime.text = "${CommonMethod.customizeChangeDateFormatZone(item?.timeslotDetails?.from.toString(),"hh:mm a","hh:mm a")}"
            btnWatchLive.setOnClickListener {
                click.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnCLick{
        fun onClick(position: Int)
    }

    private fun isLiveTime(timeslot: GetOtherLiveStreamingRoomResponse.Data.TimeslotDetails): Boolean {
        return try {
            val date = timeslot.date
            val fromTime = timeslot.from
            val toTime = timeslot.to

            val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.ENGLISH)

            val fromTimeDefault = CommonMethod.customizeChangeDateFormatZone(fromTime,"hh:mm a","hh:mm a")
            val toTimeDefault = CommonMethod.customizeChangeDateFormatZone(toTime,"hh:mm a","hh:mm a")

            val startDateTime = formatter.parse("$date $fromTimeDefault")
            val endDateTime = formatter.parse("$date $toTimeDefault")

            val currentTime = Date()

            currentTime.after(startDateTime) && currentTime.before(endDateTime)

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}


