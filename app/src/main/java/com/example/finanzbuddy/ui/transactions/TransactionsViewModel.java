package com.example.finanzbuddy.ui.transactions;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionsViewModel extends ViewModel {
    private MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>(new ArrayList<>());
    private List<Transaction> allTransactions = new ArrayList<>();
    private DatabaseReference databaseReference;

    private FirebaseUser user;
    private String userId;

    private SharedViewModel sharedViewModel;

    public TransactionsViewModel() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(userId)
                .child("transactions");

        // Fetch transactions from Firebase
        fetchTransactions();
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
        //return transactionsLiveData;
    }

    public void fetchTransactions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> transactionList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        transaction.setId(dataSnapshot.getKey()); // Assign Firebase key
                        transactionList.add(transaction);
                    }
                }
                transactions.setValue(transactionList);
                Log.d("TransactionsViewModel", "Total transactions fetched: " + transactionList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

//    public void fetchTransactions() {
//        if (user == null) {
//            Log.e("TransactionsViewModel", "User is not authenticated");
//            return;
//        }
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<Transaction> transactionList = new ArrayList<>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
//                    if (transaction != null) {
//                        transactionList.add(transaction);
//                        Log.d("TransactionsViewModel", "Fetched transaction: " + transaction.getAmount());
//                    }
//                }
//                allTransactions = new ArrayList<>(transactionList);
//                transactionsLiveData.setValue(transactionList);
//
//                Log.d("TransactionsViewModel", "Total transactions fetched: " + allTransactions.size());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("TransactionsViewModel", "Database error: " + error.getMessage());
//            }
//        });
//    }

    public void addTransaction(String description, double amount, String date, Boolean isExpense) {

        if (user == null) {
            Log.println(Log.DEBUG, "Error", "User is not authenticated");
            return;
        }

        DatabaseReference userTransactionsRef = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(userId)
                .child("transactions"); // Store transactions under the authenticated user's UID

        String transactionId = userTransactionsRef.push().getKey();
        double finalAmount = isExpense ? -Math.abs(amount) : Math.abs(amount); // Ensure expenses are negative

        Transaction transaction = new Transaction(transactionId, description, finalAmount, date);

        Log.println(Log.DEBUG, "Success", "Database Reference: " + userTransactionsRef);
        if (transactionId != null) {
            userTransactionsRef.child(transactionId).setValue(transaction)
                    .addOnSuccessListener(aVoid -> {
                        Log.println(Log.DEBUG, "Success", "Transaction added: " + transactionId);
                    })
                    .addOnFailureListener(e -> {
                        Log.println(Log.DEBUG, "Error", e.getMessage());
                    });
        } else {
            Log.println(Log.DEBUG, "Error", "Transaction ID is null");
        }
    }


    private MutableLiveData<Double> balance;


    public LiveData<Double> getBalance() {
        return balance;
    }

    public void resetAllFilters() {
        fetchTransactions();
        //transactions.setValue(allTransactions);
    }

    public void editTransaction(Transaction transaction) {
        // Edit transaction in Firebase and update LiveData
    }

    public void deleteTransaction(Transaction transaction) {
        // Delete transaction from Firebase and update LiveData
    }

    public void resetTransactions(TransactionAdapter transactionAdapter) {
        // Clear transactions in Firebase and reset balance
        if (user == null) {
            Log.println(Log.DEBUG, "Error", "User is not authenticated");
            return;
        }

        DatabaseReference userTransactionsRef = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(userId)
                .child("transactions"); // Store transactions under the authenticated user's UID

        // Remove all transactions
        userTransactionsRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "All transactions successfully deleted.");
                        allTransactions.clear();
                        transactions.setValue(allTransactions);
                        transactionAdapter.notifyDataSetChanged(); // Refresh UI
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Failed to delete transactions: " + e.getMessage());
                    }
                });

    }
}