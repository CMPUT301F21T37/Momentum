package com.example.momentum.habits;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.databinding.ActivityViewHabitBinding;
import com.example.momentum.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * An activity that lets the user see their habits and corresponding details.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class ViewHabitActivity extends AppCompatActivity {

    private ActivityViewHabitBinding binding;
    private String title;
    private String reason;
    private ArrayList<?> frequency;
    private Boolean isPrivate;
    private Date date;

    private FloatingActionButton backButton;
    private Button visualIndicatorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityViewHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(Constants.HABIT_TITLE);
        reason = intent.getStringExtra(Constants.HABIT_REASON);
        isPrivate = intent.getBooleanExtra(Constants.HABIT_PRIVACY, true);
        date = (Date) intent.getSerializableExtra(Constants.HABIT_DATE);
        frequency = (ArrayList<?>) intent.getStringArrayListExtra(Constants.HABIT_FREQUENCY);

        // set the displays
        setTitle();
        setMotivation();
        setStartingDate();
        setPrivacy();
        setFrequency();

        // back button to go back to previous fragment
        backButton = binding.viewHabitBack;
        backButton.setOnClickListener(this::backButtonOnClick);

        // visual indicator button to view its visual indicator on another activity
        visualIndicatorButton = binding.viewIndicatorButton;
        visualIndicatorButton.setOnClickListener(this::visualIndicatorButtonOnClick);
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

    private boolean visualIndicatorButtonOnClick(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        Intent intent = new Intent(ViewHabitActivity.this, HabitYearActivity.class);
        intent.putExtra(Constants.HABIT_TITLE, title);
        intent.putExtra(Constants.VISUAL_INDICATOR_USER, uid);
        intent.putExtra(Constants.HABIT_FREQUENCY, frequency);
        startActivity(intent);
        return true;
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
