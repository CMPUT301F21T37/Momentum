package com.example.momentum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.habits.HabitYearActivity;
import com.example.momentum.habits.ViewHabitActivity;
import com.example.momentum.home.DayHabitsActivity;
import com.example.momentum.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ViewHabitActivityTest {
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
     * Helper method to log in with correct entries
     */
    private void login() {
        solo.enterText((EditText) solo.getView(R.id.emailAddressEditText), "testUI5@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "kaynzhell19");
        solo.clickOnButton("Login");
    }

    /**
     * Helper method to add a habit
     * Adding a habit with
     * title:Testing
     * reason: 4/4, please?
     * Date: November 1, 2021
     * Frequency: Saturday, Sunday
     * Privacy: Default (Public)
     */
    private void addHabit() {
        solo.clickOnView(solo.getView(R.id.navigation_add_habit));
        solo.enterText((EditText) solo.getView(R.id.edit_title), "Testing");
        solo.enterText((EditText) solo.getView(R.id.edit_reason), "4/4, please?");
        solo.clickOnView(solo.getView(R.id.edit_date));
        solo.setDatePicker(0, 2021, 10, 1);
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.satButton));
        solo.clickOnView(solo.getView(R.id.sunButton));
        solo.clickOnView(solo.getView(R.id.create_habit_button));
    }

    /**
     * Helper method to add another habit
     * title: Testing2
     * reason: for more testing!
     * Date: November 1, 2021
     * Frequency: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
     * Privacy: Default (Public)
     */
    private void addHabit2() {
        solo.clickOnView(solo.getView(R.id.navigation_add_habit));
        solo.enterText((EditText) solo.getView(R.id.edit_title), "Testing2");
        solo.enterText((EditText) solo.getView(R.id.edit_reason), "for more testing!");
        solo.clickOnView(solo.getView(R.id.edit_date));
        solo.setDatePicker(0, 2021, 10, 1);
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.monButton));
        solo.clickOnView(solo.getView(R.id.tueButton));
        solo.clickOnView(solo.getView(R.id.wedButton));
        solo.clickOnView(solo.getView(R.id.thuButton));
        solo.clickOnView(solo.getView(R.id.friButton));
        solo.clickOnView(solo.getView(R.id.satButton));
        solo.clickOnView(solo.getView(R.id.sunButton));
        solo.clickOnView(solo.getView(R.id.create_habit_button));
    }

    /**
     * Helper method to delete a habit
     */
    private void deleteHabit() {
        solo.clickOnView(solo.getView(R.id.card_view_delete));
        solo.clickOnView(solo.getView(R.id.deleteHabitButton));
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
    public void testBackButton() {
        // goes to the Activity
        login();
        addHabit();

        // goes to home, clicks on habits button and then click the newly added habit
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");
        solo.clickOnText("Testing");

        // checks if it is in the ViewHabitActivity
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitActivity.class);

        // clicks on the back button and checks if it went to previous activity
        solo.clickOnView(solo.getView(R.id.viewHabitBack));
        solo.assertCurrentActivity("Wrong Activity!", MainActivity.class);

        // delete the habit
        deleteHabit();
    }

    /**
     * Checks if it shows correct info given new Data
     */
    @Test
    public void testConsistentInput() {
        // goes to the Activity
        login();
        addHabit();

        // goes to home, clicks on button and checks if the habit Testing is there
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");
        solo.waitForText("Testing", 1, 2000);

        // clicks the habit and checks if all it shows consistency
        solo.clickOnText("Testing");
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitActivity.class);
        solo.waitForText("Testing", 1, 2000);
        solo.waitForText("Saturday, Sunday", 1, 2000);
        solo.waitForText("November 01, 2021", 1, 2000);
        solo.waitForText("Public");
        solo.waitForText("4/4, please?");

        // click the visual indicator button and make sure that it goes to HabitYearActivity
        solo.clickOnView(solo.getView(R.id.viewIndicatorButton));
        solo.assertCurrentActivity("Wrong Activity!", HabitYearActivity.class);

        // go back to edit activity
        solo.clickOnView(solo.getView(R.id.yearHabitBack));

        // delete testing habit
        solo.clickOnView(solo.getView(R.id.viewHabitBack));
        deleteHabit();

        // add another habit
        addHabit2();

        // goes to home, clicks on button and checks if the habit Testing2 is there
        solo.clickOnView(solo.getView(R.id.navigation_home));
        solo.clickOnButton("Habits");
        solo.waitForText("Testing2", 1, 2000);

        // clicks the habit and checks if all it shows consistency
        // if all frequencies are chosen, it should show Everyday
        solo.clickOnText("Testing2");
        solo.assertCurrentActivity("Wrong Activity!", ViewHabitActivity.class);
        solo.waitForText("Testing2", 1, 2000);
        solo.waitForText("Everyday", 1, 2000);
        solo.waitForText("November 01, 2021", 1, 2000);
        solo.waitForText("Public");
        solo.waitForText("for more testing!");

        // click the visual indicator button and make sure that it goes to HabitYearActivity
        solo.clickOnView(solo.getView(R.id.viewIndicatorButton));
        solo.assertCurrentActivity("Wrong Activity!", HabitYearActivity.class);

        // go back to view activity
        solo.clickOnView(solo.getView(R.id.yearHabitBack));

        // delete testing2 habit
        solo.clickOnView(solo.getView(R.id.viewHabitBack));
        deleteHabit();
    }

    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}