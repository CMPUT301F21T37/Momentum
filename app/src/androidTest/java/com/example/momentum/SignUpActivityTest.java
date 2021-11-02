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

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void TestSignUpNoEntry(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    @Test
    public void TestSignUpCorrectEntry(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "user1");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "temp2@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "password1234");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "password1234");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void TestSignUpInvalidEmail(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "user1");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "qwerty");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "password1234");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "password1234");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    @Test
    public void TestSignUpInvalidPassword(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "user1");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "temp2@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "pas");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "pas");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    @Test
    public void TestSignUpMatchingPassword(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameSignUpScreen), "user1");
        solo.enterText((EditText) solo.getView(R.id.emailSignUpScreen), "temp2@ca.co");
        solo.enterText((EditText) solo.getView(R.id.passwordSignUpScreen), "password1234");
        solo.enterText((EditText) solo.getView(R.id.confirmPasswordSignUpScreen), "password12345");
        solo.clickOnButton("SIGN UP");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
