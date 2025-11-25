package com.example.fintrack

import android.app.AlertDialog
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //bottom nav bar intent
        val homeIcon = findViewById<ImageView>(R.id.homeicon)
        val incomeIcon = findViewById<ImageView>(R.id.incomeicon)
        val expenseIcon = findViewById<ImageView>(R.id.expenseicon)
        val profileIcon = findViewById<ImageView>(R.id.profileicon)

        homeIcon.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        incomeIcon.setOnClickListener {
            startActivity(Intent(this, ViewAllIncome::class.java))
        }

        expenseIcon.setOnClickListener {
            startActivity(Intent(this, ViewAllExpense::class.java))
        }

        profileIcon.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
        ///////


        val setBudgetButton = findViewById<Button>(R.id.setBudgetbtn);

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        val savedBudget = sharedPreferences.getString("budget", "0.00")?.toDoubleOrNull() ?: 0.0


        val currentBudgetTextView = findViewById<TextView>(R.id.currentBudgetTextView)

        currentBudgetTextView.text = getString(R.string.SetBudget, savedBudget?.toDouble() ?: 0.00)


        val totalExpenses = getTotalExpenses()

        checkBudgetUsage(savedBudget, totalExpenses)

        val ExpenseTotalTextView = findViewById<TextView>(R.id.expenseTotal)
        ExpenseTotalTextView.text = getString(R.string.TotalExpenses, totalExpenses?.toDouble() ?: 0.00)


        val totalIncome = getTotalIncome()
        val IncomeTotalTextView = findViewById<TextView>(R.id.incomeTotal)
        IncomeTotalTextView.text = getString(R.string.TotalIncome, totalIncome?.toDouble() ?: 0.00)


        val remainingBudget=savedBudget-totalExpenses
        val RemainingBudgetTextView = findViewById<TextView>(R.id.remainingBudget)
        RemainingBudgetTextView.text = getString(R.string.RemainingBudget, remainingBudget?.toDouble() ?: 0.00)


        if (remainingBudget < 0) {
            AlertDialog.Builder(this)
                .setTitle("Budget Alert ⚠️")
                .setMessage("Warning! Your expenses have exceeded your budget.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
            ringtone.play()
        }


        setBudgetButton.setOnClickListener {
            //to inflate dialog
            val dialogView = layoutInflater.inflate(R.layout.dialog_set_budget, null)

            val editText = dialogView.findViewById<EditText>(R.id.editbudget)

            val saveButton = dialogView.findViewById<Button>(R.id.budgetbtn)

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
                .setTitle("Set Budget")
                .setCancelable(true)
            // Show the dialog
            val dialog = builder.create()

            saveButton.setOnClickListener {
                val budget = editText.text.toString()
                if (budget.isNotEmpty()) {
                    // Save the budget value in shared preferences
                    val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    // Save the budget to SharedPreferences
                    editor.putString("budget", budget)
                    editor.apply()

                    val newBudget = budget.toDouble()
                    currentBudgetTextView.text = getString(R.string.SetBudget, newBudget)

                    //recalculate remaining budget

                    val newRemainingBudget = newBudget - getTotalExpenses()

                    checkBudgetUsage(savedBudget, totalExpenses)

                    RemainingBudgetTextView.text = getString(R.string.RemainingBudget, newRemainingBudget)

                    checkBudgetUsage(savedBudget, totalExpenses)
                    //to check if new remaining budget is negatice

                    if (newRemainingBudget < 0) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Budget Alert ⚠️")
                            .setMessage("Warning! Your expenses have exceeded your budget.")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()

                       //play sound when budget exceeds
                        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
                        ringtone.play()
                    }


                    Toast.makeText(this, "Budget amount saved: $budget", Toast.LENGTH_LONG).show()
                    // Inorder to play default notification sound
                    val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
                    ringtone.play()



                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please enter a valid budget!", Toast.LENGTH_LONG).show()
                }
            }

            dialog.show()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val incomeBtn = findViewById<Button>(R.id.incomebtn)
        val expenseBtn = findViewById<Button>(R.id.expensebtn)

        incomeBtn.setOnClickListener {
            val intent = Intent(this, AddIncome::class.java)
            startActivity(intent)
        }
        expenseBtn.setOnClickListener {
            val intent = Intent(this, AddExpense::class.java)
            startActivity(intent)
        }
    }

    private fun getTotalExpenses(): Double {
        val sharedPreferences = getSharedPreferences("Expenses", MODE_PRIVATE)
        val currentExpenses = sharedPreferences.getString("expense_list", "")
        var total = 0.0

        if (!currentExpenses.isNullOrEmpty()) {
            val expenses = currentExpenses.split(",")
            for (expense in expenses) {
                val parts = expense.split("-")
                if (parts.size == 4) {
                    val amount = parts[2].toDoubleOrNull() ?: 0.0
                    total += amount
                }
            }
        }

        return total
    }

    private fun getTotalIncome(): Double {
        val sharedPreferences = getSharedPreferences("Income", MODE_PRIVATE)
        val currentIncome = sharedPreferences.getString("income_list", "")
        var total = 0.0

        if (!currentIncome.isNullOrEmpty()) {
            val incomes = currentIncome.split(",")
            for (income in incomes) {
                val parts = income.split("-")
                if (parts.size == 4) {
                    val amount = parts[2].toDoubleOrNull() ?: 0.0
                    total += amount
                }
            }
        }

        return total
    }


    private fun checkBudgetUsage(savedBudget: Double, totalExpenses: Double) {
        val usedBudgetPercentage = (totalExpenses / savedBudget) * 100

        if (usedBudgetPercentage >= 80 && usedBudgetPercentage < 100) {
            AlertDialog.Builder(this)
                .setTitle("Budget Warning ⚠️")
                .setMessage("Warning! You've used ${usedBudgetPercentage.toInt()}% of your budget.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()

            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
            ringtone.play()
        }
    }
}
