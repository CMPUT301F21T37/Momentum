package com.example.momentum;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.login.LoginActivity;
import com.example.momentum.login.SignUpActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * This method runs before all tests and create a solo instance.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * This method/test gets the activity
     */
    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }

    /**
     * This tests login button when user enters nothing
     */
    @Test
    public void checkLoginNoEntry() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Login");
        assertTrue(solo.waitForActivity(LoginActivity.class));
    }

    /**
     * This tests login button when correct entry (email and password are correct too)
     */
    @Test
    public void checkLoginCorrectEntry() {
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "temp@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "temp12");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForActivity(MainActivity.class));
    }

    /**
     * This tests if an incorrect password is entered
     */
    @Test
    public void checkLoginIncorrectPassword() {
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "temp@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "temp123");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForActivity(LoginActivity.class));
    }

    /**
     * This tests if an incorrect email is entered
     */
    @Test
    public void checkLoginIncorrectEmail() {
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "temp@ca.po");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "temp12");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForActivity(LoginActivity.class));
    }

    /**
     * This method tests the redirection to the sign up screen
     */
    @Test
    public void testChangeToSignUpScreen() {
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);
        solo.clickOnText("Sign Up.");
        assertTrue(solo.waitForActivity(SignUpActivity.class));
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
