package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemReviewImgBinding

class ReviewImageAdapter(var context:Context, var reviewList: ArrayList<String>) : RecyclerView.Adapter<ReviewImageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemReviewImgBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val data = reviewList[position]
            Glide.with(context).load(data).placeholder(R.drawable.ic_placeholder).into(ivImage)
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReviewImgBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


}
