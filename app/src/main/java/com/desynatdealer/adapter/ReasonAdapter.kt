package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemReasonBinding
import com.desynatdealer.R

class ReasonAdapter(var context:Context, var reasonList: ArrayList<String>, var onClick: OnClick) : RecyclerView.Adapter<ReasonAdapter.ViewHolder>() {
    var isSelected = -1
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemReasonBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            tvReason.text = reasonList[position]

            if (isSelected==position){
                ivImage.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_radio_unselected)
            }

            root.setOnClickListener {
                isSelected = position
                notifyDataSetChanged()

                onClick.onClickItem(position)

            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return reasonList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
