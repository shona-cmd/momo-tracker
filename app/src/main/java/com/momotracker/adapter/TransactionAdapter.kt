// app/src/main/java/com/momotracker/adapter/TransactionAdapter.kt

package com.momotracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.momotracker.R
import com.momotracker.data.Transaction
import com.google.android.material.color.MaterialColors

class TransactionAdapter :
    ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvType: TextView = itemView.findViewById(R.id.tv_type)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        private val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvTxnId: TextView = itemView.findViewById(R.id.tv_txn_id)
        private val tvBalance: TextView = itemView.findViewById(R.id.tv_balance)

        fun bind(transaction: Transaction) {
            val context = itemView.context

            // Type & Icon
            when (transaction.type) {
                "Received" -> {
                    tvType.text = "Received"
                    tvType.setTextColor(MaterialColors.getColor(itemView, com.google.android.material.R.attr.colorPrimary))
                    tvAmount.setTextColor(MaterialColors.getColor(itemView, com.google.android.material.R.attr.colorPrimary))
                    tvAmount.text = "+ ${transaction.formattedAmount()}"
                }
                "Sent" -> {
                    tvType.text = "Sent"
                    tvAmount.setTextColor(MaterialColors.getColor(itemView, android.R.attr.textColorPrimary))
                    tvAmount.text = "- ${transaction.formattedAmount()}"
                }
                "Fee" -> {
                    tvType.text = "Fee/Charges"
                    tvAmount.setTextColor(MaterialColors.getColor(itemView, android.R.attr.textColorSecondary))
                    tvAmount.text = "- ${transaction.formattedAmount()}"
                }
                else -> {
                    tvType.text = transaction.type
                    tvAmount.text = transaction.formattedAmount()
                }
            }

            // Phone & Name
            val namePart = transaction.name?.let { "$it • " } ?: ""
            tvPhone.text = "$namePart${transaction.phone.ifEmpty { "Unknown" }}"

            // Date
            tvDate.text = transaction.formattedDate()

            // Transaction ID
            if (transaction.transactionId.isNotBlank()) {
                tvTxnId.visibility = View.VISIBLE
                tvTxnId.text = "ID: ${transaction.transactionId}"
            } else {
                tvTxnId.visibility = View.GONE
            }

            // Balance after transaction
            transaction.balanceAfter?.let {
                tvBalance.visibility = View.VISIBLE
                tvBalance.text = "Balance: UGX ${String.format("%,.0f", it)}"
            } ?: run {
                tvBalance.visibility = View.GONE
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
