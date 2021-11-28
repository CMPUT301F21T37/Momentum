package com.example.momentum;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.HabitYearActivity;
import com.example.momentum.login.LoginActivity;
import com.example.momentum.sharing.SharingHabitActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SharingHabitActivityTest {
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

    @Test
    public void testSharingHabitActivity() {
        // logs in with correct test inputs
        login();

        // clicks on the button to go to SharingFragment
        solo.clickOnView(solo.getView(R.id.navigation_sharing));

        // clicks on the Lily5 to go to SharingHabitActivity
        solo.clickOnText("Lily5");

        // checks to see that we are in the fragment
        assertTrue(solo.waitForText("Lily5", 1, 2000));
        assertTrue(solo.waitForText("uid:swRCN2iECcSh9H3EppXvo6hg3hN2", 1, 2000));
        assertTrue(solo.waitForText("My first Habit", 1, 2000));

        // checks if it is in the AddHabitEventActivity
        solo.assertCurrentActivity("Wrong Activity!", SharingHabitActivity.class);
        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.back));
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);

        solo.sleep(2000);
    }

    @Test
    public void testGotoHabitYearActivity() {
        // logs in with correct test inputs
        login();

        // clicks on the button to go to SharingFragment
        solo.clickOnView(solo.getView(R.id.navigation_sharing));

        // clicks on the Lily5 to go to SharingHabitActivity
        solo.clickOnText("Lily5");

        // clicks on the My first Habit to go to HabitYearActivity
        solo.clickOnText("My first Habit");

        // checks if it is in the AddHabitEventActivity
        solo.assertCurrentActivity("Wrong Activity!", HabitYearActivity.class);

        // checks to see that we are in the activity
        assertTrue(solo.waitForText("Choose a Year", 1, 2000));
        assertTrue(solo.waitForText("2021", 1, 2000));

        solo.sleep(2000);
    }
}
