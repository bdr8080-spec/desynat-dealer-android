package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemPlanBinding
import com.desynatdealer.databinding.ItemPlanFeatureBinding
import com.desynatdealer.ui.home.response.GetAllPlanResponse

class FeaturePlanAdapter(var context:Context, var featureList: ArrayList<SubscriptionAdapter.FeatureList>) : RecyclerView.Adapter<FeaturePlanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPlanFeatureBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        holder.binding.apply {
            tvFeature.text = featureList[position].feature
            if (featureList[position].count!=0) {
                tvCount.text = featureList[position].count.toString()
            }
        }

    }


}
