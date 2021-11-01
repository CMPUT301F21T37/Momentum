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

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkLoginNoEntry(){
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Login");
        assertTrue(solo.waitForActivity(LoginActivity.class));
    }
    @Test
    public void checkLoginCorrectEntry(){
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText),"temp@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"temp12");
        solo.clickOnButton("Login");

        assertTrue(solo.waitForActivity(MainActivity.class));
    }

    @Test
    public void checkLoginIncorrectPassword(){
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText),"temp@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"temp123");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForActivity(LoginActivity.class));
    }

    @Test
    public void checkLoginIncorrectEmail(){
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText),"temp@ca.po");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"temp12");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForActivity(LoginActivity.class));
    }

    @Test
    public void testChangeToSignUpScreen(){
        solo.assertCurrentActivity("Wrong Activity!", LoginActivity.class);
        solo.clickOnText("Sign Up.");
        assertTrue(solo.waitForActivity(SignUpActivity.class));
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
