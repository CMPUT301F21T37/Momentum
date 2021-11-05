package com.example.momentum.habitEvents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.momentum.Habit;

import java.util.ArrayList;

/**
 * Custom ViewModel for HabitEventsFragment that handles mutable live data for all habit events.
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 */
public class HabitEventsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Event>> eventsList;

    public HabitEventsViewModel() {
        eventsList = new MutableLiveData<>();
    }

    /**
     * Getter for habitsList
     * @return
     * A Mutable Live ArrayList consisting of Habit habit
     */
    public LiveData<ArrayList<Event>> getHabitsList() {
        return eventsList;
    }

    /**
     * Adds a Event event to the ArrayList.
     * @param event
     * an instance of Event
     */
    public void addEvent(Event event) {
        ArrayList<Event> listHelper;
        if (eventsList.getValue() != null) {
            listHelper = new ArrayList<>(eventsList.getValue());
        }
        else {
            listHelper = new ArrayList<>();
        }
        listHelper.add(event);
        eventsList.setValue(listHelper);
    }

    /**
     * Clears eventsList.
     */
    public void clearEventsList() {
        ArrayList<Event> listHelper = new ArrayList<>();
        eventsList.setValue(listHelper);
    }
}
