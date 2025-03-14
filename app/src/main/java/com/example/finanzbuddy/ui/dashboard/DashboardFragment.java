package com.example.finanzbuddy.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // UI-Elemente initialisieren
        tvUsername = view.findViewById(R.id.hello_user);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvAccountBalance = view.findViewById(R.id.tvAccountBalance);
        tvTransactionsCount = view.findViewById(R.id.tvTransactionsCount);
        tvMonthlyTransactionsCount = view.findViewById(R.id.tvMonthlyTransactionsCount);
        tvWeeklyTransactionsCount = view.findViewById(R.id.tvWeeklyTransactionsCount);

        // Aktuell angemeldeten Benutzer abrufen
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Benutzer-E-Mail anzeigen
            tvUserEmail.setText(user.getEmail());

            // Verweis auf Firebase-Datenbank für Transaktionsdaten
            transactionsRef = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("users")
                    .child(user.getUid())
                    .child("transactions");

            // Benutzername abrufen
            fetchUsername();
            // Transaktionsdaten abrufen
            fetchTransactionsData();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Speicherbereinigung
    }

    private void fetchTransactionsData() {
        // Holt alle Transaktionen des Benutzers aus Firebase
        transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalBalance = 0;
                int transactionCount = 0;
                int weeklyTransactionCount = 0;
                int monthlyTransactionCount = 0;

                // Aktuelles Datum abrufen
                Calendar calendar = Calendar.getInstance();
                int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                // Iteration durch alle Transaktionen
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        totalBalance += transaction.getAmount(); // Gesamtguthaben berechnen
                        transactionCount++; // Anzahl der Transaktionen erhöhen

                        try {
                            // Transaktionsdatum parsen
                            Date transactionDate = dateFormat.parse(transaction.getDate());
                            Calendar transactionCalendar = Calendar.getInstance();
                            transactionCalendar.setTime(transactionDate);

                            int transactionWeek = transactionCalendar.get(Calendar.WEEK_OF_YEAR);
                            int transactionMonth = transactionCalendar.get(Calendar.MONTH);
                            int transactionYear = transactionCalendar.get(Calendar.YEAR);

                            // Prüfen, ob die Transaktion in der aktuellen Woche liegt
                            if (transactionYear == currentYear && transactionWeek == currentWeek) {
                                weeklyTransactionCount++;
                            }

                            // Prüfen, ob die Transaktion im aktuellen Monat liegt
                            if (transactionYear == currentYear && transactionMonth == currentMonth) {
                                monthlyTransactionCount++;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // UI-Elemente mit berechneten Werten aktualisieren
                tvAccountBalance.setText("$" + String.format("%.2f", totalBalance));
                tvTransactionsCount.setText(String.valueOf(transactionCount));
                tvWeeklyTransactionsCount.setText(String.valueOf(weeklyTransactionCount));
                tvMonthlyTransactionsCount.setText(String.valueOf(monthlyTransactionCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Fehler bei der Datenbankabfrage behandeln
            }
        });
    }

    private void fetchUsername() {
        // Holt den Benutzernamen aus Firebase
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("users")
                .child(user.getUid());

        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Benutzernamen abrufen
                    String username = snapshot.child("username").getValue(String.class);

                    if (username != null) {
                        Log.d("Firebase", "Username: " + username);
                        tvUsername.setText("Hallo " + username + "!");
                    } else {
                        Log.e("Firebase", "Benutzername nicht gefunden");
                    }
                } else {
                    Log.e("Firebase", "Benutzerdaten nicht gefunden");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Datenbankfehler: " + error.getMessage());
            }
        });
    }
}
