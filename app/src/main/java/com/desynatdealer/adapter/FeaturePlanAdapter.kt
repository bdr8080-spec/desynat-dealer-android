package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemPlanFeatureBinding

class FeaturePlanAdapter(var context:Context, var featureList: ArrayList<SubscriptionAdapter.FeatureList>) : RecyclerView.Adapter<FeaturePlanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPlanFeatureBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            tvFeature.text = featureList[position].feature
      //      tvFeature.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_feature,0,0,0)
            if (featureList[position].count!=0) {
                tvCount.text = featureList[position].count.toString()
            }
            else if (featureList[position].feature=="${context.getString(R.string.number_of_products)}: "&&featureList[position].count==0){
                tvCount.text = context.getString(R.string.unlimited)
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlanFeatureBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


}
