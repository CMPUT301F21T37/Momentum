package com.example.momentum.habitEvents;

/**
 * A class that stores information stored in HabitEventsFragments for easy access and initialization.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class Event {
    private String title;
    private String comment;

    public Event (String title, String comment){
        this.title = title;
        this.comment = comment;
    }

    /**
     * Getter for Event title
     * @return
     * A string of the title.
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Getter for Event reason
     * @return
     * A string of the habit reason.
     */
    public String getComment() {
        return comment;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}