package com.example.momentum;

import static com.example.momentum.R.id.motivationText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.HabitsEditActivity;
import com.example.momentum.habits.ViewHabitActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HabitsEditActivityTest {
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
    public void checkbackButton() {
        // goes to the Activity
        login();
        solo.clickOnButton("Habits");
        solo.clickOnView(solo.getView(R.id.card_view_edit));

        // checks if it is in the ViewHabitActivity
        solo.assertCurrentActivity("Wrong Activity!", HabitsEditActivity.class);

        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.viewHabitBack));
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
    }

    /**
     * Checks changes
     */
    @Test
    public void checkAllowableChanges() {
        // goes to the Activity
        login();

        // clicks an edit button and confirm switch
        solo.clickOnButton("Habits");
        solo.clickOnView(solo.getView(R.id.card_view_edit));
        solo.assertCurrentActivity("Wrong Activity!", HabitsEditActivity.class);

        // clears the title and checks the limit
        solo.clearEditText((EditText) solo.getView(R.id.habitTitleText));
        solo.enterText((EditText) solo.getView(R.id.AddHabitEventComment), "This is more than 20 characters.");
        // this is more than 20 characters, so it is false
        assertFalse(solo.waitForText("This is more than 20 characters.", 1, 2000));
        // this is exactly 20 characters, so it is true
        assertTrue(solo.waitForText("This is more than 20", 1, 2000));

        // clears the reason and checks the limit
        solo.clearEditText((EditText) solo.getView(R.id.habitTitleText));
        solo.enterText((EditText) solo.getView(R.id.AddHabitEventComment), "1234567890123456789012345678901");
        // this is more than 30 characters, so it is false
        assertFalse(solo.waitForText("1234567890123456789012345678901", 1, 2000));
        // this is exactly 30 characters, so it is true
        assertTrue(solo.waitForText("123456789012345678901234567890", 1, 2000));

        // checks if starting date can be changed
        solo.clickOnView(solo.getView(R.id.dateText));
        solo.waitForText("You cannot change your habit start date.", 1, 2000);

        // checks if buttons can be clicked
        solo.clickOnView(solo.getView(R.id.satButton));
        solo.clickOnView(solo.getView(R.id.sunButton));
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
