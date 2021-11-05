package com.example.momentum;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.HabitsEditActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HabitsEventsEditActivity {
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
        solo.clickOnButton("Events");
        solo.clickOnView(solo.getView(R.id.card_view_edit));

        // checks if it is in the activity
        solo.assertCurrentActivity("Wrong Activity!", HabitsEventsEditActivity.class);

        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.viewEventBack));
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);
    }

    /**
     * Checks changes
     */
    @Test
    public void checkEdit() {
        // goes to the Activity
        login();

        // clicks an edit button and confirm switch
        solo.clickOnButton("Events");
        solo.clickOnView(solo.getView(R.id.card_view_edit));
        solo.assertCurrentActivity("Wrong Activity!", HabitsEventsEditActivity.class);

        // clears the title and checks the limit
        solo.clearEditText((EditText) solo.getView(R.id.motivationText));
        // checks the comment limit to 20 characters
        solo.enterText((EditText) solo.getView(R.id.motivationText), "This is more than 20 characters.");
        // this is more than 20 characters, so it is false
        assertFalse(solo.waitForText("This is more than 20 characters.", 1, 2000));
        // this is exactly 20 characters, so it is true
        assertTrue(solo.waitForText("This is more than 20", 1, 2000));

        // checks if goes back to main activity
        solo.clickOnView(solo.getView(R.id.editConfirmButton));
        solo.waitForText("Events", 1, 2000);


    }
}
