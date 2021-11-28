package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.HabitYearActivity;
import com.example.momentum.habits.VisualIndicatorActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class VisualIndicatorActivityTest {
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

        // click 2021 to go to VisualIndicatorActivity
        solo.clickOnText("2021");
        solo.assertCurrentActivity("Wrong Activity!", VisualIndicatorActivity.class);

        // click the back button to go back to previous activity
        solo.clickOnView(solo.getView(R.id.yearHabitBack));
        solo.assertCurrentActivity("Wrong Activity!", HabitYearActivity.class);
    }

    /**
     * Checks to see if the visual indicator activity works
     * In the testUI profile,
     * Testing3 has frequencies Wednesday, Saturday, Sunday
     * Done dates for November: 1
     * Done dates for December: 1
     */
    @Test
    public void testVisualization() {
        // logs in with correct input
        login();

        // goes to home, clicks on habits button and then click the newly added habit
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");
        solo.clickOnText("Testing3");

        // click the visual indicator button and make sure that it goes to HabitYearActivity
        solo.clickOnView(solo.getView(R.id.viewIndicatorButton));
        solo.assertCurrentActivity("Wrong Activity!", HabitYearActivity.class);

        // click 2021 to go to VisualIndicatorActivity
        solo.clickOnText("2021");
        solo.assertCurrentActivity("Wrong Activity!", VisualIndicatorActivity.class);

        // wait for texts to make sure
        solo.waitForText("Testing3", 1, 2000);
        solo.waitForText("January", 1, 2000);
        solo.waitForText("February", 1, 2000);
        solo.waitForText("March", 1, 2000);
        solo.waitForText("April", 1, 2000);
        solo.waitForText("May", 1, 2000);
        solo.waitForText("June", 1, 2000);
        solo.waitForText("July", 1, 2000);
        solo.waitForText("August", 1, 2000);
        solo.waitForText("September", 1, 2000);
        solo.waitForText("October", 1, 2000);
        solo.waitForText("November", 1, 2000);
        solo.waitForText("December", 1, 2000);

        /*
        In November, there are 11 possible dates with given frequencies ((1/11)*100) = 9.0909
        In December, there are 13 possible dates with given frequencies ((1/13)*100) = 7.6923
         */
        solo.waitForText("9.1%", 1, 2000);
        solo.waitForText("7.7%", 1, 2000);
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
