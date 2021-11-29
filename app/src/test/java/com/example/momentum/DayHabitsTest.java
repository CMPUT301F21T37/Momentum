package com.example.momentum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.momentum.home.DayHabits;

import junit.framework.TestCase;

import org.junit.Test;

public class DayHabitsTest {
    /**
     * Helper method to instantiate DayHabits
     * @return
     * an instance of DayHabits
     */
    private DayHabits mockDayHabits() {
        return new DayHabits("Exercise", "I want to be healthy!");
    }

    /**
     * Tests the getters of DayHabits with successful assertions
     */
    @Test
    public void testGettersSuccess() {
        DayHabits habit_exercise = mockDayHabits();
        assertEquals("Exercise", habit_exercise.getDayHabitTitle());
        assertEquals("I want to be healthy!", habit_exercise.getDayHabitReason());
    }

    /**
     * Tests the getters of DayHabits with failed assertions
     */
    @Test
    public void testGettersFailure() {
        DayHabits habit_exercise = mockDayHabits();
        assertNotEquals("Not exercise", habit_exercise.getDayHabitTitle());
        assertNotEquals("I want to be unhealthy!", habit_exercise.getDayHabitReason());
    }

}
