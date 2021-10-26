package com.example.momentum.home;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.R;
import com.example.momentum.databinding.FragmentHomeBinding;

import java.util.Calendar;

public class HomeFragment extends Fragment {
    private HomeViewModel HomeViewModel;
    private FragmentHomeBinding binding;
    private CalendarView calendar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendar = binding.calendarView;
        calendar.setOnDateChangeListener(this::onDateClick);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
