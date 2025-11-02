package com.example.ticketingo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.Transaction;
import com.example.ticketingo.model.TransactionRepo;

import java.util.List;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepo repo = new TransactionRepo();

    public void createTransaction(String eventName, String date, String time,
                                  double amount, String paymentMethod, String transactionDate) {
        repo.createTransaction(eventName, date, time, amount, paymentMethod, transactionDate);
    }

    public void loadTransactions() {
        repo.loadTransactions();
    }

    public LiveData<List<Transaction>> getTransactions() {
        return repo.getTransactionsLiveData();
    }

    public LiveData<Double> getTotalSpent() {
        return repo.getTotalSpentLiveData();
    }

    public LiveData<String> getError() {
        return repo.getErrorLiveData();
    }
}