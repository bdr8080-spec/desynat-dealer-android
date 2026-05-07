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
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference

class SubscriptionAdapter(var context:Context, var subscriptionList: ArrayList<GetAllPlanResponse.Data>, var subscriptionClick:SubscriptionClick) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var subscriptionData = subscriptionList[position]
            var canAction = true
            var canView = true
            if (SharedPreference.get(context).userType == "1") {
                canAction = CommonMethod.getPermission(context,"seller.subscriptionPlan")?.canAction == 1
                canView = CommonMethod.getPermission(context,"seller.subscriptionPlan")?.canView == 1
            }

            if (subscriptionData.isPurchased){
                btnContinue.isInvisible = true
            }
            else{
                if (canAction){
                    btnContinue.isVisible = true
                }
                else{
                    btnContinue.isInvisible = true
                }

            }
                when(position){
                    0->tvPackage.setBackgroundResource(R.drawable.pro)
                    1->tvPackage.setBackgroundResource(R.drawable.plus)
                    2->tvPackage.setBackgroundResource(R.drawable.growth)
                    else ->tvPackage.setBackgroundResource(R.drawable.free)
                }

                tvPackage.text = if (Constants.language=="ar") subscriptionData.planNameArabic else subscriptionData.planNameEnglish
                tvPrice.text = "${CommonMethod.formatPrice(subscriptionData.price)} ${
                    context.getString(
                        R.string.kd
                    )
                }"
                when(subscriptionData.durationType){
                    1 -> {
                        tvDuration.text = "${context.getString(R.string.for_text)} ${subscriptionData.duration} ${
                            context.getString(
                                R.string.month
                            )
                        }"
                    }
                    2 -> {
                        tvDuration.text = "${context.getString(R.string.for_text)} ${subscriptionData.duration} ${
                            context.getString(
                                R.string.year
                            )
                        }"
                    }
                    else -> {
                        tvDuration.text = "${context.getString(R.string.for_text)} ${subscriptionData.duration} ${
                            context.getString(
                                R.string.month
                            )
                        }"
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
                    featureList.add(FeatureList("${context.getString(R.string.number_of_products)}", noOfProducts))

                else  featureList.add(FeatureList("${context.getString(R.string.number_of_products)}", 0))

                if (noOfVideos != 0)
                    featureList.add(FeatureList("${context.getString(R.string.number_of_videos)}", noOfVideos))

                if (noOfHighlights != 0)
                    featureList.add(FeatureList("${context.getString(R.string.number_of_highlights)}", noOfHighlights))

                if (stories != 0)
                    featureList.add(FeatureList("${context.getString(R.string.stories)}", stories))

                if (noOfEmployee != 0)
                    featureList.add(FeatureList("$noOfEmployee ${context.getString(R.string.store_employees)}", 0))

                // Handle commission formatting
                val commissionValue = commissionPercentage?.toString()?.toDoubleOrNull()
                if (commissionValue != null && commissionValue != 0.0) {
                    if (commissionValue % 1.0 == 0.0){
                        val formattedCommission =  commissionValue.toInt()
                        featureList.add(FeatureList("${context.getString(R.string.commission)}", formattedCommission))}
                    else {
                        val formattedCommission =  commissionValue.toString()
                        featureList.add(FeatureList("${context.getString(R.string.commission)} $formattedCommission", 0))
                    }
                }

                if (noOfFreeMonths != null&&noOfFreeMonths != "null"&&noOfFreeMonths != "")
                    featureList.add(FeatureList("${context.getString(R.string.number_of_free_month)}", noOfFreeMonths.toIntOrNull()))

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
