package com.example.momentum;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class HabitTest {
    @Test
    public void habit_constructor_test(){
        String test_title = "title";
        String test_reason = "reason";
        Date test_date = new Date();
        Boolean test_private = true;
        ArrayList<String> test_freq = new ArrayList<String>();
        test_freq.add("Monday");
        test_freq.add("Tuesday");
        test_freq.add("Wednesday");
        test_freq.add("Thursday");

        Habit habit = new Habit(test_title,test_reason, test_date, test_private, test_freq);
    }
    @Test
    public void habit_Get_Set_test(){

    }
    @Test
    public void habit_string_test(){

    }
}
