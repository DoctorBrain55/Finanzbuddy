package com.example.finanzbuddy.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel für das Dashboard-Fragment.
 * Verwaltet die Daten des Dashboards unabhängig vom UI-Lifecycle.
 */
public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText; // LiveData-Variable zur Speicherung von Textdaten

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment"); // Standardtext setzen
    }

    /**
     * Gibt die LiveData-Variable zurück, die vom Fragment beobachtet werden kann.
     */
    public LiveData<String> getText() {
        return mText;
    }
}
