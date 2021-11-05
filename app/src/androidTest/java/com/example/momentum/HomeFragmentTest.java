package com.example.momentum;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test class for HomeFragment.
 */
public class HomeFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * This method runs before all tests and create a solo instance.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Helper method to log in with correct entries to be able to test
     */
    private void login() {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "test12345");
        solo.clickOnButton("Login");
    }

    /**
     * Simple test cast to verify if everything is okay.
     */
    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }

    /**
     * Checks if the calendar view goes to another view (Current day's Habits page)
     */
    @Test
    public void checkCalendar(){
        // log in with correct entries
        login();

        // checks that we are in MainActivity
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);

        // click on a date on the calendar and checks if it goes to another fragment
        solo.clickOnView(solo.getView(R.id.calendarView));
        assertTrue(solo.waitForText("Habits", 1, 2000));
    }

    /**
     * Checks if the Habit Events Button works
     */
    @Test
    public void checkHabitEventsButton(){
        // log in with correct entries
        login();

        // checks that we are in MainActivity
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);

        // clicks the HabitEventsAdapter Button
        solo.clickOnButton("Habit Events");

        // checks that we've changed to the Events Fragment
        solo.waitForText("Events", 1, 2000);
    }

    /**
     * Checks if the Habit Events Button works
     */
    @Test
    public void checkHabitsButton(){
        // log in with correct entries
        login();

        // checks that we are in MainActivity
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);

        // clicks the HabitEventsAdapter Button
        solo.clickOnButton("Habits");

        // checks that we've changed to the Events Fragment
        solo.waitForText("Habits", 1, 2000);
    }
}
