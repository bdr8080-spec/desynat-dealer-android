package com.desynatdealer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatdealer.databinding.ItemTransactionBinding
import com.desynatdealer.ui.home.paymentManagement.response.GetAllTransactionResponse
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    var context: Context,
    var transactionList: ArrayList<GetAllTransactionResponse.Data.Transactions.TransactionData>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val transactionData = transactionList[position]
            tvTransactionId.text = "#${transactionData.transactionId?.takeLast(6) ?: "N/A"}"

            val dateStr = transactionData.transactionDate ?: ""
            if (dateStr.isNotEmpty()) {
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a", Locale.getDefault())
                    val parsedDate = inputFormat.parse(dateStr)
                    tvDateTime.text = if (parsedDate != null) outputFormat.format(parsedDate) else dateStr
                } catch (_: Exception) {
                    tvDateTime.text = dateStr
                }
            } else {
                tvDateTime.text = ""
            }

            tvCustomerName.text = transactionData.customerName ?: "N/A"
            tvOrderId.text = transactionData.orderId?.let { "#$it" } ?: "N/A"
            tvTotalAmount.text = transactionData.amount ?: "0"

            val commission = transactionData.commission
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

            tvPaymentMode.text = transactionData.transactionLabel ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
