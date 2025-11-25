package com.example.fintrack

import android.app.DatePickerDialog
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.Calendar

class ViewAllIncome : AppCompatActivity() {

    private lateinit var incomeList: MutableList<Income> // Define this list to hold incomes
    private lateinit var adapter: IncomeAdapter

    //to display totals

    private lateinit var totalIncomeTextView: TextView
    private lateinit var totalBonusTextView: TextView
    private lateinit var totalSalaryTextView: TextView
    private lateinit var totalGiftTextView: TextView
    private lateinit var totalBuisnessIncomeTextView: TextView
    private lateinit var totalIncomeOtherTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_all_income)

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



        totalIncomeTextView = findViewById(R.id.textView27)
        totalBonusTextView = findViewById(R.id.textView28)
        totalSalaryTextView = findViewById(R.id.textView29)
        totalGiftTextView = findViewById(R.id.textView30)
        totalBuisnessIncomeTextView = findViewById(R.id.textView31)
        totalIncomeOtherTextView = findViewById(R.id.textView32)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        incomeList = loadIncomes().toMutableList()

        val exportBtn = findViewById<Button>(R.id.downloadbtn)
        exportBtn.setOnClickListener {
            exportIncome()
        }

        adapter = IncomeAdapter(incomeList, { income -> deleteIncome(income) }, { income -> showUpdateDialog(income) })
        recyclerView.adapter = adapter

        calculateTotals()


    }

    private fun deleteIncome(income:Income) {
        incomeList.remove(income)
        adapter.notifyDataSetChanged()

        // Update SharedPreferences after deleting
        saveIncome()

        Toast.makeText(this, "Income record Deleted successfully", Toast.LENGTH_LONG).show()

        // Inorder to play default notification sound
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, notification)
        ringtone.play()

        calculateTotals()

    }

    private fun saveIncome() {
        val sharedPreferences = getSharedPreferences("Income", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val incomeString = incomeList.joinToString(",") { "${it.title}-${it.category}-${it.amount}-${it.date}" }
        editor.putString("income_list", incomeString)
        editor.apply()
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
    private fun calculateTotals() {
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

        findViewById<TextView>(R.id.textView27).text = getString(R.string.TotalIncome, totalIncome)
        findViewById<TextView>(R.id.textView28).text = getString(R.string.TotalBonus, totalBonus)
        findViewById<TextView>(R.id.textView29).text = getString(R.string.TotalSalary, totalSalary)
        findViewById<TextView>(R.id.textView30).text = getString(R.string.TotalGift, totalGift)
        findViewById<TextView>(R.id.textView31).text = getString(R.string.TotalBuisnessIncome, totalBuisnessIncome)
        findViewById<TextView>(R.id.textView32).text = getString(R.string.TotalIncomeOther, totalIncomeOther)
    }

    private fun exportIncome() {

        val sharedPreferences = getSharedPreferences("Income", MODE_PRIVATE)
        val incomeData = sharedPreferences.getString("income_list", "")

        if (incomeData.isNullOrEmpty()) {
            Toast.makeText(this, "No data to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "Incomes.txt"

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        try {
            file.writeText(incomeData)
            Toast.makeText(this, "Saved to Downloads/$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save file: ${e.message}", Toast.LENGTH_LONG).show()
        }

    }


    private fun showUpdateDialog(income: Income) {
        val dialogView = layoutInflater.inflate(R.layout.update_income_dialog, null)

        val titleInput = dialogView.findViewById<EditText>(R.id.editTitle)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.editCategorySpinner)


        val categories = listOf("Bonus", "Salary", "Gift", "Buisness Income", "Other")

        val amountInput = dialogView.findViewById<EditText>(R.id.editAmount)
        val dateInput = dialogView.findViewById<EditText>(R.id.editDate)

        // Set up the adapter for the spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Set spinner to currently selected category
        val currentCategoryIndex = categories.indexOf(income.category)
        if (currentCategoryIndex != -1) {
            categorySpinner.setSelection(currentCategoryIndex)
        }


        // Pre-fill current values
        titleInput.setText(income.title)

        amountInput.setText(income.amount.toString())
        dateInput.setText(income.date)

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
            .setTitle("Update Income ")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                income.title = titleInput.text.toString()
                income.category = categorySpinner.selectedItem.toString()
                income.amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
                income.date = dateInput.text.toString()

                adapter.notifyDataSetChanged()
                saveIncome()
                calculateTotals()

                Toast.makeText(this, "Income updated successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}