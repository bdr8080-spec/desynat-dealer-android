package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemOrderBinding
import com.desynatdealer.ui.home.orderManagement.response.GetAllOrderResponse
import com.desynatdealer.utils.CommonMethod

class OrderAdapter(
    var context: Context,
    var orderList: ArrayList<GetAllOrderResponse.Data.AllOrder>,
    var orderClick: OrderClick
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    interface OrderClick {
        fun onOrderClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderData = orderList[position]
        holder.binding.apply {

            ivUser.load(orderData?.userDetails?.profileImage) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            ivProduct.load(orderData?.variationDetails?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvName.text = orderData?.userDetails?.fullName
            tvOrderIdValue.text = "#${orderData?.order?.id}"
            tvShippingLocation.text = orderData?.order?.deliveryDetails?.address
            tvQuantityValue.text = orderData?.variationDetails?.size?.name
            tvProductName.text = orderData?.productDetails?.productNameEn
            tvOrderDateTime.text = CommonMethod.customizeChangeDateFormat(
                orderData?.createdAt.toString(),
                outputFormat = "dd/MM/yyyy 'at' hh:mm a"
            )

            if (orderData?.paymentStatus == "2" || orderData?.paymentStatus == "3") {
                tvStatus.text = "Cancelled"
                tvStatus.setTextColor(Color.parseColor("#E70000"))
            }
            else {
                when (orderData?.order?.deliveryStatus) {
                    "1" -> {
                        tvStatus.text = "Confirmed"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                    }

                    "2" -> {
                        tvStatus.text = "Ongoing"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                    }

                    "3" -> {
                        tvStatus.text = "Delivered"
                        tvStatus.setTextColor(Color.parseColor("#047BD5"))
                    }

                    "4" -> {
                        tvStatus.text = "Cancelled"
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                    }

                    "5" -> {
                        tvStatus.text = "Return Req"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                    "7" -> {
                        tvStatus.text = "Ongoing"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                    }

                    "8" -> {
                        tvStatus.text = "Not Delivered"
                        tvStatus.setTextColor(Color.parseColor("#E70000"))
                    }

                    "9" -> {
                        tvStatus.text = "Exchange Req"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            orderClick.onOrderClick(position)
        }

    }


}
