package com.example.momentum;


import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.After;

/**
 * Test class for the Follower Fragment
 */
public class FollowerFragTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Helper  method that logs into the application on
     * the test account and then navigates to the Add Habit Frag
     * that is being tested.
     */
    private void GoToFollFrag() {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "s@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "12345678");
        solo.clickOnButton("Login");
        solo.clickOnView(solo.getView(R.id.navigation_following));
    }
    /**
     * Verification Test that program is working
     */
    @Test
    public void Test_start() {
        Activity activity = rule.getActivity();
    }
    @Test
    public void Test_follow_nav(){
        GoToFollFrag();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }


    /**
     * Closes activities after tests are finished running
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }




}
