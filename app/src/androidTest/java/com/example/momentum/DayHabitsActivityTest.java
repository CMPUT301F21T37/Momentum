package com.example.momentum;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.home.DayHabitsActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for DayHabitsActivity. This also tests DayHabitsFragment functionalities since the activity is started from the fragment.
 */
public class DayHabitsActivityTest {
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
     * Checks if the custom back button works correctly.
     */
    @Test
    public void backButton() {
        // goes to DayHabitsActivity
        login();
        goToActivity();

        // checks if it is in the DayHabitsActivity
        solo.assertCurrentActivity("Wrong Activity!", DayHabitsActivity.class);

        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.dayHabitsBack));
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
