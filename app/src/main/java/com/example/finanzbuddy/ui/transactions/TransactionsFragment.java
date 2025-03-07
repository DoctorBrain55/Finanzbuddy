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
import android.widget.EditText;
import java.util.Calendar;

import com.example.finanzbuddy.R;

public class TransactionsFragment extends Fragment {

    private TransactionsViewModel mViewModel;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private Button addTransactionButton, resetFiltersButton, resetButton;

    private SharedViewModel sharedViewModel;

    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // Initialize RecyclerView
        transactionRecyclerView = view.findViewById(R.id.transactionRecyclerView);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter
        transactionAdapter = new TransactionAdapter(sharedViewModel);
        transactionRecyclerView.setAdapter(transactionAdapter);

        // Add Transaction Button
        addTransactionButton = view.findViewById(R.id.addTransactionButton);
        addTransactionButton.setOnClickListener(v -> showAddTransactionDialog());

        resetFiltersButton = view.findViewById(R.id.resetFiltersButton);
        resetFiltersButton.setOnClickListener(v -> transactionAdapter.resetAllFilters());

        resetButton = view.findViewById(R.id.resetButton);

        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(requireActivity()).get(TransactionsViewModel.class);
//
//        // Observe transactions from ViewModel
//        mViewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
//            transactionAdapter.setTransactions(transactions);
//        });
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);

        // Ensure latest data is fetched when fragment loads
        mViewModel.fetchTransactions();

        mViewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            Log.d("TransactionsFragment", "Updating UI with " + transactions.size() + " transactions");
            transactionAdapter.setTransactions(transactions);

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
        if (mViewModel != null) {
            Log.d("TransactionsFragment", "Resuming - Fetching latest transactions");
            mViewModel.fetchTransactions();
        }
    }


    private void showAddTransactionDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_transaction, null);

        // Initialize dialog components
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        EditText amountEditText = dialogView.findViewById(R.id.amountEditText);
        EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
        Button addButton = dialogView.findViewById(R.id.addButton);
        SwitchCompat expenseSwitch = dialogView.findViewById(R.id.incomeOrExpenseSwitch);

        // Create and show the dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Add New Transaction")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create();

        dateEditText.setOnClickListener(v -> {
            // Get current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format the selected date (YYYY-MM-DD)
                String selectedDate = selectedYear + "-" + String.format("%02d", (selectedMonth + 1)) + "-" + String.format("%02d", selectedDay);
                dateEditText.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        expenseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    expenseSwitch.setText("Expense");
                } else {
                    expenseSwitch.setText("Income");
                }
            }
        });

        // Handle Add Button Click
        addButton.setOnClickListener(v -> {
            String description = descriptionEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            Boolean isExpense = expenseSwitch.isChecked();

            if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            //Transaction transaction = new Transaction(description, amount, date);

            // Add transaction to Firebase via ViewModel
            mViewModel.addTransaction(description, amount, date, isExpense);

            // Dismiss the dialog
            dialog.dismiss();
        });

        dialog.show();
    }

}