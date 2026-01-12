package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemChatBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.ui.home.chat.response.ChatMessage

class ChatAdapter(var context:Context, var chatList: ArrayList<ChatMessage>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    interface ChatClick{
        fun onChatClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = chatList[position]
        holder.binding.apply {

            if (chatData.by == "seller") {
                relMy.isVisible = true
                relOther.isVisible = false

                if (chatData.type=="1"){
                    ivMyMsg.isVisible = true
                    tvMyMsg.isVisible = false
                    ivMyMsg.load(chatData.message){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                else{
                    ivMyMsg.isVisible = false
                    tvMyMsg.isVisible = true
                    tvMyMsg.text = chatData.message
                }

                if (chatData.read==1){
                    ivRead.isVisible = true
                }
                else{
                    ivRead.isVisible = false
                }
                tvMyMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")

            }
            else{
                relMy.isVisible = false
                relOther.isVisible = true
                if (chatData.type=="1"){
                    ivOtherMsg.isVisible = true
                    tvOtherMsg.isVisible = false
                    ivOtherMsg.load(chatData.message){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                else{
                    ivOtherMsg.isVisible = false
                    tvOtherMsg.isVisible = true
                    tvOtherMsg.text = chatData.message
                }
                tvOtherMsg.text = chatData.message
                tvOtherMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")
            }

        }

        holder.itemView.setOnClickListener {
            chatClick.onChatClick(position)
        }

    }

}