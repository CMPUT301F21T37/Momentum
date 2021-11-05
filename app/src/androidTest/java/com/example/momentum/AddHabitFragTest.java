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

public class AddHabitFragTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }
    private void login() {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "test12345");
        solo.clickOnButton("Login");
    }
    private void goToActivity() {
        solo.clickOnMenuItem("add");
    }
    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }


}
