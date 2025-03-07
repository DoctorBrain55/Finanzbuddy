package com.example.finanzbuddy.ui.transactions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Double> sharedAmountData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> sharedBooleanData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> sharedLargestOrSmallestData = new MutableLiveData<>();


    public void setSharedAmountData(Double amount) {
        sharedAmountData.setValue(amount);
    }

    public LiveData<Double> getSharedAmountData() {
        return sharedAmountData;
    }

    public void setSharedBooleanData(Boolean value) {
        sharedBooleanData.setValue(value);
    }

    public LiveData<Boolean> getSharedBooleanData() {
        return sharedBooleanData;
    }

    public void setSharedLargestOrSmallestData(Boolean value) {
        sharedLargestOrSmallestData.setValue(value);
    }

    public LiveData<Boolean> getSharedLargestOrSmallestData() {
        return sharedLargestOrSmallestData;
    }

    public void resetAllValues() {
        sharedAmountData.setValue(null);
        sharedBooleanData.setValue(null);
        sharedLargestOrSmallestData.setValue(null);
    }

}

