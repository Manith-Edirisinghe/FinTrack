package com.example.fintrack

import android.app.DatePickerDialog
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class AddExpense : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val saveExpenseBtn = findViewById<Button>(R.id.saveExpenseBtn)

        val cancelBtn = findViewById<Button>(R.id.cancelBtn)

        val dateEditText = findViewById<EditText>(R.id.editTextDate)
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year) // Format date as YYYY-MM-DD
            dateEditText.setText(selectedDate) // Set the selected date into the EditText
        }

        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()


            // Create and show the DatePickerDialog
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }



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

        saveExpenseBtn.setOnClickListener {

            val title = findViewById<EditText>(R.id.editTextText).text.toString()
            val category = findViewById<Spinner>(R.id.spinner).selectedItem.toString()
            val amountText = findViewById<EditText>(R.id.editTextText3).text.toString()
            val date = dateEditText.text.toString()


            if (title.isNotEmpty() && category.isNotEmpty() && amountText.isNotEmpty() && date.isNotEmpty()) {
                val amount = amountText.toDoubleOrNull()
                if (amount != null) {
                    // Create the expense string
                    val expenseString = "$title-$category-$amount-$date"

                    saveExpense(expenseString)

                    Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_LONG).show()
                    // Inorder to play default notification sound
                    val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
                    ringtone.play()

                    // Navigate to MainActivity after saving
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    finish()
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }

        }
        cancelBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveExpense(expense: String) {
        val sharedPreferences = getSharedPreferences("Expenses", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the existing expenses from SharedPreferences if there wrere
        val currentExpenses = sharedPreferences.getString("expense_list", "")


        // Append the new expense to the existing expenses
        val updatedExpenses = if (currentExpenses.isNullOrEmpty()) {
            expense
        } else {
            "$currentExpenses,$expense"  // Append with a comma separator
        }

        // Save the updated list of expenses back to SharedPreferences
        editor.putString("expense_list", updatedExpenses)

        // Apply the changes asynchronously
        editor.apply()
    }


}