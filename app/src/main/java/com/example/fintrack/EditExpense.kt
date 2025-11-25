package com.example.fintrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditExpense : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_expense)

        titleEditText = findViewById(R.id.editTextText)
        categorySpinner = findViewById(R.id.spinner)
        amountEditText = findViewById(R.id.editTextText3)
        dateEditText = findViewById(R.id.editTextDate)
        saveChangesButton = findViewById(R.id.saveChangesBtn)
        backButton = findViewById(R.id.backBtn)

        // Receive the current expense data passed which was passed from the previous activity
        val title = intent.getStringExtra("title")
        val category = intent.getStringExtra("category")
        val amount = intent.getStringExtra("amount")
        val date = intent.getStringExtra("date")

        // Set the current data in the fields
        titleEditText.setText(title)
        amountEditText.setText(amount)
        dateEditText.setText(date)


        saveChangesButton.setOnClickListener {
            // Retrieve updated values
            val updatedTitle = titleEditText.text.toString()
            val updatedCategory = categorySpinner.selectedItem.toString()  // Assuming a valid selection
            val updatedAmount = amountEditText.text.toString()
            val updatedDate = dateEditText.text.toString()

            //use intent to send the data back to view all expenses activity
            val resultIntent = Intent()
            resultIntent.putExtra("updatedTitle", updatedTitle)
            resultIntent.putExtra("updatedCategory", updatedCategory)
            resultIntent.putExtra("updatedAmount", updatedAmount)
            resultIntent.putExtra("updatedDate", updatedDate)

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets




        }
    }
}