package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemPlanBinding
import com.desynatdealer.ui.home.response.GetAllPlanResponse

class SubscriptionAdapter(var context:Context, var subscriptionList: ArrayList<GetAllPlanResponse.Data>, var subscriptionClick:SubscriptionClick) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface SubscriptionClick{
        fun onSubscriptionClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlanBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var subscriptionData = subscriptionList[position]
        holder.binding.apply {
            when(position){
                0->tvPackage.setBackgroundResource(R.drawable.pro)
                1->tvPackage.setBackgroundResource(R.drawable.plus)
                2->tvPackage.setBackgroundResource(R.drawable.growth)
                else ->tvPackage.setBackgroundResource(R.drawable.free)
            }

            tvPackage.text = subscriptionData.planNameEnglish
            tvPrice.text = "KD ${subscriptionData.price}"
            when(subscriptionData.durationType){
                1 -> {
                    tvDuration.text = "for ${subscriptionData.duration} Month"
                }
                2 -> {
                    tvDuration.text = "for ${subscriptionData.duration} Year"
                }
                else -> {
                    tvDuration.text = "for ${subscriptionData.duration} Month"
                }
            }

        }

        val featureList = ArrayList<FeatureList>() // new list per item

        featureList.clear()
        /*featureList.add(FeatureList("Number of Products: ",subscriptionData.noOfProducts))
        featureList.add(FeatureList("Number of Videos (Reels): ",subscriptionData.noOfVideos))
        featureList.add(FeatureList("Number of Highlights: ",subscriptionData.noOfHighlights))
        featureList.add(FeatureList("Stories: ",subscriptionData.stories))
        featureList.add(FeatureList("${subscriptionData.noOfEmployee} Store Employees",0))
        featureList.add(FeatureList("Commission: ${subscriptionData.commissionPercentage}",0))*/


        subscriptionData.apply {
            if (noOfProducts != 0)
                featureList.add(FeatureList("Number of Products: ", noOfProducts))

            if (noOfVideos != 0)
                featureList.add(FeatureList("Number of Videos (Reels): ", noOfVideos))

            if (noOfHighlights != 0)
                featureList.add(FeatureList("Number of Highlights: ", noOfHighlights))

            if (stories != 0)
                featureList.add(FeatureList("Stories: ", stories))

            if (noOfEmployee != 0)
                featureList.add(FeatureList("$noOfEmployee Store Employees", 0))

            // Handle commission formatting
            val commissionValue = commissionPercentage?.toString()?.toDoubleOrNull()
            if (commissionValue != null && commissionValue != 0.0) {
                if (commissionValue % 1.0 == 0.0){
                    val formattedCommission =  commissionValue.toInt()
                featureList.add(FeatureList("Commission: ", formattedCommission))}
                else {
                    val formattedCommission =  commissionValue.toString()
                    featureList.add(FeatureList("Commission: $formattedCommission", 0))

                }

            }
        }


        holder.binding.rvFeature.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = FeaturePlanAdapter(
                context = context,
                featureList)
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            setRecycledViewPool(null)
        }

        holder.itemView.setOnClickListener {
            subscriptionClick.onSubscriptionClick(position)
        }

    }

    data class FeatureList(
        var feature:String,
        var count:Int?=0
    )

}
