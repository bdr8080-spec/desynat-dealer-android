package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatdealer.databinding.ItemEventBinding
import com.desynatdealer.ui.home.liveStreaming.response.GetOtherLiveStreamingRoomResponse
import com.desynatdealer.utils.CommonMethod

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
            tvJoinedOn.text = "Joined on ${CommonMethod.customizeChangeDateFormat(item?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"
            tvLiveTiming.text = "Live Timing ${CommonMethod.customizeChangeDateFormat(item?.timeslotDetails?.date.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yy")}"

            tvTime.text = "${item?.timeslotDetails?.from}"
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

}


