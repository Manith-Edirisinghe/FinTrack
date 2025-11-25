package com.example.fintrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter (private val expenseList: List<Expenses>,
                      private val onDeleteClick: (Expenses) -> Unit,
                      private val onUpdateClick: (Expenses) -> Unit
                        ) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.bind(expense)
    }
    override fun getItemCount(): Int {
        return expenseList.size
    }
    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.expenseTitle)
        private val categoryTextView: TextView = itemView.findViewById(R.id.expenseCategory)
        private val amountTextView: TextView = itemView.findViewById(R.id.expenseAmount)
        private val dateTextView: TextView = itemView.findViewById(R.id.expenseDate)

        fun bind(expense: Expenses) {
            titleTextView.text = itemView.context.getString(R.string.Title, expense.title)
            categoryTextView.text = itemView.context.getString(R.string.Categoryy, expense.category)
            // Ensure amount is an integer or handle its format if it's a double or string
            amountTextView.text = itemView.context.getString(R.string.Amount, expense.amount.toInt())
            dateTextView.text = itemView.context.getString(R.string.Date, expense.date)


            val deleteBtn = itemView.findViewById<Button>(R.id.deletebtn)
            deleteBtn.setOnClickListener {
                onDeleteClick(expense)
            }
            val updateBtn = itemView.findViewById<Button>(R.id.editbtn)
            updateBtn.setOnClickListener {
                onUpdateClick(expense)
            }

        }
    }

}