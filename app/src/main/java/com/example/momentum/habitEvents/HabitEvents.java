package com.example.momentum.habitvents;

public class HabitEvents {
    private String title;
    private String reason;


    public HabitEvents(String title, String reason){
        this.title = title;
        this.reason = reason;
    }

    public String getTitle() {
        return this.title;
    }

    public String getReason() {
        return this.reason;
    }
}
