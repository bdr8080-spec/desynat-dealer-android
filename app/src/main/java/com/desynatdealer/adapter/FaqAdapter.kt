package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemFaqBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdriver.ui.home.cms.response.GetFaqResponse

class FaqAdapter(var context:Context, var faqList: ArrayList<GetFaqResponse.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val faqData = faqList[position]
            tvQuestion.text = if (Constants.language=="ar")  CommonMethod.setHtmlText(faqData?.questionArabic.toString()) else  CommonMethod.setHtmlText(faqData?.questionEnglish.toString())
            tvAnswer.text =  if (Constants.language=="ar")  CommonMethod.setHtmlText(faqData?.answerArabic.toString()) else  CommonMethod.setHtmlText(faqData?.answerEnglish.toString())

            if (faqData?.isSelected == true){
                ivDropDown.setImageResource(R.drawable.ic_faq_up_arrow)
                tvAnswer.isVisible = true
            }
            else{
                ivDropDown.setImageResource(R.drawable.ic_faq_down_arrow)
                tvAnswer.isVisible = false
            }

            clQuestion.setOnClickListener {
                faqList[position].isSelected = !faqList[position].isSelected
                notifyItemChanged(position)
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
