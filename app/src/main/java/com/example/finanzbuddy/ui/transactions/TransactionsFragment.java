package com.example.finanzbuddy.ui.transactions;

import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;

import com.example.finanzbuddy.R;

/**
 * Fragment für die Anzeige und Verwaltung der Transaktionsliste.
 * Enthält Funktionen zum Hinzufügen, Filtern, Sortieren und Zurücksetzen von Transaktionen.
 */
public class TransactionsFragment extends Fragment {

    private TransactionsViewModel mViewModel;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private Button addTransactionButton, resetFiltersButton, resetButton;

    private SharedViewModel sharedViewModel;

    /**
     * Erstellt eine neue Instanz des Fragments.
     */
    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewModel zur gemeinsamen Nutzung von Daten initialisieren
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // RecyclerView initialisieren
        transactionRecyclerView = view.findViewById(R.id.transactionRecyclerView);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter initialisieren
        transactionAdapter = new TransactionAdapter(sharedViewModel);
        transactionRecyclerView.setAdapter(transactionAdapter);

        // Button zum Hinzufügen einer Transaktion
        addTransactionButton = view.findViewById(R.id.addTransactionButton);
        addTransactionButton.setOnClickListener(v -> showAddTransactionDialog());

        // Button zum Zurücksetzen von Filtern
        resetFiltersButton = view.findViewById(R.id.resetFiltersButton);
        resetFiltersButton.setOnClickListener(v -> transactionAdapter.resetAllFilters());

        resetButton = view.findViewById(R.id.resetButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);

        // Stellt sicher, dass die neuesten Daten beim Laden des Fragments abgerufen werden
        mViewModel.fetchTransactions();

        // Beobachtet Änderungen der Transaktionsliste und aktualisiert die RecyclerView
        mViewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            Log.d("TransactionsFragment", "Updating UI with " + transactions.size() + " transactions");
            transactionAdapter.setTransactions(transactions);

            // Überprüfung, ob das SharedViewModel verfügbar ist
            if (sharedViewModel != null) {
                sharedViewModel.getSharedAmountData().observe(getViewLifecycleOwner(), new Observer<Double>() {
                    @Override
                    public void onChanged(Double s) {
                        if (s == null) return;
                        Log.d("TransactionsViewModel", "Shared data changed: " + s);
                        transactionAdapter.filterByAmount(s);
                    }
                });

                sharedViewModel.getSharedBooleanData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean s) {
                        if (s == null) return;
                        Log.d("TransactionsViewModel", "Shared data changed: " + s);
                        transactionAdapter.sortByDate(s);
                    }
                });

                sharedViewModel.getSharedLargestOrSmallestData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean s) {
                        if (s == null) return;
                        Log.d("TransactionsViewModel", "Shared data changed: " + s);
                        transactionAdapter.sortByAmount(s);
                    }
                });
            } else {
                Log.e("TransactionsFragment", "SharedViewModel is NULL");
            }
        });

        // Button zum Zurücksetzen der Transaktionen
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.resetTransactions(transactionAdapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Stellt sicher, dass beim Zurückkehren zum Fragment die neuesten Daten geladen werden
        if (mViewModel != null) {
            Log.d("TransactionsFragment", "Resuming - Fetching latest transactions");
            mViewModel.fetchTransactions();
        }
    }

    /**
     * Zeigt einen Dialog zum Hinzufügen einer neuen Transaktion an.
     */
    private void showAddTransactionDialog() {
        // Dialog-Layout laden
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_transaction, null);

        // Dialog-Komponenten initialisieren
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        EditText amountEditText = dialogView.findViewById(R.id.amountEditText);
        EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
        Button addButton = dialogView.findViewById(R.id.addButton);
        SwitchCompat expenseSwitch = dialogView.findViewById(R.id.incomeOrExpenseSwitch);

        // Dialog erstellen und anzeigen
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Neue Transaktion hinzufügen")
                .setView(dialogView)
                .setNegativeButton("Abbrechen", null)
                .create();

        // Datumsauswahl für die Transaktion
        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Zeigt einen DatePickerDialog an, um das Datum auszuwählen
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                String selectedDate = selectedYear + "-" + String.format("%02d", (selectedMonth + 1)) + "-" + String.format("%02d", selectedDay);
                dateEditText.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        // Schalter für Einnahmen oder Ausgaben
        expenseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    expenseSwitch.setText("Ausgabe");
                } else {
                    expenseSwitch.setText("Einnahme");
                }
            }
        });

        // Fügt eine neue Transaktion hinzu, wenn der Benutzer auf "Hinzufügen" klickt
        addButton.setOnClickListener(v -> {
            String description = descriptionEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            Boolean isExpense = expenseSwitch.isChecked();

            // Überprüfung, ob alle Felder ausgefüllt sind
            if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Transaktion über das ViewModel in Firebase speichern
            mViewModel.addTransaction(description, amount, date, isExpense);

            // Dialog schließen
            dialog.dismiss();
        });

        dialog.show();
    }
}
