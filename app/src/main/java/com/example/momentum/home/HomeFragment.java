package com.example.momentum.home;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.momentum.habitEvents.HabitEventsFragment;
import com.example.momentum.R;
import com.example.momentum.databinding.FragmentHomeBinding;
import com.example.momentum.habits.HabitsFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * A fragment in MainActivity for Home functionality.
 * A user can choose to see their habits and habit events, and can click a calendar to see corresponding habits.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class HomeFragment extends Fragment {
    public static final String DATE_TITLE_DAY_HABIT = "DATE_TITLE";
    public static final String DAY_OF_WEEK = "DAY_OF_WEEK";
    public static final String DATE_COMPARE_DAY_HABIT = "IS_DATE_CLICKED_CURRENT";
    public static final String DATE_CLICKED_DAY_HABIT = "DATE_CLICKED";
    public static final String DATE_CLICKED_DAY_HABIT_STR = "DATE_CLICKED_STR";
    public static final String DATE_MONTH_CLICKED_DAY = "MONTH_CLICKED";
    public static final String START_DAY_HABIT_FRAGMENT = "DAY_HABIT_FRAGMENT";
    public static final String START_ALL_HABITS_VIEW_FRAGMENT = "HABITS_FRAGMENT";
    public static final String START_ALL_EVENTS_VIEW_FRAGMENT = "EVENTS_FRAGMENT";

    private FragmentHomeBinding binding;
    private CalendarView calendar;
    private Button habitEvents;
    private Button habits;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // binds the fragment to MainActivity and creates the view
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // calendar view binding
        calendar = binding.calendarView;
        calendar.setOnDateChangeListener(this::onDateClick);

        // habit events binding
        habitEvents = binding.viewAllHabitEventsButton;
        habitEvents.setOnClickListener(this::onHabitEventsClick);

        // habit binding
        habits = binding.viewAllHabitsButton;
        habits.setOnClickListener(this::onHabitsClick);

        return root;
    }

    /**
     * Callback handler for the listener of when a date in the calendar is clicked.
     * Handles the date conversions and data transfer to DayHabitsFragment.
     * @param calendarView
     * View of the calendar associated with the listener.
     * @param year
     * The year that was set.
     * @param month
     * The month that was set [0-11]
     * @param day
     * The day of the month that was set.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean onDateClick(CalendarView calendarView, int year, int month, int day) {
        // initialize Calendar format
        Calendar calendar_click = Calendar.getInstance();
        Calendar calendar_real = Calendar.getInstance();
        calendar_click.set(year, month, day);

        // get the day of week for database query
        String dayOfWeek = (String) DateFormat.format("EEEE", calendar_click);

        // get the complete date
        String month_str = (String) DateFormat.format("MMMM", calendar_click.getTime());
        String date_title =  month_str + " " + day + ", " + year + " Habits";
        String date = month_str + " " + day + ", " + year;

        // compare if selected date and current date is the same for DayHabitsActivity
        boolean isSame = compareDates(calendar_click, calendar_real);

        /*
        passing the data date to the day habits fragment via Bundle
        https://www.youtube.com/watch?v=iVxKMZ8sGXY
        Author: Oum Saokosal
         */
        Bundle bundle = new Bundle();
        bundle.putString(DATE_TITLE_DAY_HABIT, date_title);
        bundle.putString(DAY_OF_WEEK, dayOfWeek);
        bundle.putBoolean(DATE_COMPARE_DAY_HABIT, isSame);
        bundle.putSerializable(DATE_CLICKED_DAY_HABIT, calendar_click.getTime());
        bundle.putString(DATE_CLICKED_DAY_HABIT_STR, date);
        bundle.putString(DATE_MONTH_CLICKED_DAY, month_str);
        startDayHabitsFragment(bundle);
        return true;
    }

    /**
     * Helper method for onDateClick method to start the DayHabitsFragment and send the data bundle.
     * @param bundle
     * Consists of the data: date_title (String), dayOfWeek (String), isSame (boolean), clicked date (Date), clicked date (String)
     */
    private void startDayHabitsFragment(Bundle bundle) {
        /*
        Android Developer Documentation
        using fragment transactions: https://developer.android.com/guide/fragments/transactions
        Used to go to DayHabitsFragment
         */
        Fragment fragment = new DayHabitsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment, START_DAY_HABIT_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * It compares two dates if they are equal.
     * @param clicked_date
     * A Calendar instance of the clicked date by the user
     * @param current_date
     * A Calendar instance of the current date
     * @return
     * Returns either true or false of whether the two parameters are equal
     */
    private boolean compareDates(Calendar clicked_date, Calendar current_date) {
        Date date_click = clicked_date.getTime();
        Date date_current = current_date.getTime();

        if (date_current.compareTo(date_click) == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Callback handler for when the AllHabitEvents button is clicked
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean onHabitEventsClick(View view) {
        Fragment fragment = new HabitEventsFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment, START_ALL_EVENTS_VIEW_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
        return true;
    }

    /**
     * Callback handler for when the Habits button is clicked
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean onHabitsClick(View view) {
        Fragment fragment = new HabitsFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment, START_ALL_HABITS_VIEW_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
        return true;
    }

    /**
     * When view is destroyed, set binding to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
