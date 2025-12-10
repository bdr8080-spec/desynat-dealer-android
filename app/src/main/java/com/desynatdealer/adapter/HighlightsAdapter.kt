package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemHighlightsBinding
import com.desynatdealer.databinding.ItemReelBinding

class HighlightsAdapter(var context:Context, var highlightList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<HighlightsAdapter.ViewHolder>() {

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemHighlightsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHighlightsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return highlightList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            if (position==0){
                ivAdd.isVisible = true
                ivHighlight.isVisible = false
                tvHighlight.isVisible = false
            }
            else{
                ivAdd.isVisible = false
                ivHighlight.isVisible = true
                tvHighlight.isVisible = true
            }

            ivAdd.setOnClickListener {
                onClick.onItemClick(position,1)
            }

            ivHighlight.setOnClickListener {
                onClick.onItemClick(position,2)
            }
        }

    }


}
