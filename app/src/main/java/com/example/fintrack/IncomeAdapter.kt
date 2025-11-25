package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IncomeAdapter(private val incomeList: List<Income>,
                    private val onDeleteClick: (Income) -> Unit
                    ,private val onUpdateClick: (Income) -> Unit
                        ) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        return IncomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val income = incomeList[position]
        holder.bind(income)
    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.incomeTitle)
        private val categoryTextView: TextView = itemView.findViewById(R.id.incomeCategory)
        private val amountTextView: TextView = itemView.findViewById(R.id.incomeAmount)
        private val dateTextView: TextView = itemView.findViewById(R.id.incomeDate)

        fun bind(income: Income) {
            titleTextView.text = itemView.context.getString(R.string.Title, income.title)
            categoryTextView.text = itemView.context.getString(R.string.Categoryy, income.category)
            amountTextView.text = itemView.context.getString(R.string.Amount, income.amount.toInt())
            dateTextView.text = itemView.context.getString(R.string.Date, income.date)

            val deleteBtn = itemView.findViewById<Button>(R.id.deletebtn)
            deleteBtn.setOnClickListener {
                onDeleteClick(income)
            }
            val updateBtn = itemView.findViewById<Button>(R.id.editbtn)
            updateBtn.setOnClickListener {
                onUpdateClick(income)
            }
        }
    }
}
