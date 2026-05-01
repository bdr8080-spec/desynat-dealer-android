package com.desynatdealer.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.R
import com.desynatdealer.databinding.ItemRefundBinding
import com.desynatdealer.ui.home.refundManagement.response.GetRefundOrderResponse
import com.desynatdealer.utils.CommonMethod

class RefundAdapter(var context:Context, var refundList: ArrayList<GetRefundOrderResponse.Data.Orders.Data>, var onClick:OnClick) : RecyclerView.Adapter<RefundAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRefundBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var refundData = refundList[position]
            tvOrderDateTime.text = CommonMethod.customizeChangeDateFormatKuwaitFromUtc(refundData?.order?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvRequestedOn.text = CommonMethod.customizeChangeDateFormatKuwaitFromUtc(refundData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy")
            tvCustomerName.text = refundData?.order?.deliveryDetails?.name
            tvRefundId.text = "#${refundData?.order?.transactionId}"
            tvTotalAmount.text = "${CommonMethod.formatPrice(refundData?.order?.totalAmount?.toDoubleOrNull()?:0.0)} KD"
            tvTotalCommissionValue.text = "${CommonMethod.formatPrice(refundData?.productDetails?.affiliatedCommission?.toDoubleOrNull()?:0.0)} KD"
            tvPaymentMode.text = when(refundData?.order?.paymentMethod){
                "1"->context.getString(R.string.card)
                "2"-> context.getString(R.string.wallet)
                "3"-> context.getString(R.string.cod)
                else -> context.getString(R.string.card)
            }
            if (refundData?.returnReason!=null){
                viewRefund.isVisible = true
                tvReturnReason.isVisible = true
                tvReturn.isVisible = true
                tvReturnReason.text = refundData?.returnReason.toString()
            }
            else if (refundData?.exchangeReason!=null){
                viewRefund.isVisible = true
                tvReturnReason.isVisible = true
                tvReturn.isVisible = true
                tvReturnReason.text = refundData?.exchangeReason.toString()
            }
            else if (refundData?.cancelReason!=null){
                viewRefund.isVisible = true
                tvReturnReason.isVisible = true
                tvReturn.isVisible = true
                tvReturnReason.text = refundData?.cancelReason.toString()
            }
            else{
                viewRefund.isVisible = false
                tvReturn.isVisible = false
                tvReturnReason.isVisible = false
            }

            when(refundData?.status){
                "2"-> {
                    tvStatus.text = context.getString(R.string.completed)
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))

                }
                "1"-> {
                    tvStatus.text = context.getString(R.string.pending)
                    tvStatus.setTextColor(Color.parseColor("#FFA41B"))
                }
                else -> {
                    tvStatus.text = context.getString(R.string.pending)
                    tvStatus.setTextColor(Color.parseColor("#FFA41B"))
                }
            }
        }
        }

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRefundBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return refundList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
