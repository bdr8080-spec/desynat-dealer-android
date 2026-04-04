package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemPlanBinding
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.utils.CommonMethod

class SubscriptionAdapter(var context:Context, var subscriptionList: ArrayList<GetAllPlanResponse.Data>, var subscriptionClick:SubscriptionClick) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var subscriptionData = subscriptionList[position]

            if (subscriptionData.isPurchased){
                btnContinue.isInvisible = true
            }
            else{
                btnContinue.isVisible = true
            }
                when(position){
                    0->tvPackage.setBackgroundResource(R.drawable.pro)
                    1->tvPackage.setBackgroundResource(R.drawable.plus)
                    2->tvPackage.setBackgroundResource(R.drawable.growth)
                    else ->tvPackage.setBackgroundResource(R.drawable.free)
                }

                tvPackage.text = subscriptionData.planNameEnglish
                tvPrice.text = "${CommonMethod.formatPrice(subscriptionData.price)} KD"
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

                else  featureList.add(FeatureList("Number of Products: ", 0))

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

                if (noOfFreeMonths != null&&noOfFreeMonths != "null"&&noOfFreeMonths != "")
                    featureList.add(FeatureList("Number of Free Month: ", noOfFreeMonths.toIntOrNull()))

            }

            rvFeature.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = FeaturePlanAdapter(
                    context = context,
                    featureList)
                setHasFixedSize(false)
                isNestedScrollingEnabled = false
                setRecycledViewPool(null)
            }

            btnContinue.setOnClickListener {
                subscriptionClick.onSubscriptionClick(position)
            }
        }
        }

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
        holder.bind(position)
    }

    data class FeatureList(
        var feature:String,
        var count:Int?=0
    )

}
