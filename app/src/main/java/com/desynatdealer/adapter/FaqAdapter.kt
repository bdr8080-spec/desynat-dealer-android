package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemFaqBinding

class FaqAdapter(var context:Context, var faqList: ArrayList<String>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val faqData = faqList[position]
            tvQuestion.text = ""
            tvAnswer.text = ""
//
//            if (isSelected){
//                ivDropDown.setImageResource(R.drawable.ic_faq_up_arrow)
//                tvAnswer.isVisible = true
//            }
//            else{
//                ivDropDown.setImageResource(R.drawable.ic_faq_down_arrow)
//                tvAnswer.isVisible = false
//            }

            clQuestion.setOnClickListener {

                notifyDataSetChanged()
            }
        }
        }

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
