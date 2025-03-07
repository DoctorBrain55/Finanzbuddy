package com.example.finanzbuddy.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finanzbuddy.R;
import com.example.finanzbuddy.databinding.FragmentDashboardBinding;
import com.example.finanzbuddy.ui.transactions.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private TextView tvUsername, tvUserEmail, tvAccountBalance, tvTransactionsCount, tvWeeklyTransactionsCount, tvMonthlyTransactionsCount;
    private DatabaseReference transactionsRef;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize UI Components
        tvUsername = view.findViewById(R.id.hello_user);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvAccountBalance = view.findViewById(R.id.tvAccountBalance);
        tvTransactionsCount = view.findViewById(R.id.tvTransactionsCount);
        tvMonthlyTransactionsCount = view.findViewById(R.id.tvMonthlyTransactionsCount);
        tvWeeklyTransactionsCount = view.findViewById(R.id.tvWeeklyTransactionsCount);

        // Get authenticated user
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Display user email
            tvUserEmail.setText(user.getEmail());

            // Reference to Firebase Database
            transactionsRef = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
                    .child(user.getUid())
                    .child("transactions");

            //Fetch username
            fetchUsername();
            // Fetch transaction data
            fetchTransactionsData();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchTransactionsData() {
        transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalBalance = 0;
                int transactionCount = 0;
                int weeklyTransactionCount = 0;
                int monthlyTransactionCount = 0;

                // Get the current date
                Calendar calendar = Calendar.getInstance();
                int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        totalBalance += transaction.getAmount(); // Calculate total balance
                        transactionCount++; // Count total transactions

                        try {
                            // Parse the transaction date
                            Date transactionDate = dateFormat.parse(transaction.getDate());
                            Calendar transactionCalendar = Calendar.getInstance();
                            transactionCalendar.setTime(transactionDate);

                            int transactionWeek = transactionCalendar.get(Calendar.WEEK_OF_YEAR);
                            int transactionMonth = transactionCalendar.get(Calendar.MONTH);
                            int transactionYear = transactionCalendar.get(Calendar.YEAR);

                            // Check if the transaction is from the current week
                            if (transactionYear == currentYear && transactionWeek == currentWeek) {
                                weeklyTransactionCount++;
                            }

                            // Check if the transaction is from the current month
                            if (transactionYear == currentYear && transactionMonth == currentMonth) {
                                monthlyTransactionCount++;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Update UI
                tvAccountBalance.setText("$" + String.format("%.2f", totalBalance));
                tvTransactionsCount.setText(String.valueOf(transactionCount));
                tvWeeklyTransactionsCount.setText(String.valueOf(weeklyTransactionCount));
                tvMonthlyTransactionsCount.setText(String.valueOf(monthlyTransactionCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void fetchUsername() {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(user.getUid());

        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the username
                    String username = snapshot.child("username").getValue(String.class);

                    if (username != null) {
                        // Use the username (e.g., display it in a TextView)
                        Log.d("Firebase", "Username: " + username);
                        tvUsername.setText("Hello " + username + "!");
                    } else {
                        Log.e("Firebase", "Username not found");
                    }
                } else {
                    Log.e("Firebase", "User data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });

    }
}