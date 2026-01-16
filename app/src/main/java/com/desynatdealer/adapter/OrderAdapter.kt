package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
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
            tvOrderIdValue.text = "#${orderData?.id}"
            tvShippingLocation.text = orderData?.order?.deliveryDetails?.address
            tvQuantityValue.text = orderData?.quantity
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
                when (orderData?.deliveryStatus) {
                    "1" -> {
                        if (orderData?.rEApprove=="1"&&orderData?.returnType=="1"&&orderData?.assignStatus=="0"){
                            tvStatus.text = "Return Req Accepted"
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        else if (orderData?.rEApprove=="1"&&orderData?.returnType=="1"&&orderData?.assignStatus=="1"){
                            tvStatus.text = "Out for Picked"
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        else{
                            tvStatus.text = "Confirmed"
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }

                    }

                    "2" -> {
                        tvStatus.text = "Confirmed"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                    }
                    "2.1" -> {
                        tvStatus.text = "Shipped"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                    }
                    "2.2" -> {
                        tvStatus.text = "Out for Delivery"
                        tvStatus.setTextColor(Color.parseColor("#16B364"))
                    }

                    "3" -> {
                        if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "0" && orderData?.returnType == "0") {
                            tvStatus.text = "Delivered"
                            tvStatus.setTextColor(Color.parseColor("#047BD5"))
                        }
                        else if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "1" && orderData?.returnType == "2") {

                            if (orderData?.exchangeOrderDetails?.deliveryStatus=="7"){
                                tvStatus.text = "Out For Drop"
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                            else if (orderData?.exchangeOrderDetails?.deliveryStatus=="3"){
                                tvStatus.text = "Exchange Complete"
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                            else{
                                tvStatus.text = "Exchange Req Accepted"
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                        }
                        else if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "0" && orderData?.returnType == "2") {
                        tvStatus.text = "Exchange Req"
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    }

                        else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="0"&&orderData?.returnType=="1"){
                            tvStatus.text = "Return Req"
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                        else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="1"&&orderData?.returnType=="1"){
                            tvStatus.text = "Return Complete"
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                          else{
                            tvStatus.text = "Delivered"
                            tvStatus.setTextColor(Color.parseColor("#047BD5"))
                        }
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
                        when (orderData?.returnType) {
                            "1" -> {
                                tvStatus.text = "Picked Up"
                                tvStatus.setTextColor(Color.parseColor("#16B364"))

                            }

                            "2" -> {
                                tvStatus.text = "Picked Up"
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                            }

                            else -> {
                                tvStatus.text = "Out for Delivery"
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                            }
                        }

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
