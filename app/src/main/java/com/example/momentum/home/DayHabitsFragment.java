package com.example.momentum.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.databinding.FragmentDayHabitsBinding;

public class DayHabitsFragment extends Fragment {
    private DayHabitsViewModel DayHabitsViewModel;
    private FragmentDayHabitsBinding binding;
    private String dayofWeek;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DayHabitsViewModel =
                new ViewModelProvider(this).get(DayHabitsViewModel.class);

        binding = FragmentDayHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*
        receiving the data date to the day habits fragment
        https://www.youtube.com/watch?v=iVxKMZ8sGXY
        Author: Oum Saokosal
         */
        Bundle bundle = getArguments();
        if (bundle != null){
            dayofWeek = bundle.getString("dayOfWeek");
            String date_title = bundle.getString("dateTitle");
            MutableLiveData<String> date_title_mutable = new MutableLiveData<>();
            date_title_mutable.setValue(date_title);
            DayHabitsViewModel.setText(date_title_mutable);
            changeDayTitle();
        }

        return root;
    }

    public void changeDayTitle() {
        final TextView textView = binding.dayHabitTitle;
        DayHabitsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
