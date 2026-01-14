package com.desynatdealer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemChatListBinding
import com.desynatdealer.ui.home.chat.response.GetChatListResponse
import com.desynatdealer.utils.CommonMethod

class ChatListAdapter(var context:Context, var chatList: ArrayList<GetChatListResponse.Data>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    interface ChatClick{
        fun onChatClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = chatList[position]
        var status = "1"
        holder.binding.apply {
            ivUser.load(chatData?.userDetails?.profileImage){
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            tvName.text = chatData?.userDetails?.fullName
            if (chatData?.lastChat=="null"&&chatData?.lastChat==null&&chatData?.lastChat==""){
                tvLastMsg.isVisible = false
            }
            else{
                tvLastMsg.isVisible = true
            }
            tvLastMsg.text = chatData?.lastChat
            if (chatData.lastChatTime!=null&&chatData.lastChatTime!="null"&&chatData.lastChatTime!="") {
                tvTime.text = CommonMethod.formatChatTime(
                    chatData?.lastChatTime.toString()
                )

            }
            else{
                tvTime.text = CommonMethod.formatChatTime(CommonMethod.customizeChangeDateFormat(
                    chatData?.updatedAt.toString(), outputFormat = "dd MMM yyyy, hh:mm a"
                ) )
            }
            if (chatData?.type=="2"){
                tvStatus.isVisible = true
                when(chatData?.action){
                    "0"->{
                        tvStatus.text = "New"
                        tvStatus.setTextColor(Color.parseColor("#EA9413"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1AEA9413"))
                    }
                    "1"->{
                        tvStatus.text = "Quotation Sent"
                        tvStatus.setTextColor(Color.parseColor("#172B85"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A172B85"))
                    }
                    "2"->{
                        tvStatus.text = "Quotation Modified"
                        tvStatus.setTextColor(Color.parseColor("#172B85"))
                        tvStatus.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#1A172B85"))
                    }
                    "4"->{
                        tvStatus.text = "Quotation Rejected"
                        tvStatus.setTextColor(Color.parseColor("#D53B36"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1AD53B36"))
                    }
                    "5"->{
                        tvStatus.text = "Quotation Accepted"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                    }
                }
            }
            else{
                tvStatus.isVisible = false
            }

        }

        holder.itemView.setOnClickListener {
            chatClick.onChatClick(position)
        }

    }


}
