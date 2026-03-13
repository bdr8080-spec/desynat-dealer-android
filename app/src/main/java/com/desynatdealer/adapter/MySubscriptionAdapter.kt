package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemMyPlanBinding
import com.desynatdealer.ui.home.response.GetAllPlanResponse
import com.desynatdealer.utils.CommonMethod

class MySubscriptionAdapter(var context:Context, var subscriptionList: ArrayList<GetAllPlanResponse.Data>, var subscriptionClick:SubscriptionClick) : RecyclerView.Adapter<MySubscriptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemMyPlanBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var subscriptionData = subscriptionList[position]

            if (subscriptionData.status==1){
                btnRenew.isVisible = false
                tvStatus.text = "Active"
                btnRenew.text = "Upgrade Plan"
                CommonMethod.applyGradientText(tvStatus)
            }
            else{
                btnRenew.isVisible = true
                tvStatus.paint.shader = null
                tvStatus.setTextColor(context.getColor(R.color.black))
                tvStatus.text = "Expired"
                btnRenew.text = "Renew Plan"
            }

            tvPlanName.text = subscriptionData.planNameEnglish
            tvAmount.text = "KD ${CommonMethod.formatPrice(subscriptionData.price)}"
            tvPurchaseOn.text = CommonMethod.customizeChangeDateFormat(subscriptionData.createdAt, outputFormat = "dd/MM/yyyy 'at' hh:mm a")
                when(subscriptionData.durationType){
                    1 -> {
                        tvPlanType.text = "for ${subscriptionData.duration} Monthly"
                    }
                    2 -> {
                        tvPlanType.text = "for ${subscriptionData.duration} Yearly"
                    }
                    else -> {
                        tvPlanType.text = "for ${subscriptionData.duration} Monthly"
                    }
                }

        }
        }

    interface SubscriptionClick{
        fun onSubscriptionClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyPlanBinding.inflate(
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
