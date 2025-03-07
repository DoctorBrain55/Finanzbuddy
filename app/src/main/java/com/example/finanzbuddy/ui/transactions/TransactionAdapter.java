package com.example.finanzbuddy.ui.transactions;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanzbuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();
    private List<Transaction> allTransactions = new ArrayList<>();

    private SharedViewModel sharedViewModel;

    public TransactionAdapter(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        this.allTransactions = new ArrayList<>(transactions);

        notifyDataSetChanged();
    }

    public void resetAllFilters() {
        //fetchTransactions();

        //Reset all sharedviewmodel
        transactions = allTransactions;

        // Reset ViewModel values
        if (sharedViewModel != null) {
            sharedViewModel.resetAllValues();
        }

        notifyDataSetChanged();
    }

    public void filterByAmount(double minAmount) {
        transactions = allTransactions.stream()
                .filter(t -> t.getAmount() <= minAmount) // Change from >= to <=
                .collect(Collectors.toList());

        Log.d("Filter", "Total transactions filtered: " + transactions.size());
        Log.d("Filter", "Total transactions: " + allTransactions.size());

        notifyDataSetChanged();
    }


    public void sortByDate(boolean ascending) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getDate));
        if (!ascending) {
            Collections.reverse(transactions);
        }
        Log.println(Log.DEBUG, "Sort", "Sort by date: " + ascending);

        notifyDataSetChanged();
    }

    public void sortByAmount(boolean ascending) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getAmount));

        if (!ascending) {
            Collections.reverse(transactions);
        }

        Log.println(Log.DEBUG, "Sort", "Sort by amount: " + ascending);

        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);

        holder.deleteButton.setOnClickListener(v -> {
            deleteTransaction(transaction.getId(), position);
        });

        // Set color based on positive/negative amount
        if (transaction.getAmount() < 0) {
            holder.iconImageView.setImageResource(R.drawable.ic_transaction_negative);
            holder.amountTextView.setTextColor(Color.RED);  // Expenses in red
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_transaction_default);
            holder.amountTextView.setTextColor(Color.GREEN); // Income in green
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView descriptionTextView, amountTextView, dateTextView;

        private ImageView iconImageView;
        private Button deleteButton;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);

            iconImageView = itemView.findViewById(R.id.transactionIcon);

            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Transaction transaction) {
            descriptionTextView.setText(transaction.getDescription());
            amountTextView.setText("$" + String.valueOf(transaction.getAmount()));
            dateTextView.setText(transaction.getDate());
        }
    }

    private void deleteTransaction(String transactionId, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("TransactionAdapter", "User is not authenticated");
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(user.getUid())
                .child("transactions")
                .child(transactionId);

        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    //transactions.remove(position);
                    notifyItemRemoved(position);
                    //notifyItemRangeChanged(position, transactions.size()); // Update indices
                    Log.d("TransactionAdapter", "Transaction deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("TransactionAdapter", "Failed to delete transaction", e);
                });
    }
}