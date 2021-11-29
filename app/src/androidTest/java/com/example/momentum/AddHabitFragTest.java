package com.example.momentum;

import static org.junit.Assert.assertTrue;

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
 * Test class for the Add Habit Fragment
 */
public class AddHabitFragTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Vars for easy editing and testing of titles
     */
    public String Valid_title = "TestTitle";
    public String Valid_reason = "TestReason";
    public String Valid_date = "31/12/2021";
    public String Invalid_date = "invalid_date";

    /**
     * This method runs before all tests and create a solo instance.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Helper  method that logs into the application on
     * the test account and then navigates to the Add Habit Frag
     * that is being tested.
     */
    private void GoToAddFrag() {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "s@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "12345678");
        solo.clickOnButton("Login");
        solo.clickOnView(solo.getView(R.id.navigation_add_habit));
    }

    /**
     * Helper method that automatically fills habit parameters
     * in the fragment with the given variables
     * @param title
     * @param reason
     * @param date
     */
    private void fill_habit(String title, String reason, String date){
        solo.enterText((EditText) solo.getView((R.id.edit_title)), title);
        solo.enterText((EditText) solo.getView((R.id.edit_reason)), reason);
        solo.enterText((EditText) solo.getView((R.id.edit_date)), date);
        solo.clickOnButton("Cancel");
    }

    /**
     * Verification Test that program is working
     */
    @Test
    public void Test_start() {
        Activity activity = rule.getActivity();
    }

    /**
     * Tests that the fragment is being navigated to correctly
     */
    @Test
    public void Test_gotoAddHabit(){
        GoToAddFrag();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * tests that a habit is created when given valid parameters
     */
    @Test
    public void habit_creation(){
        GoToAddFrag();
        fill_habit(Valid_title, Valid_reason, Valid_date);
        solo.waitForText("Habit Created",
                1, 2000);
    }

    /**
     * tests that no habit is created when an empty title is given
     */
    @Test
    public void empty_title(){
        GoToAddFrag();
        fill_habit("", Valid_reason, Valid_date);
        solo.clickOnButton("Create Habit");
        solo.waitForText("Title Required",
                1, 2000);
    }

    /**
     * tests that no habit is created when an invalid date is given
     */
    @Test
    public void bad_date(){
        GoToAddFrag();
        fill_habit(Valid_title, Valid_reason, Invalid_date);
        solo.clickOnButton("Create Habit");
        solo.waitForText("Invalid Date Format Entered",
                1, 2000);
    }

    /**
     * tests functionality of the toggle buttons and switch
     */
    @Test
    public void toggle_buttons() {
        GoToAddFrag();
        solo.clickOnButton("Mon");
        solo.clickOnButton("Tue");
        solo.clickOnButton("Wed");
        solo.clickOnButton("Thu");
        solo.clickOnButton("Fri");
        solo.clickOnButton("Sat");
        solo.clickOnButton("Sun");
        solo.clickOnButton("Private");
        assertTrue(solo.isToggleButtonChecked("Mon"));
        assertTrue(solo.isToggleButtonChecked("Tue"));
        assertTrue(solo.isToggleButtonChecked("Wed"));
        assertTrue(solo.isToggleButtonChecked("Thu"));
        assertTrue(solo.isToggleButtonChecked("Fri"));
        assertTrue(solo.isToggleButtonChecked("Sat"));
        assertTrue(solo.isToggleButtonChecked("Sun"));

    }

    /**
     * tests that calendar is a functioning tool to select a valid date for a habit
     */
    @Test
    public void calendar(){
        GoToAddFrag();
        fill_habit(Valid_title, Valid_reason, "");
        solo.enterText((EditText) solo.getView((R.id.edit_date)), Invalid_date);
        solo.clickOnButton("Mon");
        solo.clickOnButton("OK");
        solo.clickOnButton("Create Habit");
        solo.waitForText("Habit Created",
                1, 2000);
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
