package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.HabitYearActivity;
import com.example.momentum.habits.ViewHabitActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HabitYearActivityTest {
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
     * Since the code is set up to only set a habit as done when it is the current day,
     * All habits are pre-added to the testUI account
     */
    private void login() {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "testUI@gmail.com");
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
        // logs in with correct input
        login();

        // goes to home, clicks on habits button and then click the newly added habit
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");
        solo.clickOnText("Testing3");

        // click the visual indicator button and make sure that it goes to HabitYearActivity
        solo.clickOnView(solo.getView(R.id.viewIndicatorButton));
        solo.assertCurrentActivity("Wrong Activity!", HabitYearActivity.class);

        // go back to view activity
        solo.clickOnView(solo.getView(R.id.yearHabitBack));
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitActivity.class);

    }

    /**
     * Checks to see if the HabitYearActivity is good UI-wise
     */
    @Test
    public void testYear() {
        // logs in with correct input
        login();

        // goes to home, clicks on habits button and then click the newly added habit
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");
        solo.clickOnText("Testing3");

        // click the visual indicator button and make sure that it goes to HabitYearActivity
        solo.clickOnView(solo.getView(R.id.viewIndicatorButton));
        solo.assertCurrentActivity("Wrong Activity!", HabitYearActivity.class);

        // look for texts
        solo.waitForText("Choose a Year", 1, 2000);
        solo.waitForText("2021", 1, 2000);
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
