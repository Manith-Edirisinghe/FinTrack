package com.example.fintrack

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.Calendar

class ViewAllExpense : AppCompatActivity() {

    private lateinit var expensesList: MutableList<Expenses> // Define this list to hold expenses
    private lateinit var adapter: ExpenseAdapter


    //to display totals

    private lateinit var totalExpenseTextView: TextView
    private lateinit var totalFoodTextView: TextView
    private lateinit var totalElectricityTextView: TextView
    private lateinit var totalRentTextView: TextView
    private lateinit var totalHealthcareTextView: TextView
    private lateinit var totalGroceriesTextView: TextView
    private lateinit var totalShoppingTextView: TextView
    private lateinit var totalOtherTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_all_expense)

        totalExpenseTextView = findViewById(R.id.textView27)
        totalFoodTextView = findViewById(R.id.textView28)
        totalElectricityTextView = findViewById(R.id.textView29)
        totalRentTextView = findViewById(R.id.textView30)
        totalHealthcareTextView = findViewById(R.id.textView31)
        totalGroceriesTextView = findViewById(R.id.textView32)
        totalShoppingTextView = findViewById(R.id.textView33)
        totalOtherTextView = findViewById(R.id.textView34)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        expensesList = loadExpenses().toMutableList()

        val exportBtn = findViewById<Button>(R.id.downloadbtn)
        exportBtn.setOnClickListener {
            exportExpenses()
        }

        adapter = ExpenseAdapter(expensesList, { expense -> deleteExpense(expense) }, { expense -> showUpdateDialog(expense) })
        recyclerView.adapter = adapter

        calculateTotals()


    }

    private fun deleteExpense(expense: Expenses) {
        expensesList.remove(expense)
        adapter.notifyDataSetChanged()

        // Update SharedPreferences after deleting
        saveExpenses()

        Toast.makeText(this, "Expense Deleted successfully", Toast.LENGTH_LONG).show()

        // Inorder to play default notification sound
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
        ringtone.play()

        calculateTotals()

    }


    private fun saveExpenses() {
        val sharedPreferences = getSharedPreferences("Expenses", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val expenseString = expensesList.joinToString(",") { "${it.title}-${it.category}-${it.amount}-${it.date}" }
        editor.putString("expense_list", expenseString)
        editor.apply()
    }



    private fun loadExpenses(): List<Expenses> {
        val sharedPreferences = getSharedPreferences("Expenses", MODE_PRIVATE)
        val expenseString = sharedPreferences.getString("expense_list", "")

        val expenses = mutableListOf<Expenses>()

        if (!expenseString.isNullOrEmpty()) {
            val expenseArray = expenseString.split(",")
            for (expenseData in expenseArray) {
                val expenseParts = expenseData.split("-")
                if (expenseParts.size == 4) {
                    val title = expenseParts[0]
                    val category = expenseParts[1]
                    val amount = expenseParts[2].toDoubleOrNull() ?: 0.0
                    val date = expenseParts[3]

                    val expense = Expenses(title, category, amount, date)
                    expenses.add(expense)
                }
            }
        }
        return expenses
    }




    //to calculate the totals and display
    private fun calculateTotals() {
        var totalExpense = 0.0
        var totalFood = 0.0
        var totalElectricity = 0.0
        var totalRent = 0.0
        var totalHealthcare = 0.0
        var totalGroceries = 0.0
        var totalShopping = 0.0
        var totalOther = 0.0

        for (expense in expensesList) {
            totalExpense += expense.amount

            when (expense.category) {
                "Food" -> totalFood += expense.amount
                "Electricity" -> totalElectricity += expense.amount
                "Rent" -> totalRent += expense.amount
                "Healthcare" -> totalHealthcare += expense.amount
                "Groceries" -> totalGroceries += expense.amount
                "Shopping" -> totalShopping += expense.amount
                "Other" -> totalOther += expense.amount
            }
        }

        // Inorder to set the text to the text views

        findViewById<TextView>(R.id.textView27).text = getString(R.string.TotalExpenses, totalExpense)
        findViewById<TextView>(R.id.textView28).text = getString(R.string.TotalFood, totalFood)
        findViewById<TextView>(R.id.textView29).text = getString(R.string.TotalElectricity, totalElectricity)
        findViewById<TextView>(R.id.textView30).text = getString(R.string.TotalRent, totalRent)
        findViewById<TextView>(R.id.textView31).text = getString(R.string.TotalHealthcare, totalHealthcare)
        findViewById<TextView>(R.id.textView32).text = getString(R.string.TotalGroceries, totalGroceries)
        findViewById<TextView>(R.id.textView33).text = getString(R.string.TotalShopping, totalShopping)
        findViewById<TextView>(R.id.textView34).text = getString(R.string.TotalOther, totalOther)
    }




    private fun exportExpenses() {

        val sharedPreferences = getSharedPreferences("Expenses", MODE_PRIVATE)
        val expenseData = sharedPreferences.getString("expense_list", "")

        if (expenseData.isNullOrEmpty()) {
            Toast.makeText(this, "No data to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "Expenses.txt"

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            try {
                file.writeText(expenseData)
                Toast.makeText(this, "Saved to Downloads/$fileName", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to save file: ${e.message}", Toast.LENGTH_LONG).show()
            }

    }




    private fun showUpdateDialog(expense: Expenses) {
        val dialogView = layoutInflater.inflate(R.layout.update_expense_dialog, null)

        val titleInput = dialogView.findViewById<EditText>(R.id.editTitle)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.editCategorySpinner)
        val categories = listOf("Food", "Electricity", "Rent", "Healthcare", "Groceries", "Shopping", "Other")

        val amountInput = dialogView.findViewById<EditText>(R.id.editAmount)
        val dateInput = dialogView.findViewById<EditText>(R.id.editDate)

        // Set up the adapter for the spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Set spinner to currently selected category
        val currentCategoryIndex = categories.indexOf(expense.category)
        if (currentCategoryIndex != -1) {
            categorySpinner.setSelection(currentCategoryIndex)
        }


        // Pre-fill current values
        titleInput.setText(expense.title)

        amountInput.setText(expense.amount.toString())
        dateInput.setText(expense.date)

        // Set up DatePickerDialog
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Format the selected date and set it to the EditText
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                dateInput.setText(selectedDate)
            }

            // Create and show the DatePickerDialog
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }



        AlertDialog.Builder(this)
            .setTitle("Update Expense")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                expense.title = titleInput.text.toString()
                expense.category = categorySpinner.selectedItem.toString()
                expense.amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
                expense.date = dateInput.text.toString()

                adapter.notifyDataSetChanged()
                saveExpenses()
                calculateTotals()

                Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }




}