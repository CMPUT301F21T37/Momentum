package com.example.momentum.home;

public class DayHabits {
    private String title;
    private String reason;

    public DayHabits(String title, String reason){
        this.title = title;
        this.reason = reason;
    }

    public String getDayHabitTitle(){
        return this.title;
    }


    public void setDayHabitTitle(String title) {
        this.title = title;
    }

    public String getDayHabitReason() {
        return reason;
    }

    public void setDayHabitReason(String reason) {
        this.reason = reason;
    }
}
