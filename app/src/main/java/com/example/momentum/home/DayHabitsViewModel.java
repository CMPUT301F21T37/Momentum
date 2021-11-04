package com.example.momentum.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * Custom ViewModel for DayHabitsFragment that handles mutable live data for the habits list of the current day.
 * @author: Kaye Ena Crayzhel F. Misay
 */
public class DayHabitsViewModel extends ViewModel {

    private MutableLiveData<String> dateTitle;
    private MutableLiveData<ArrayList<DayHabits>> habitsList;

    public DayHabitsViewModel() {
        dateTitle = new MutableLiveData<>();
        habitsList = new MutableLiveData<>();
    }

    /**
     * Getter for dateTitle
     * @return
     * A Mutable Live String of the date title (ex: October 19, 2021 Habits)
     */
    public LiveData<String> getTitle() {
        return dateTitle;
    }

    /**
     * Setter for dateTitle
     * @param date_title
     * Sets a date title to the mutable live string dateTitle
     */
    public void setTitle(MutableLiveData<String> date_title) {
        this.dateTitle = date_title;
    }

    /**
     * Getter for habitsList
     * @return
     * A Mutable Live ArrayList consisting of DayHabits
     */
    public LiveData<ArrayList<DayHabits>> getHabitsList() {
        return habitsList;
    }

    /**
     * Adds a DayHabit habit to the ArrayList.
     * @param habit
     * an instance of DayHabits
     */
    public void addHabit(DayHabits habit) {
        ArrayList<DayHabits> listHelper;
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
        ArrayList<DayHabits> listHelper = new ArrayList<>();
        habitsList.setValue(listHelper);
    }
}