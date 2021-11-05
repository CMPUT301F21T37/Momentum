package com.example.momentum;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.MainActivity;
import com.example.momentum.R;
import com.example.momentum.habitEvents.Indiv_habitEvent_view;
import com.example.momentum.home.DayHabitsActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class Habit_EventsTest extends TestCase {
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
        solo.clickOnButton("Habit Events");
        solo.waitForText("Habit Events", 1, 2000);

    }
    /**
     * Simple test cast to verify if everything is okay.
     */
    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }

    /**
     * Checks if the list of habit event work correctly
     */
    @Test
    public void testListView(){
        // logs in with correct test inputs and go to the habit event activity
        login();
        goToActivity();
        //check the List of habit event works
        solo.clickOnView(solo.getView(R.id.habit_event_listView));
        // checks that we are in Indiv_habitEvent_view
        solo.assertCurrentActivity("Wrong Activity!", Indiv_habitEvent_view.class);
        assertTrue(solo.waitForText("Exercise", 1, 2000));
    }


    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}