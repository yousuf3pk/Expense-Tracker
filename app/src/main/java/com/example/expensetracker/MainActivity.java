package com.example.expensetracker;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> expensesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private double totalAmount = 0.0;
    private TextView textViewTotal;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ExpenseTrackerPrefs";
    private static final String EXPENSES_KEY = "expensesList";
    private static final String TOTAL_KEY = "totalAmount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewExpenses = findViewById(R.id.listViewExpenses);
        Button buttonAddExpense = findViewById(R.id.buttonAddExpense);
        Button buttonClearTotal = findViewById(R.id.buttonClearTotal);
        textViewTotal = findViewById(R.id.textViewTotal);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load saved data
        loadExpenses();

        // Set up the adapter for the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expensesList);
        listViewExpenses.setAdapter(adapter);

        // Handle Add Expense button click
        buttonAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1);
        });

        // Change 1
        buttonClearTotal.setOnClickListener(v -> {
            totalAmount = 0.00;

            expensesList.clear();

            // Notify the adapter and update the total amount TextView
            adapter.notifyDataSetChanged();
            updateTotalText();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(expensesList);
            editor.putString(EXPENSES_KEY, json);
            editor.putFloat(TOTAL_KEY, (float) totalAmount);
            editor.apply();

            updateTotalText();
        });

        // Long press to delete an expense
        listViewExpenses.setOnItemLongClickListener((parent, view, position, id) -> {
            // Get the amount from the selected expense and subtract it from the total
            String expense = expensesList.get(position);
            double amount = extractAmountFromExpense(expense);
            totalAmount -= amount;

            // Remove the expense, update the total, and save the updated list
            expensesList.remove(position);
            adapter.notifyDataSetChanged();
            updateTotalText();
            saveExpenses();
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get the new expense and amount
            String expense = data.getStringExtra("expense");
            double amount = extractAmountFromExpense(expense);

            // Add the expense, update the total, and save the updated list
            expensesList.add(expense);
            totalAmount += amount;
            adapter.notifyDataSetChanged();
            updateTotalText();
            saveExpenses();
        }
    }

    // Helper method to extract the amount from the expense string
    private double extractAmountFromExpense(String expense) {
        try {
            String[] parts = expense.split("Rs - ");
            return Double.parseDouble(parts[1]);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Update the total amount TextView
    private void updateTotalText() {
        textViewTotal.setText(String.format("Your Total Expense: Rs/ %.2f", totalAmount));
    }

    // Save the expenses list and total amount to SharedPreferences
    private void saveExpenses() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(expensesList);
        editor.putString(EXPENSES_KEY, json);
        editor.putFloat(TOTAL_KEY, (float) totalAmount);
        editor.apply();
    }

    // Load the expenses list and total amount from SharedPreferences
    private void loadExpenses() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(EXPENSES_KEY, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        if (json != null) {
            expensesList = gson.fromJson(json, type);
        }
        totalAmount = sharedPreferences.getFloat(TOTAL_KEY, 0.0f);
        updateTotalText();
    }
}
