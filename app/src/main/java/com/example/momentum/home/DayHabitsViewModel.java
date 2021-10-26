package com.example.momentum.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DayHabitsViewModel extends ViewModel {

    private MutableLiveData<String> date_title;

    public DayHabitsViewModel() {
        date_title = new MutableLiveData<>();
        date_title.setValue("Current Date Habit");
    }

    public LiveData<String> getText() {
        return date_title;
    }

    public void setText(MutableLiveData<String> date) {
        this.date_title = date;
    }
}