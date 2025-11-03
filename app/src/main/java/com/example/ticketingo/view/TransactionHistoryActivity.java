package com.example.ticketingo.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingo.R;
import com.example.ticketingo.model.Transaction;
import com.example.ticketingo.viewmodel.TransactionAdapter;
import com.example.ticketingo.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TextView totalSpentText;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private TransactionViewModel transactionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // Initialize views
        totalSpentText = findViewById(R.id.totalSpentText);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);

        // Initialize ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Setup RecyclerView
        transactionList = new ArrayList<>();
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, transactionList);
        transactionsRecyclerView.setAdapter(adapter);

        // Load transactions
        transactionViewModel.loadTransactions();

        // Observe transactions
        transactionViewModel.getTransactions().observe(this, transactions -> {
            if (transactions != null) {
                transactionList.clear();
                transactionList.addAll(transactions);
                adapter.notifyDataSetChanged();
            }
        });

        // Observe total spent
        transactionViewModel.getTotalSpent().observe(this, totalSpent -> {
            if (totalSpent != null) {
                totalSpentText.setText("₹" + String.format("%.2f", totalSpent));
            } else {
                totalSpentText.setText("₹0.00");
            }
        });


    }
}