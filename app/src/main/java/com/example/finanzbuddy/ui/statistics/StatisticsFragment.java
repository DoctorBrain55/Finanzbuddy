package com.example.finanzbuddy.ui.statistics;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SwitchCompat;

import com.example.finanzbuddy.R;
import com.example.finanzbuddy.ui.transactions.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel mViewModel;
    private SwitchCompat switchStatisticsView;
    private BarChart barChartYearly;
    private LineChart lineChartMonthly;
    private DatabaseReference transactionsRef;
    private FirebaseUser user;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Initialize UI
        //switchStatisticsView = view.findViewById(R.id.switchStatisticsView);
        barChartYearly = view.findViewById(R.id.barChartYearly);
        lineChartMonthly = view.findViewById(R.id.lineChartMonthly);

        // Firebase Auth
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            transactionsRef = FirebaseDatabase.getInstance("https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
                    .child(user.getUid())
                    .child("transactions");

            fetchYearlyStatistics(); // Default view: Yearly statistics
            fetchMonthlyStatistics();
        }

        // Toggle between Yearly & Monthly Views
//        switchStatisticsView.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                barChartYearly.setVisibility(View.VISIBLE);
//                lineChartMonthly.setVisibility(View.GONE);
//                fetchYearlyStatistics();
//            } else {
//                barChartYearly.setVisibility(View.GONE);
//                lineChartMonthly.setVisibility(View.VISIBLE);
//                fetchMonthlyStatistics();
//            }
//        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void fetchYearlyStatistics() {
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<Integer, Double> monthlyData = new HashMap<>();
                String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        int month = Integer.parseInt(transaction.getDate().split("-")[1]);
                        monthlyData.put(month, monthlyData.getOrDefault(month, 0.0) + transaction.getAmount());
                    }
                }

                ArrayList<BarEntry> entries = new ArrayList<>();
                for (int i = 1; i <= 12; i++) {
                    entries.add(new BarEntry(i, monthlyData.getOrDefault(i, 0.0).floatValue()));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Monthly Expenses");
                dataSet.setColor(getResources().getColor(R.color.teal_200));

                BarData barData = new BarData(dataSet);
                barChartYearly.setData(barData);

                // Customize X-Axis
                XAxis xAxis = barChartYearly.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(months)); // Display short month names
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f); // Ensure each bar represents one month
                xAxis.setLabelCount(12);
                xAxis.setTextSize(12f);

                // Customize Y-Axis
                YAxis yAxisLeft = barChartYearly.getAxisLeft();
                yAxisLeft.setTextSize(12f);
                yAxisLeft.setAxisMinimum(0f); // No negative values
                barChartYearly.getAxisRight().setEnabled(false); // Hide right Y-axis

                // Remove grid lines
                xAxis.setDrawGridLines(false);
                yAxisLeft.setDrawGridLines(false);

                // Set chart description
                barChartYearly.getDescription().setText("Total expenses per month");
                barChartYearly.getDescription().setTextSize(12f);

                barChartYearly.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchMonthlyStatistics() {
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<Integer, Double> dailyData = new HashMap<>();
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        String[] dateParts = transaction.getDate().split("-");
                        int month = Integer.parseInt(dateParts[1]);
                        int day = Integer.parseInt(dateParts[2]);

                        if (month == currentMonth) {
                            dailyData.put(day, dailyData.getOrDefault(day, 0.0) + transaction.getAmount());
                        }
                    }
                }

                ArrayList<Entry> entries = new ArrayList<>();
                for (int i = 1; i <= 31; i++) {
                    entries.add(new Entry(i, dailyData.getOrDefault(i, 0.0).floatValue()));
                }

                LineDataSet dataSet = new LineDataSet(entries, "Daily Spending");
                dataSet.setColor(getResources().getColor(R.color.teal_700));
                dataSet.setCircleColor(getResources().getColor(R.color.teal_200));
                dataSet.setDrawFilled(true); // Enable fill under the line
                dataSet.setFillColor(getResources().getColor(R.color.teal_200));
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curved line

                LineData lineData = new LineData(dataSet);
                lineChartMonthly.setData(lineData);

                // Customize X-Axis
                XAxis xAxis = lineChartMonthly.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setTextSize(12f);

                // Customize Y-Axis
                YAxis yAxisLeft = lineChartMonthly.getAxisLeft();
                yAxisLeft.setTextSize(12f);
                yAxisLeft.setAxisMinimum(0f);
                lineChartMonthly.getAxisRight().setEnabled(false); // Hide right Y-axis

                // Remove grid lines
                xAxis.setDrawGridLines(false);
                yAxisLeft.setDrawGridLines(false);

                // Set chart description
                lineChartMonthly.getDescription().setText("Daily spending trend");
                lineChartMonthly.getDescription().setTextSize(12f);

                lineChartMonthly.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}