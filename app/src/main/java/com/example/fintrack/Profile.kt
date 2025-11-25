package com.example.fintrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.InputStream

class Profile : AppCompatActivity() {

    private lateinit var incomeList: MutableList<Income>
    private lateinit var expensesList: MutableList<Expenses>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)


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

        val importExpenseButton: Button = findViewById(R.id.importexpensebtn)
        importExpenseButton.setOnClickListener {
            openExpenseFilePicker()
        }

        val importIncomeButton: Button = findViewById(R.id.importincomebtn)
        importIncomeButton.setOnClickListener {
            openIncomeFilePicker()
        }


        incomeList = loadIncomes().toMutableList()

        expensesList = loadExpense().toMutableList()

        calculateIncomeTotals()
        calculateExpenseTotals()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }


    private fun openExpenseFilePicker() {
        // Open file picker
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain" // as import file is a text file
        startActivityForResult(intent, REQUEST_CODE_IMPORT_Expense)
    }

    private fun handleExpenseFile(uri: Uri) {
        // Read the content of the file selected
        try {
            val inputStream: InputStream = contentResolver.openInputStream(uri)!!
            val expenseData = inputStream.bufferedReader().use { it.readText() }

            // Save data to SharedPreferences
            val sharedPreferences = getSharedPreferences("Expenses", MODE_PRIVATE)
            sharedPreferences.edit().putString("expense_list", expenseData).apply()

            expensesList = loadExpense().toMutableList()
            calculateExpenseTotals()
            Toast.makeText(this, "Data imported successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to import data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Handle result from the file picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                when (requestCode) {
                    REQUEST_CODE_IMPORT_Expense ->  handleExpenseFile(uri)
                    REQUEST_CODE_IMPORT_INCOME -> handleIncomeFile(uri)
                }
            }
        }
    }
    companion object {
        const val REQUEST_CODE_IMPORT_Expense = 1001
        const val REQUEST_CODE_IMPORT_INCOME = 1002
    }

    private fun openIncomeFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain"
        startActivityForResult(intent, REQUEST_CODE_IMPORT_INCOME)
    }

    private fun handleIncomeFile(uri: Uri) {
        try {
            val inputStream: InputStream = contentResolver.openInputStream(uri)!!
            val incomeData = inputStream.bufferedReader().use { it.readText() }

            val sharedPreferences = getSharedPreferences("Income", MODE_PRIVATE)
            sharedPreferences.edit().putString("income_list", incomeData).apply()

            incomeList=loadIncomes().toMutableList()
            calculateIncomeTotals()

            Toast.makeText(this, "Income data imported successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to import income data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



    private fun loadIncomes(): List<Income> {
        val sharedPreferences = getSharedPreferences("Income", MODE_PRIVATE)
        val incomeString = sharedPreferences.getString("income_list", "")

        val incomes = mutableListOf<Income>()

        if (!incomeString.isNullOrEmpty()) {
            val incomeArray = incomeString.split(",")
            for (incomeData in incomeArray) {
                val incomeParts = incomeData.split("-")
                if (incomeParts.size == 4) {
                    val title = incomeParts[0]
                    val category = incomeParts[1]
                    val amount = incomeParts[2].toDoubleOrNull() ?: 0.0
                    val date = incomeParts[3]

                    val income = Income(title, category, amount, date)
                    incomes.add(income)
                }
            }
        }
        return incomes
    }

    //to calculate the totals and display
    private fun calculateIncomeTotals() {
        var totalIncome = 0.0
        var totalBonus = 0.0
        var totalSalary = 0.0
        var totalGift = 0.0
        var totalBuisnessIncome = 0.0
        var totalIncomeOther= 0.0

        for (income in incomeList) {
            totalIncome += income.amount

            when (income.category) {
                "Bonus" -> totalBonus += income.amount
                "Salary" -> totalSalary += income.amount
                "Gift" -> totalGift += income.amount
                "Buisness Income" -> totalBuisnessIncome += income.amount
                "Other" -> totalIncomeOther += income.amount

            }
        }

        // Inorder to set the text to the text views

        findViewById<TextView>(R.id.textView35).text = getString(R.string.TotalIncome, totalIncome)
        findViewById<TextView>(R.id.textView36).text = getString(R.string.TotalBonus, totalBonus)
        findViewById<TextView>(R.id.textView37).text = getString(R.string.TotalSalary, totalSalary)
        findViewById<TextView>(R.id.textView38).text = getString(R.string.TotalGift, totalGift)
        findViewById<TextView>(R.id.textView39).text = getString(R.string.TotalBuisnessIncome, totalBuisnessIncome)
        findViewById<TextView>(R.id.textView40).text = getString(R.string.TotalIncomeOther, totalIncomeOther)
    }






    private fun loadExpense(): List<Expenses> {
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
    private fun calculateExpenseTotals() {
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

}