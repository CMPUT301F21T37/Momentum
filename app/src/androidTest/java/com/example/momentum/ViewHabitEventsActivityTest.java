package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habitEvents.ViewHabitEventsActivity;
import com.example.momentum.habits.ViewHabitActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ViewHabitEventsActivityTest {
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
     * Checks if the custom back button works correctly.
     */
    @Test
    public void backButton() {
        // goes to the Activity
        login();
        solo.clickOnButton("Events");
        solo.clickOnText("Coding: November 4, 2021");

        // checks if it is in the activity
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitEventsActivity.class);

        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.viewEventBack));
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
    }

    /**
     * Checks if it shows correct info given previous data
     */
    @Test
    public void checkCorrectInfo() {
        // goes to the Activity
        login();
        solo.clickOnButton("Events");
        solo.clickOnText("Coding: November 04, 2021");

        // checks if it is in the ViewHabitActivity
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitEventsActivity.class);

        // waits for texts to appear
        solo.waitForText("Coding: November 04, 2021", 1, 2000);
        solo.waitForText("hacker", 1, 2000);
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
