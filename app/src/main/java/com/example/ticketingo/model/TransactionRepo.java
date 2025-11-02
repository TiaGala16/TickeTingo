package com.example.ticketingo.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionRepo {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> totalSpentLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Transaction>> getTransactionsLiveData() {
        return transactionsLiveData;
    }

    public MutableLiveData<Double> getTotalSpentLiveData() {
        return totalSpentLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Create a transaction record
    public void createTransaction(String eventName, String date, String time, 
                                  double amount, String paymentMethod, String transactionDate) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String transactionId = db.collection("Transactions").document().getId();

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("id", transactionId);
        transaction.put("eventName", eventName);
        transaction.put("date", date);
        transaction.put("time", time);
        transaction.put("amount", amount);
        transaction.put("paymentMethod", paymentMethod);
        transaction.put("transactionDate", transactionDate);
        transaction.put("email", email);
        transaction.put("status", "success");

        db.collection("Transactions").document(transactionId).set(transaction)
                .addOnSuccessListener(aVoid -> 
                    Log.d("TransactionRepo", "Transaction saved successfully"))
                .addOnFailureListener(e -> {
                    Log.e("TransactionRepo", "Failed to save transaction: " + e.getMessage());
                    errorLiveData.postValue(e.getMessage());
                });
    }

    // Load all transactions for current user
    public void loadTransactions() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        
        db.collection("Transactions")
                .whereEqualTo("email", email)
                .orderBy("transactionDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("TransactionRepo", "Error loading transactions", error);
                        errorLiveData.postValue(error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Transaction> transactionList = new ArrayList<>();
                        double totalSpent = 0.0;

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Transaction transaction = doc.toObject(Transaction.class);
                            transaction.setId(doc.getId());
                            transactionList.add(transaction);
                            
                            // Calculate total spent
                            if ("success".equals(transaction.getStatus())) {
                                totalSpent += transaction.getAmount();
                            }
                        }

                        transactionsLiveData.postValue(transactionList);
                        totalSpentLiveData.postValue(totalSpent);
                        
                        Log.d("TransactionRepo", "Loaded " + transactionList.size() + " transactions");
                    }
                });
    }
}