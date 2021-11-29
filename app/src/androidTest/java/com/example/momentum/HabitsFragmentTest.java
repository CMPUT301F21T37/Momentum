package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.DeleteHabitActivity;
import com.example.momentum.habits.HabitsEditActivity;
import com.example.momentum.habits.ViewHabitActivity;
import com.example.momentum.home.DayHabitsActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HabitsFragmentTest {
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
     * Helper method to add another habit
     * title: Testing2
     * reason: for more testing!
     * Date: November 1, 2021
     * Frequency: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
     * Privacy: Default (Public)
     */
    private void addHabit2() {
        solo.clickOnView(solo.getView(R.id.navigation_add_habit));
        solo.enterText((EditText) solo.getView(R.id.edit_title), "Testing2");
        solo.enterText((EditText) solo.getView(R.id.edit_reason), "for more testing!");
        solo.clickOnView(solo.getView(R.id.edit_date));
        solo.setDatePicker(0, 2021, 10, 1);
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.monButton));
        solo.clickOnView(solo.getView(R.id.tueButton));
        solo.clickOnView(solo.getView(R.id.wedButton));
        solo.clickOnView(solo.getView(R.id.thuButton));
        solo.clickOnView(solo.getView(R.id.friButton));
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
     * Checks to see that the habits that were added exists there
     */
    @Test
    public void testHabitsView() {
        // goes to the Activity and adds two habits
        login();
        addHabit();
        solo.clickOnView(solo.getView(R.id.navigation_home));
        addHabit2();

        // goes to home then clicks home button
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");

        // checks to see that we are in the Habits Fragment
        solo.waitForText("Habits", 1, 2000);

        // checks to see if the two added habits are there
        solo.waitForText("Testing", 1, 2000);
        solo.waitForText("Testing2", 1, 2000);

        // delete these habits
        deleteHabit();
        deleteHabit();
    }

    /**
     * Checks to see that if the edit button goes to the edit activity
     */
    @Test
    public void testEditButton() {
        // logs in with correct input and add habit
        login();
        addHabit();

        // goes to home then clicks home button
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");

        // checks to see that we are in the fragment
        solo.waitForText("Habits", 1, 2000);

        // checks to see that when the edit button is clicked, it goes to the edit habit activity
        solo.clickOnView(solo.getView(R.id.card_view_edit));
        solo.assertCurrentActivity("Wrong Activity!", HabitsEditActivity.class);

        // delete the habit
        solo.clickOnView(solo.getView(R.id.editHabitBack));
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

        // delete the habit
        solo.clickOnView(solo.getView(R.id.deleteHabitButton));
    }

    /**
     * Checks to see if clicking the habit will go to the ViewHabitActivity
     */
    @Test
    public void testHabitView() {
        // logs in with correct input and add habit
        login();
        addHabit();

        // goes to home then clicks home button
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");

        // checks to see that we are in the fragment
        solo.waitForText("Habits", 1, 2000);

        // checks to see that it is on the ViewHabitActivity
        solo.clickOnView(solo.getView(R.id.card_view_edit_delete));
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitActivity.class);

        // delete the habit
        solo.clickOnView(solo.getView(R.id.viewHabitBack));
        deleteHabit();
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
