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

public class HomeFragment extends Fragment {
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        // get the day of week for database query
        String dayOfWeek = (String) DateFormat.format("EEEE", calendar);

        // get the complete date
        String month_str = (String) DateFormat.format("MMM", calendar.getTime());
        String date_title =  month_str + " " + day + " " + year + " Habit";

        /*
        passing the data date to the day habits fragment via Bundle
        https://www.youtube.com/watch?v=iVxKMZ8sGXY
        Author: Oum Saokosal
         */
        Bundle bundle = new Bundle();
        bundle.putString("dateTitle", date_title);
        bundle.putString("dayOfWeek", dayOfWeek);

        /*
        Android Developer Documentation
        using fragment transactions: https://developer.android.com/guide/fragments/transactions
         */
        Fragment fragment = new DayHabitsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        return true;
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
