package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.DeleteHabitActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DeleteHabitActivityTest {
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
     * Helper method to add a habit
     * title:Testing
     * reason: 4/4, please?
     * Date: November 1, 2021
     * Frequency: Saturday, Sunday
     * Privacy: Default (Public)
     */
    private void addHabit() {
        solo.clickOnView(solo.getView(R.id.navigation_add_habit));
        solo.enterText((EditText) solo.getView(R.id.edit_title), "Testing");
        solo.enterText((EditText) solo.getView(R.id.edit_reason), "4/4, please?");
        solo.clickOnView(solo.getView(R.id.edit_date));
        solo.setDatePicker(0, 2021, 10, 1);
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.satButton));
        solo.clickOnView(solo.getView(R.id.sunButton));
        solo.clickOnView(solo.getView(R.id.create_habit_button));
    }

    /**
     * Helper method to delete a habit
     */
    private void deleteHabit() {
        solo.clickOnView(solo.getView(R.id.card_view_delete));
        solo.clickOnView(solo.getView(R.id.deleteHabitButton));
    }

    /**
     * Helper method to log in with correct entries
     */
    private void login() {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "testUI1@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "test12345");
        solo.clickOnButton("Login");
    }

    /**
     * Simple test case to verify if everything is okay.
     */
    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }

    /**
     * Checks if the custom back button works correctly.
     */
    @Test
    public void testBackButton() {
        // goes to the Activity
        login();
        addHabit();

        // goes to home, clicks on habits button and then click the newly added habit
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");
        solo.clickOnView(solo.getView(R.id.card_view_delete));

        // checks if it is in the DeleteHabitActivity
        solo.assertCurrentActivity("Wrong Activity!", DeleteHabitActivity.class);

        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.deleteHabitBack));
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);

        // delete habit
        deleteHabit();
    }

    /**
     * Checks to see if the delete button goes to the delete activity
     */
    @Test
    public void testDeleteButton() {
        // logs in with correct input and add habit
        login();
        addHabit();

        // goes to home then clicks home button
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");

        // checks to see that we are in the fragment
        solo.waitForText("Habits", 1, 2000);

        // checks to see that when the delete button is clicked, it goes to the delete habit activity
        solo.clickOnView(solo.getView(R.id.card_view_delete));
        solo.assertCurrentActivity("Wrong Activity!", DeleteHabitActivity.class);

        // check motivation, title, and other info
        solo.waitForText("Testing", 1, 2000);
        solo.waitForText("Motivation", 1, 2000);
        solo.waitForText("4/4, please?", 1, 2000);
        solo.waitForText("Are you sure you want to delete this habit?", 1, 2000);

        // delete the habit
        solo.clickOnView(solo.getView(R.id.deleteHabitButton));

        // check if it went to previous activity
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
