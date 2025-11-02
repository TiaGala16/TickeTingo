package com.example.ticketingo.model;

import com.google.firebase.firestore.PropertyName;

public class Transaction {
    private String id;
    private String eventName;
    private String date;
    private String time;
    private double amount;
    private String paymentMethod;
    private String transactionDate;
    private String email;
    private String status; // "success", "failed", "pending"

    public Transaction() {
        // Required empty constructor for Firestore
    }

    public Transaction(String eventName, String date, String time, double amount, 
                      String paymentMethod, String transactionDate, String email, String status) {
        this.eventName = eventName;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionDate = transactionDate;
        this.email = email;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}