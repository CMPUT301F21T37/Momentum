package com.example.momentum.add;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.Habit;
import com.example.momentum.MainActivity;
import com.example.momentum.R;
import com.example.momentum.databinding.FragmentAddHabitBinding;
import com.example.momentum.login.SignUpActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFragment extends Fragment{
    private AddViewModel AddViewModel;
    private FragmentAddHabitBinding binding;

    EditText title;
    EditText reason;
    EditText date;
    ToggleButton mon;
    ToggleButton tue;
    ToggleButton wed;
    ToggleButton thu;
    ToggleButton fri;
    ToggleButton sat;
    ToggleButton sun;
    Switch privacy;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddHabitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        title = binding.editTitle;
        reason = binding.editReason;
        date = binding.editDate;
        mon = binding.monButton;
        tue = binding.tueButton;
        wed = binding.wedButton;
        thu = binding.thuButton;
        fri = binding.friButton;
        sat = binding.satButton;
        sun = binding.sunButton;
        privacy = binding.habitPrivacy;
        Activity activity = getActivity();
        final Button create_button = binding.createHabitButton;
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().length() > 20){
                    Log.w(TAG, "Title too long");
                    Toast.makeText(activity, "Title must be less than 20 chars",
                            Toast.LENGTH_SHORT).show();
                }
                else if (reason.getText().toString().length() > 30){
                    Log.w(TAG, "Reason too long");
                    Toast.makeText(activity, "Reason must be less than 30 chars",
                            Toast.LENGTH_SHORT).show();
                }
                else if(title == null || title.getText().toString().equals("")){
                    Log.w(TAG, "No Title Error");
                    Toast.makeText(activity, "Title Required",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        String t = title.getText().toString();
                        String r = reason.getText().toString();
                        Date d = null;
                        d = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
                        Boolean[] f = {mon.isChecked(), tue.isChecked(), wed.isChecked(), thu.isChecked(), fri.isChecked(), sat.isChecked(), sun.isChecked()};
                        Boolean p = privacy.isChecked();
                        Habit habit = new Habit(t, r, d, p, f);
                        Log.w(TAG, "Habit_created");
                        Log.w(TAG, habit.toString());
                    }
                    catch (ParseException e){
                        Log.w(TAG, "Date Parse Error");
                        Toast.makeText(activity, "Invalid Date Format Entered", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
