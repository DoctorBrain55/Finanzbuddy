package com.example.finanzbuddy;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.finanzbuddy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // ViewBinding für einfachere UI-Verwaltung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisiert ViewBinding für das Layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Zugriff auf das Bottom Navigation Menü
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Definiert die Navigationsstruktur: Diese Menüpunkte sind Hauptziele
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.navigation_dashboard, R.id.navigation_transactions, 
                 R.id.navigation_statistics, R.id.navigation_settings)
                .build();

        // Holt das NavHostFragment, das die Navigation verwaltet
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        // Verbindet das Bottom Navigation Menü mit dem NavController
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}
