package com.example.momentum.home;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.Habit_Events;
import com.example.momentum.R;
import com.example.momentum.databinding.FragmentHomeBinding;

import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    public static final String DATE_TITLE_DAY_HABIT = "DATE_TITLE";
    public static final String DAY_OF_WEEK = "DAY_OF_WEEK";
    public static final String DATE_COMPARE_DAY_HABIT = "IS_DATE_CLICKED_CURRENT";
    public static final String DATE_CLICKED_DAY_HABIT = "DATE_CLICKED";
    public static final String DATE_CLICKED_DAY_HABIT_STR = "DATE_CLICKED_STR";

    private HomeViewModel HomeViewModel;
    private FragmentHomeBinding binding;
    private CalendarView calendar;
    private Button habitEvents;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // calendar view binding
        calendar = binding.calendarView;
        calendar.setOnDateChangeListener(this::onDateClick);

        // habit events binding
        habitEvents = binding.viewAllHabitEventsButton;
        habitEvents.setOnClickListener(this::onHabitEventsClick);

        return root;
    }

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

        /*
        Android Developer Documentation
        using fragment transactions: https://developer.android.com/guide/fragments/transactions
        Used to go to DayHabitsFragment
         */
        Fragment fragment = new DayHabitsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return true;
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

    private boolean onHabitEventsClick(View view) {
        Intent intent = new Intent(getContext(), Habit_Events.class);
        startActivity(intent);

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
