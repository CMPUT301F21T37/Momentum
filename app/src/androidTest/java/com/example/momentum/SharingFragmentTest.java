package com.example.momentum;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.momentum.home.AddHabitEventActivity;
import com.example.momentum.login.LoginActivity;
import com.example.momentum.sharing.SharingHabitActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SharingFragmentTest {
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
     * Simple test cast to verify if everything is okay.
     */
    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }

    /**
     * Checks to see that all following are there
     * It is logged in into the group's test account which will have time、Lily5
     */
    @Test
    public void testSharingFragment() {
        // logs in with correct test inputs
        login();

        // clicks on the button to go to SharingFragment
        solo.clickOnView(solo.getView(R.id.navigation_sharing));

        // checks to see that we are in the fragment
        solo.waitForText("time", 1, 2000);

        // checks to see that we are in the fragment
        solo.waitForText("Lily5", 1, 2000);
    }

    @Test
    public void testSearchView() {
        // logs in with correct test inputs
        login();

        // clicks on the button to go to SharingFragment
        solo.clickOnView(solo.getView(R.id.navigation_sharing));

        solo.clickOnView(solo.getView(R.id.search_bar));
        solo.sleep(1000);

//        SearchView search = (SearchView) solo.getView(R.id.search_bar);
//        int id = search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//        EditText textView = (EditText) search.findViewById(id);
//        solo.enterText(textView, "Lily5");

        // checks to see that we are in the fragment
        assertTrue(solo.waitForText("Lily5", 1, 2000));

        solo.sleep(2000);
    }


    /**
     * Closes all activities after tests are done
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
