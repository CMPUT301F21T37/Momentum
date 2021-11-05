package com.example.momentum.habitEvents;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.R;
import com.example.momentum.databinding.FragmentHabitEventsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * An activity that lets the user see their habit events and corresponding details.
 * @author: Kaye Ena Crayzhel F. Misay, Han Yan
 */

public class ViewHabitEventsActivity extends AppCompatActivity {
    private FragmentHabitEventsBinding binding;
    private String title;
    private String reason;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indiv_habit_event_view);

        // creates the activity view
        binding = FragmentHabitEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitEventsFragment.EVENT_TITLE);
        reason = intent.getStringExtra(HabitEventsFragment.EVENT_COMMENT);


        // set the displays
        setTitle();
        setMotivation();

    }

    /**
     * Helper method to set the title habit title
     */
    private void setTitle() {
        TextView activityTitle;
        activityTitle = findViewById(R.id.habit_event);
        activityTitle.setText(title);
    }

    /**
     * Helper method to set the motivation/reason
     */
    private void setMotivation() {
        TextView motivation;
        motivation = findViewById(R.id.comment);
        motivation.setText(reason);
    }


}
