package com.example.momentum.habitEvents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class HabitEventsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Event>> eventsList;

    public HabitEventsViewModel() {
        eventsList = new MutableLiveData<>();
    }

    /**
     * Getter for habit eventsList
     * @return
     * A Mutable Live ArrayList consisting of Events habit events
     */
    public LiveData<ArrayList<Event>> getEventsList() {
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
