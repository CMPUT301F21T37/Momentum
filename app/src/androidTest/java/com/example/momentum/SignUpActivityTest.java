package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.login.LoginActivity;
import com.example.momentum.login.SignUpActivity;
import com.robotium.solo.Solo;

import org.junit.*;

public class SignUpActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SignUpActivity> rule =
            new ActivityTestRule<>(SignUpActivity.class, true, true);

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
     * This tests sign up button when user enters nothing
     */
    @Test
    public void TestSignUpNoEntry() {
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    /**
     * This tests sign up button when correct entry
     */
    @Test
    public void TestSignUpCorrectEntry() {
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "user1");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "temp2@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "password1234");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "password1234");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * This tests sign up button when an invalid email is entered
     */
    @Test
    public void TestSignUpInvalidEmail() {
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "someUsername");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "qwerty");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "password1234");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "password1234");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    /**
     * This tests sign up button when an invalid password is entered
     */
    @Test
    public void TestSignUpInvalidPassword() {
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "user1");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "temp2@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "pas");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "pas");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    /**
     * This tests sign up button when an passwords are not matching
     */
    @Test
    public void TestSignUpUnMatchingPassword() {
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "user1");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "temp2@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "password1234");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "password12345");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
