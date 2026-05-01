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
import com.desynatdealer.utils.setImage

class OrderAdapter(
    var context: Context,
    var orderList: ArrayList<GetAllOrderResponse.Data.AllOrder>,
    var orderClick: OrderClick
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    interface OrderClick {
        fun onOrderClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val orderData = orderList[position]

//                ivUser.load(orderData?.userDetails?.profileImage) {
//                    crossfade(true)
//                    placeholder(R.drawable.ic_user)
//                    error(R.drawable.ic_user)
//                }
            ivUser.setImage(orderData?.userDetails?.profileImage,R.drawable.ic_user)
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
                    tvStatus.text = context.getString(R.string.cancelled)
                    tvStatus.setTextColor(Color.parseColor("#E70000"))
                }
                else {
                    when (orderData?.deliveryStatus) {
                        "1" -> {
                            if (orderData?.rEApprove=="1"&&orderData?.returnType=="1"&&orderData?.assignStatus=="0"){
                                tvStatus.text = context.getString(R.string.return_req_accepted)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                            }
                            else if (orderData?.rEApprove=="1"&&orderData?.returnType=="1"&&orderData?.assignStatus=="1"){
                                tvStatus.text = context.getString(R.string.out_for_picked)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                            }
                            else{
                                tvStatus.text = context.getString(R.string.confirmed)
                                tvStatus.setTextColor(Color.parseColor("#16B364"))
                            }

                        }

                        "2" -> {
                            tvStatus.text = context.getString(R.string.confirmed)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        "2.1" -> {
                            tvStatus.text = context.getString(R.string.shipped)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }
                        "2.2" -> {
                            tvStatus.text = context.getString(R.string.out_for_delivery)
                            tvStatus.setTextColor(Color.parseColor("#16B364"))
                        }

                        "3" -> {
                            if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "0" && orderData?.returnType == "0") {
                                tvStatus.text = context.getString(R.string.delivered)
                                tvStatus.setTextColor(Color.parseColor("#047BD5"))
                            }
                            else if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "1" && orderData?.returnType == "2") {

                                if (orderData?.exchangeOrderDetails?.deliveryStatus=="7"){
                                    tvStatus.text = context.getString(R.string.out_for_drop)
                                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                                }
                                else if (orderData?.exchangeOrderDetails?.deliveryStatus=="3"){
                                    tvStatus.text = context.getString(R.string.exchange_complete)
                                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                                }
                                else{
                                    tvStatus.text =
                                        context.getString(R.string.exchange_req_accepted)
                                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                                }
                            }
                            else if (orderData?.r_e_type == "0" && orderData?.p_orderdetails_id == "0" && orderData?.rEApprove == "0" && orderData?.returnType == "2") {
                                tvStatus.text = context.getString(R.string.exchange_req)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }

                            else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="0"&&orderData?.returnType=="1"){
                                tvStatus.text = context.getString(R.string.return_req)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                            else if (orderData?.r_e_type=="0"&&orderData?.p_orderdetails_id=="0"&&orderData?.rEApprove=="1"&&orderData?.returnType=="1"){
                                tvStatus.text = context.getString(R.string.return_complete)
                                tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                            }
                            else{
                                tvStatus.text = context.getString(R.string.delivered)
                                tvStatus.setTextColor(Color.parseColor("#047BD5"))
                            }
                        }

                        "4" -> {
                            tvStatus.text = context.getString(R.string.cancelled)
                            tvStatus.setTextColor(Color.parseColor("#E70000"))
                        }

                        "5" -> {
                            tvStatus.text = context.getString(R.string.return_req)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }

                        "7" -> {
                            when (orderData?.returnType) {
                                "1" -> {
                                    tvStatus.text = context.getString(R.string.picked_up)
                                    tvStatus.setTextColor(Color.parseColor("#16B364"))

                                }

                                "2" -> {
                                    tvStatus.text = context.getString(R.string.picked_up)
                                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                                }

                                else -> {
                                    tvStatus.text = context.getString(R.string.out_for_delivery)
                                    tvStatus.setTextColor(Color.parseColor("#16B364"))
                                }
                            }

                        }

                        "8" -> {
                            tvStatus.text = context.getString(R.string.not_delivered)
                            tvStatus.setTextColor(Color.parseColor("#E70000"))
                        }

                        "9" -> {
                            tvStatus.text = context.getString(R.string.exchange_req)
                            tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        }
                    }
                }

            root.setOnClickListener {
                orderClick.onOrderClick(position)
            }
        }
        }

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
        holder.bind(position)
    }

}
