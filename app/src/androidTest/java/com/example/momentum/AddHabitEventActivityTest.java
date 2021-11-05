package com.example.momentum;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.home.AddHabitEventActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for AddHabitEventActivity. This also tests DayHabitsFragment functionalities since the activity is started from the fragment.
 */
public class AddHabitEventActivityTest {
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
     * Helper so to log in with correct entries to be able to test
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
        solo.clickOnText("Coding");
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
        // goes to AddHabitEventActivity
        login();
        goToActivity();

        // checks if it is in the AddHabitEventActivity
        solo.assertCurrentActivity("Wrong Activity!", AddHabitEventActivity.class);

        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.addHabitEventBack));
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
    }

    /**
     * Checks if the comment limit is working.
     */
    @Test
    public void checkCommentLimit() {
        // goes to AddHabitEventActivity
        login();
        goToActivity();

        // checks if it is in the AddHabitEventActivity
        solo.assertCurrentActivity("Wrong Activity!", AddHabitEventActivity.class);

        // checks the comment limit to 20 characters
        solo.enterText((EditText) solo.getView(R.id.AddHabitEventComment), "This is more than 20 characters.");
        // this is more than 20 characters, so it is false
        assertFalse(solo.waitForText("This is more than 20 characters.", 1, 2000));
        // this is exactly 20 characters, so it is true
        assertTrue(solo.waitForText("This is more than 20", 1, 2000));
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
