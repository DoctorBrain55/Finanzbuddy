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

/**
 * Adapter für die RecyclerView, um Transaktionen in einer Liste anzuzeigen.
 * Enthält Funktionen zum Filtern, Sortieren und Löschen von Transaktionen.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>(); // Aktuell gefilterte Transaktionen
    private List<Transaction> allTransactions = new ArrayList<>(); // Gesamtliste aller Transaktionen

    private SharedViewModel sharedViewModel; // ViewModel zur gemeinsamen Nutzung von Daten

    /**
     * Konstruktor zur Übergabe des SharedViewModel.
     */
    public TransactionAdapter(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }

    /**
     * Setzt die Liste der Transaktionen und aktualisiert die RecyclerView.
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        this.allTransactions = new ArrayList<>(transactions);

        notifyDataSetChanged();
    }

    /**
     * Setzt alle Filter zurück und stellt die Original-Transaktionsliste wieder her.
     */
    public void resetAllFilters() {
        transactions = allTransactions;

        // SharedViewModel zurücksetzen
        if (sharedViewModel != null) {
            sharedViewModel.resetAllValues();
        }

        notifyDataSetChanged();
    }

    /**
     * Filtert Transaktionen nach einem bestimmten Maximalbetrag.
     */
    public void filterByAmount(double minAmount) {
        transactions = allTransactions.stream()
                .filter(t -> t.getAmount() <= minAmount)
                .collect(Collectors.toList());

        Log.d("Filter", "Total transactions filtered: " + transactions.size());
        Log.d("Filter", "Total transactions: " + allTransactions.size());

        notifyDataSetChanged();
    }

    /**
     * Sortiert die Transaktionsliste nach Datum (aufsteigend oder absteigend).
     */
    public void sortByDate(boolean ascending) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getDate));
        if (!ascending) {
            Collections.reverse(transactions);
        }
        Log.d("Sort", "Sort by date: " + ascending);

        notifyDataSetChanged();
    }

    /**
     * Sortiert die Transaktionsliste nach Betrag (aufsteigend oder absteigend).
     */
    public void sortByAmount(boolean ascending) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getAmount));

        if (!ascending) {
            Collections.reverse(transactions);
        }

        Log.d("Sort", "Sort by amount: " + ascending);

        notifyDataSetChanged();
    }

    /**
     * Erstellt eine neue View für ein RecyclerView-Element.
     */
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    /**
     * Bindet die Daten einer Transaktion an die RecyclerView-Zelle.
     */
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(transaction);

        // Löscht die Transaktion aus der Datenbank und RecyclerView bei Klick auf den Button
        holder.deleteButton.setOnClickListener(v -> {
            deleteTransaction(transaction.getId(), position);
        });

        // Setzt das Icon und die Farbe je nach positivem oder negativem Betrag
        if (transaction.getAmount() < 0) {
            holder.iconImageView.setImageResource(R.drawable.ic_transaction_negative);
            holder.amountTextView.setTextColor(Color.RED);  // Ausgaben in Rot
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_transaction_default);
            holder.amountTextView.setTextColor(Color.GREEN); // Einnahmen in Grün
        }
    }

    /**
     * Gibt die Anzahl der angezeigten Transaktionen zurück.
     */
    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     * ViewHolder-Klasse zur Verwaltung einzelner Transaktionsansichten.
     */
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

        /**
         * Setzt die Werte der Transaktion in die UI-Elemente.
         */
        public void bind(Transaction transaction) {
            descriptionTextView.setText(transaction.getDescription());
            amountTextView.setText("$" + String.valueOf(transaction.getAmount()));
            dateTextView.setText(transaction.getDate());
        }
    }

    /**
     * Löscht eine Transaktion aus der Firebase-Datenbank und entfernt sie aus der RecyclerView.
     */
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
                    notifyItemRemoved(position);
                    Log.d("TransactionAdapter", "Transaction deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("TransactionAdapter", "Failed to delete transaction", e);
                });
    }
}
