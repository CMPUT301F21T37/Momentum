package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.home.AddHabitEventActivity;
import com.example.momentum.home.DayHabitsActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DayHabitsFragmentTest {
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
     * Helper to go to the activity
     */
    private void goToActivity() {
        solo.clickOnView(solo.getView(R.id.calendarView));
        solo.waitForText("Habits", 1, 2000);
        solo.clickOnText("Study");
    }

    /**
     * Simple test cast to verify if everything is okay.
     */
    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }

    /**
     * Checks to see if when a habit that is not completed (greyed out) is clicked,
     * it goes to DayHabitsActivity
     */
    @Test
    public void testWhenHabitNotCompleted() {
        // logs in with correct test inputs
        login();

        // clicks on the habit that is not completed
        solo.clickOnView(solo.getView(R.id.calendarView));
        solo.waitForText("Habits", 1, 2000);
        solo.clickOnText("Study");

        // checks if it is in the DayHabitsActivity to set if a habit is completed
        solo.assertCurrentActivity("Wrong Activity!", DayHabitsActivity.class);
    }

    /**
     * Checks to see if when a habit that is completed (red coloured) is clicked without a habit event,
     * it goes to AddHabitEventActivity
     */
    @Test
    public void testWhenHabitCompletedNoEvent() {
        // logs in with correct test inputs
        login();

        // clicks on the habit that is not completed
        solo.clickOnView(solo.getView(R.id.calendarView));
        solo.waitForText("Habits", 1, 2000);
        solo.clickOnText("Exercise");

        // checks if it is in the AddHabitEventActivity to add a habit event
        solo.assertCurrentActivity("Wrong Activity!", AddHabitEventActivity.class);
    }

    /**
     * Checks to see if when a habit that is completed (red coloured) is clicked with a habit event,
     * it shows a custom toast
     */
    @Test
    public void testWhenHabitCompletedWithEvent() {
        // logs in with correct test inputs
        login();

        // clicks on the habit that is not completed
        solo.clickOnView(solo.getView(R.id.calendarView));
        solo.waitForText("Habits", 1, 2000);
        solo.clickOnText("Coding");

        // checks if a toast exists
        solo.waitForText("You have already added a habit event for today. Please edit or delete your event by clicking the 'Habit Events' button on the Home page.",
                1, 2000);

        // checks to see that it didn't go to another activity
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
