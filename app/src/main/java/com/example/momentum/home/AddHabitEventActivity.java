package com.example.momentum.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.databinding.ActivityAddHabitEventBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddHabitEventActivity extends AppCompatActivity {
    private final String TAG = "ADD_HABIT_EVENT";

    private ActivityAddHabitEventBinding binding;
    private FloatingActionButton backButton;
    private FloatingActionButton checkButton;
    private FirebaseFirestore db;
    private String title;
    private String clickedDateStr;
    private TextView activityTitle;
    private EditText commentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddHabitEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(DayHabitsFragment.TITLE_DAY_HABIT);
        clickedDateStr = intent.getStringExtra(DayHabitsFragment.CLICKED_DATE_STR);

        // back button to go back to previous dayHabitsFragment
        backButton = binding.addHabitEventBack;
        backButton.setOnClickListener(this :: backButtonOnClick);

        // title of the current day habit for the habit event
        activityTitle = binding.AddHabitEventTitle;
        activityTitle.setText(title);

        // optional comment for the habit event
        commentField = binding.AddHabitEventComment;

        // add/check button to add the habit event
        checkButton = binding.addHabitEventDone;
        checkButton.setOnClickListener(this :: checkButtonOnClick);
    }

    private boolean backButtonOnClick(View view) {
        finish();
        return true;
    }

    private boolean checkButtonOnClick(View view) {
        final String comment = commentField.getText().toString();
        HashMap<String, String> comment_data = new HashMap<>();

        if (comment.trim().length()>0) {
            comment_data.put("comment", comment);
        }
        else {
            comment_data.put("comment", "");
        }
        addHabitEventToDatabase(comment_data);
        finish();
        return true;
    }

    /**
     * Adds habit event for the given habit
     * @param comment_data
     */
    private void addHabitEventToDatabase(HashMap<String,String> comment_data) {
        String habits_collection_name = "Habits";
        String habit_events_collection_name = "Events";
        String document_title = title + ": " + clickedDateStr;

        final CollectionReference collectionReference = db.collection(habits_collection_name)
                .document(title).collection(habit_events_collection_name);

        collectionReference
                .document(document_title)
                .set(comment_data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added!" + e.toString()); }
                });
    }
}
