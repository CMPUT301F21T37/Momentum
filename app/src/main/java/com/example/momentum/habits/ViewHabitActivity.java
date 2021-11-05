package com.example.momentum.habits;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.databinding.ActivityViewHabitBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

/**
 * An activity that lets the user see their habits and corresponding details.
 * @author: Kaye Ena Crayzhel F. Misay
 */
public class ViewHabitActivity extends AppCompatActivity {
    private ActivityViewHabitBinding binding;
    private String title;
    private String reason;
    private ArrayList<?> frequency;
    private Boolean isPrivate;
    private Date date;
    private FloatingActionButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityViewHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitsFragment.HABIT_TITLE);
        reason = intent.getStringExtra(HabitsFragment.HABIT_REASON);
        isPrivate = intent.getBooleanExtra(HabitsFragment.HABIT_PRIVACY, true);
        date = (Date) intent.getSerializableExtra(HabitsFragment.HABIT_DATE);
        frequency = (ArrayList<?>) intent.getStringArrayListExtra(HabitsFragment.HABIT_FREQUENCY);

        // set the displays
        setTitle();
        setMotivation();
        setStartingDate();
        setPrivacy();
        setFrequency();

        // back button to go back to previous fragment
        backButton = binding.viewHabitBack;
        backButton.setOnClickListener(this::backButtonOnClick);
    }

    /**
     * Helper method to set the title habit title
     */
    private void setTitle() {
        TextView activityTitle;
        activityTitle = binding.ViewHabitTitle;
        activityTitle.setText(title);
    }

    /**
     * Helper method to set the motivation/reason
     */
    private void setMotivation() {
        TextView motivation;
        motivation = binding.motivationText;
        motivation.setText(reason);
    }

    /**
     * Helper method to set the starting date
     */
    private void setStartingDate() {
        String month = (String) DateFormat.format("MMMM", date);
        String day = (String) DateFormat.format("dd", date);
        String year = (String) DateFormat.format("yyyy", date);
        String str_date = month + " " + day + ", " + year;

        TextView startingDate = binding.dateText;
        startingDate.setText(str_date);
    }

    /**
     * Helper method to set the habit privacy
     */
    private void setPrivacy() {
        TextView privacy;
        privacy = binding.privacyText;

        if (isPrivate) {
            privacy.setText("Private");
        }
        else {
            privacy.setText("Public");
        }
    }

    /**
     * Helper method to set frequency
     */
    private void setFrequency() {
        TextView frequencyView;
        frequencyView = binding.FrequencyText;
        String frequency_str = "";

        // getting the frequencies in string format
        if (frequency.size() == 7) {
            frequency_str = "Everyday";
        }
        else {
            for (int i = 0; i < frequency.size(); i++) {
                if (i == 0) {
                    frequency_str = (String) frequency.get(i);
                }
                else {
                    frequency_str = frequency_str + ", " + (String) frequency.get(i);
                }
            }
        }

        frequencyView.setText(frequency_str);
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
