package com.example.momentum.habitEvents;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.momentum.databinding.ActivityIndivHabitEventViewBinding;
import com.example.momentum.databinding.ActivityViewHabitBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * An activity that lets the user see their habit events and corresponding details.
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 */
public class ViewHabitEventsActivity extends AppCompatActivity {
    private ActivityIndivHabitEventViewBinding binding;
    private String title;
    private String reason;
    private FloatingActionButton backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityIndivHabitEventViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitEventsFragment.EVENT_TITLE);
        reason = intent.getStringExtra(HabitEventsFragment.EVENT_COMMENT);


        // set the displays
        setTitle();
        setComment();

        // back button to go back to previous fragment
        backButton = binding.viewEventBack;
        backButton.setOnClickListener(this::backButtonOnClick);

    }

    /**
     * Helper method to set the title habit title
     */
    private void setTitle() {
        TextView activityTitle;
        activityTitle = binding.habitEvent;
        activityTitle.setText(title);
    }

    /**
     * Helper method to set the motivation/reason
     */
    private void setComment() {
        TextView motivation;
        motivation = binding.comment;
        motivation.setText(reason);
    }

    /**
     * Callback handler for when the back button is clicked.
     * Goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean backButtonOnClick(View view) {
        finish();
        return true;
    }


}
