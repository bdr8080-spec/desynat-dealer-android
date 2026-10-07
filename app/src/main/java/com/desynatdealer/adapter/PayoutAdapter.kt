package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemPayoutBinding
import com.desynatdealer.ui.home.paymentManagement.response.GetPayoutResponse
import java.text.SimpleDateFormat
import java.util.Locale

class PayoutAdapter(
    var context: Context,
    var payoutList: ArrayList<GetPayoutResponse.Data.Payouts.PayoutData>
) : RecyclerView.Adapter<PayoutAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val payoutData = payoutList[position]

            tvFromDate.text = formatDate(payoutData.fromDate ?:"")
            tvToDate.text = formatDate(payoutData.toDate)
            tvTransactionId.text = "#${payoutData.transactionId?.takeLast(5) }"?: ""
            tvPayoutReceived.isVisible = true
            tvPayoutReceivedAt.isVisible = true
            tvPayoutReceivedAt.text = formatDateTime(payoutData.payoutDate ?: payoutData.paymentReceiveDate ?: payoutData.date)

            tvTotal.isVisible = true
            tvTotalAmount.isVisible = true
            tvTotalAmount.text = payoutData.amount ?: "0"

            val commission = payoutData.commission
            val isCommissionValid = !commission.isNullOrEmpty() && 
                commission != "0" && 
                commission != "0.0" && 
                commission != "0.00" && 
                commission != "0.000" &&
                commission.toDoubleOrNull()?.let { it > 0.0 } == true

            tvTotalCommission.isVisible = isCommissionValid
            tvTotalCommissionValue.isVisible = isCommissionValid
            if (isCommissionValid) {
                tvTotalCommissionValue.text = commission
            }
        }
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return ""
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            if (date != null) return outputFormat.format(date)
        } catch (_: Exception) {
            try {
                val inputFormat2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat2 = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date2 = inputFormat2.parse(dateStr)
                if (date2 != null) return outputFormat2.format(date2)
            } catch (_: Exception) {}
        }
        return dateStr
    }

    private fun formatDateTime(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return ""
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            if (date != null) return outputFormat.format(date)
        } catch (_: Exception) {
            try {
                val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat2 = SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a", Locale.getDefault())
                val date2 = inputFormat2.parse(dateStr.substringBefore('.'))
                if (date2 != null) return outputFormat2.format(date2)
            } catch (_: Exception) {}
        }
        return dateStr
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return payoutList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
