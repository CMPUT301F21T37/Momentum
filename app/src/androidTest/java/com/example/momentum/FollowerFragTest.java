package com.example.momentum;


import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
    private void login(Integer account) {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), ("s"+ account.toString() + "@gmail.com"));
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "12345678");
        solo.clickOnButton("Login");
    }
    private void change_acc(Integer account){
        solo.clickOnView(solo.getView(R.id.navigation_settings));
        solo.clickOnButton("Log out");
        login(account);
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
        login(0);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
    @Test
    public void Test_account_change(){
        login(0);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        change_acc(1);
        solo.clickOnView(solo.getView(R.id.navigation_settings));
        solo.waitForText("Sam1", 1, 2000);
    }
    @Test
    public void Test_add_request(){
        login(0);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        solo.clickOnButton("add");
        solo.enterText(0, "Sam1");
        solo.clickOnButton("Follow");
        change_acc(1);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        solo.waitForText("Sam0");
    }
    @Test
    public void Test_deny_request(){
        login(0);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        solo.clickOnButton("add");
        solo.enterText(0, "Sam1");
        solo.clickOnButton("Follow");
        change_acc(1);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        solo.clickOnView(solo.getButton(2));
    }
    @Test
    public void Test_accept_request(){
        login(0);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        solo.clickOnButton("add");
        solo.enterText(0, "Sam1");
        solo.clickOnButton("Follow");
        change_acc(1);
        solo.clickOnView(solo.getView(R.id.navigation_following));
        solo.clickOnView(solo.getButton(1));
        change_acc(0);
        solo.clickOnView(solo.getView(R.id.navigation_sharing));
        solo.waitForText("Sam1");

    }
    // Testing for following is very sparse as entering text into an alertdialog
    // seems to be impossible so testing is brought to a standstill as the reliability
    // of the follow requesting function is mandatory for further testing
    /**
     * Closes activities after tests are finished running
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }




}
