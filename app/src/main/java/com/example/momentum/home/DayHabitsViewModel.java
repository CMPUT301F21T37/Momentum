package com.example.momentum.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class DayHabitsViewModel extends ViewModel {

    private MutableLiveData<String> dateTitle;
    private MutableLiveData<ArrayList<DayHabits>> habitsList;

    public DayHabitsViewModel() {
        dateTitle = new MutableLiveData<>();
        habitsList = new MutableLiveData<>();
    }

    public LiveData<String> getTitle() {
        return dateTitle;
    }

    public void setTitle(MutableLiveData<String> date_title) {
        this.dateTitle = date_title;
    }

    public LiveData<ArrayList<DayHabits>> getHabitsList() {
        return habitsList;
    }

    public void addHabit(DayHabits habits) {
        ArrayList<DayHabits> listHelper;
        if (habitsList.getValue() != null) {
            listHelper = new ArrayList<>(habitsList.getValue());
        }
        else {
            listHelper = new ArrayList<>();
        }
        listHelper.add(habits);
        habitsList.setValue(listHelper);
    }

    public void clearHabitsList() {
        ArrayList<DayHabits> listHelper = new ArrayList<>();
        habitsList.setValue(listHelper);
    }
}