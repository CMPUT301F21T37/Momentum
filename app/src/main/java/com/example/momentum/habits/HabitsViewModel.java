package com.example.momentum.habits;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.momentum.Habit;

import java.util.ArrayList;

public class HabitsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Habit>> habitsList;

    public HabitsViewModel() {
        habitsList = new MutableLiveData<>();
    }

    /**
     * Getter for habitsList
     * @return
     * A Mutable Live ArrayList consisting of Habit habit
     */
    public LiveData<ArrayList<Habit>> getHabitsList() {
        return habitsList;
    }

    /**
     * Adds a Habit habit to the ArrayList.
     * @param habit
     * an instance of Habit
     */
    public void addHabit(Habit habit) {
        ArrayList<Habit> listHelper;
        if (habitsList.getValue() != null) {
            listHelper = new ArrayList<>(habitsList.getValue());
        }
        else {
            listHelper = new ArrayList<>();
        }
        listHelper.add(habit);
        habitsList.setValue(listHelper);
    }

    /**
     * Clears habitsList.
     */
    public void clearHabitsList() {
        ArrayList<Habit> listHelper = new ArrayList<>();
        habitsList.setValue(listHelper);
    }
}
