package com.desynatdealer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemChatListBinding

class ChatListAdapter(var context:Context, var chatList: ArrayList<String>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

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

            //tvName.text = chatData?.productNameEn
           when(status){
                "1"->{
                    tvStatus.text = "New"
                    tvStatus.setTextColor(Color.parseColor("#EA9413"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1AEA9413"))
                }
                "2"->{
                    tvStatus.text = "Quotation Sent"
                    tvStatus.setTextColor(Color.parseColor("#172B85"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A172B85"))
                }
                "3"->{
                    tvStatus.text = "Quotation Accepted"
                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A16B364"))
                }
            }
        }

        holder.itemView.setOnClickListener {
            chatClick.onChatClick(position)
        }

    }


}
