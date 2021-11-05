package com.example.momentum.habitEvents;

/**
 * A class that stores information stored from HabitEventsFragment for easy access and initialization.
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 */
public class Event {
    private String title;
    private String comment;

    public Event (String title, String comment){
        this.title = title;
        this.comment = comment;
    }

    /**
     * Getter for Events title
     * @return
     * A string of the title.
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Getter for Events comment
     * @return
     * A string of the comment.
     */
    public String getComment() {
        return comment;
    }
}