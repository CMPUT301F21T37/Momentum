package com.example.momentum;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.MainActivity;
import com.example.momentum.R;
import com.example.momentum.habitEvents.HabitsEventsEditActivity;
import com.example.momentum.habitEvents.ViewHabitEventsActivity;
import com.example.momentum.habits.HabitsEditActivity;
import com.example.momentum.habits.ViewHabitActivity;
import com.example.momentum.home.DayHabitsActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HabitEventsFragmentTest {
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
     * Checks to see that all events are there
     * It is logged in into the group's test account which will have Exercise, Coding, and Study in its events
     */
    @Test
    public void testEventsView() {
        // logs in with correct test inputs
        login();

        // clicks on the button to go to HabitsFragment
        solo.clickOnButton("Events");

        // checks to see that we are in the fragment
        solo.waitForText("Events", 1, 2000);

        // checks to see if the three habits are there
        solo.waitForText("Exercise", 1, 2000);
        solo.waitForText("Coding", 1, 2000);
        solo.waitForText("Study", 1, 2000);
    }


    /**
     * Checks to see that if the edit button goes to the edit activity
     */
    @Test
    public void testEditButton() {
        // logs in with correct test inputs
        login();

        // clicks on the button to go to HabitsFragment
        solo.clickOnButton("Events");

        // checks to see that we are in the fragment
        solo.waitForText("Events", 1, 2000);

        // checks to see that when the edit button is clicked, it goes to the edit habit activity
        solo.clickOnView(solo.getView(R.id.card_view_edit));
        solo.assertCurrentActivity("Wrong Activity!", HabitsEventsEditActivity.class);
    }

    /**
     * Checks to see if the delete button shows an alert dialog
     */
    @Test
    public void testDeleteButton() {
        // logs in with correct test inputs and adds test habit
        login();

        // clicks on the button to go to HabitsFragment
        solo.clickOnButton("Events");

        // checks to see that we are in the fragment
        solo.waitForText("Events", 1, 2000);

        // checks to see that when the delete button is clicked, an alert dialog shows up
        solo.clickOnView(solo.getView(R.id.card_view_delete));
        solo.waitForText("Are you sure you want to delete", 1, 2000);

        // clicks the cancel button and it should go back to the fragment
        solo.clickOnText("Cancel");
        solo.waitForText("Events", 1, 2000);
    }

    /**
     * Checks to see if clicking the event will go to the ViewEventsctivity
     */
    @Test
    public void testEventView() {
        // logs in with correct test inputs
        login();

        // clicks on the button to go to HabitsFragment
        solo.clickOnButton("Events");

        // checks to see that we are in the fragment
        solo.waitForText("Events", 1, 2000);

        // checks to see that it is on the ViewEventsActivity
        solo.clickOnView(solo.getView(R.id.card_view_edit_delete));
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitEventsActivity.class);
    }
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}