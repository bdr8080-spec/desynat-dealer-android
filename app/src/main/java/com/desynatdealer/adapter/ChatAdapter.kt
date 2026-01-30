package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemChatBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.ui.home.chat.response.ChatMessage
import com.desynatdealer.ui.home.chat.toSafeInt
import com.desynatdealer.ui.home.chat.toSafeString
import com.desynatdealer.utils.SharedPreference

class ChatAdapter(var context:Context, var chatList: ArrayList<ChatMessage>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    interface ChatClick{
        fun onChatClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val chatData = chatList[position]
            val isArabicApp = SharedPreference.get(context).language == "ar"
            val arText = chatData.messageAr.toSafeString()
            val enText = chatData.message.toSafeString()

                if (chatData.by.toSafeString() == "seller") {
                    relMy.isVisible = true
                    relOther.isVisible = false

                    if (chatData.type.toSafeInt()==1){
                        ivMyMsg.isVisible = true
                        tvMyMsg.isVisible = false
                        Glide.with(context).load(chatData.message.toSafeString()).placeholder(R.drawable.ic_placeholder).into(ivMyMsg)
                    }
                    else{
                        ivMyMsg.isVisible = false
                        tvMyMsg.isVisible = true
                        tvMyMsg.text = when {
                            isArabicApp && arText.isNotEmpty() -> arText
                            enText.isNotEmpty() -> enText
                            else -> arText
                        }
                    }

                    if (chatData.read.toSafeInt()==1){
                        ivRead.isVisible = true
                    }
                    else{
                        ivRead.isVisible = false
                    }
                    tvMyMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toSafeString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mm a")

                }
                else{
                    relMy.isVisible = false
                    relOther.isVisible = true
                    if (chatData.type.toSafeInt()==1){
                        ivOtherMsg.isVisible = true
                        tvOtherMsg.isVisible = false
                        Glide.with(context).load(chatData.message.toSafeString()).placeholder(R.drawable.ic_placeholder).into(ivOtherMsg)
                    }
                    else{
                        ivOtherMsg.isVisible = false
                        tvOtherMsg.isVisible = true
                        tvOtherMsg.text = when {
                            isArabicApp && arText.isNotEmpty() -> arText
                            enText.isNotEmpty() -> enText
                            else -> arText
                        }
                    }
                    tvOtherMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toSafeString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mm a")
                }

                ivMyMsg.setOnClickListener {
                    chatClick.onChatClick(position)
                }

                ivOtherMsg.setOnClickListener {
                    chatClick.onChatClick(position)
                }

            }
        }

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
       holder.bind(position)

    }

}