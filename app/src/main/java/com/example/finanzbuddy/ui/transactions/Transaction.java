package com.example.finanzbuddy.ui.transactions;

public class Transaction {
    private String id;
    private String description;
    private double amount;
    private String date;

    // Constructor, getters, and setters

    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    public Transaction(String id, String description, double amount, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
