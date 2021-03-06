package com.example.momentum;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class HabitTest {

    /**
     * Tests Habit constructor and its get functions.
     */
    @Test
    public void habit_construct_get_test(){
        String test_title = "title";
        String test_reason = "reason";
        Integer test_order = 0;
        Date test_date = new Date();
        Boolean test_private = true;
        ArrayList<String> test_freq = new ArrayList<String>();
        test_freq.add("Monday");
        test_freq.add("Tuesday");
        test_freq.add("Wednesday");
        test_freq.add("Thursday");
        Habit habit = new Habit(test_title,test_reason, test_date, test_private, test_freq, test_order);

        assertEquals(test_title, habit.getTitle());
        assertEquals(test_reason, habit.getReason());
        assertEquals(test_private, habit.isPrivate_account());
        assertEquals(test_date, habit.getDate());
        assertEquals(test_freq, habit.getWeekly_frequency());
        assertEquals(test_order, habit.getOrder());

    }

    /**
     * Tests setters of the habit function
     */
    @Test
    public void habit_Set_test(){
        String test_title = "title";
        String test_reason = "reason";
        Integer test_order = 0;
        Date test_date = new Date();
        Boolean test_private = true;
        ArrayList<String> test_freq = new ArrayList<String>();
        test_freq.add("Monday");
        test_freq.add("Tuesday");
        test_freq.add("Wednesday");
        test_freq.add("Thursday");
        Habit habit = new Habit(test_title,test_reason, test_date, test_private, test_freq, test_order);

        String new_title = "better_title";
        String new_reason = "better_reason";
        Integer new_order = 1;
        Date new_date = new Date();
        Boolean new_private = false;
        ArrayList<String> new_freq = new ArrayList<String>();
        new_freq.add("Saturday");
        new_freq.add("Sunday");
        habit.setTitle(new_title);
        habit.setReason(new_reason);
        habit.setDate(new_date);
        habit.setPrivacy(new_private);
        habit.setWeekly_frequency(new_freq);
        habit.setOrder(new_order);
        Habit new_habit = new Habit(new_title, new_reason, new_date, new_private, new_freq, new_order);

        assertEquals(new_title, habit.getTitle());
        assertEquals(new_reason, habit.getReason());
        assertEquals(new_private, habit.isPrivate_account());
        assertEquals(new_date, habit.getDate());
        assertEquals(new_freq, habit.getWeekly_frequency());
        assertEquals(new_order, habit.getOrder());
    }
}
