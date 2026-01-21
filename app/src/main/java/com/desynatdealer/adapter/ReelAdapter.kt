package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemReelBinding

class ReelAdapter(var context:Context, var reelList: ArrayList<String>,var onClick:OnClick) : RecyclerView.Adapter<ReelAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemReelBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){

                if (position==0){
                    clAdd.isVisible = true
                    ivReel.isVisible = false
                    ivPlay.isVisible = false
                }
                else{
                    clAdd.isVisible = false
                    ivReel.isVisible = true
                    ivPlay.isVisible = true
                }

                clAdd.setOnClickListener {
                    onClick.onItemClick(position,1)
                }

                ivReel.setOnClickListener {
                    onClick.onItemClick(position,2)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReelBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
