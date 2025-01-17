package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        EditText editTextDescription = findViewById(R.id.editTextDescription);
        EditText editTextAmount = findViewById(R.id.editTextAmount);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Handle the Save button click
        buttonSave.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();

            if (!description.isEmpty() && !amount.isEmpty()) {
                String expense = description + " - Rs - " + amount;

                // Return the expense to MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("expense", expense);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}