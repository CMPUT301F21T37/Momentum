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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * An activity that lets the user add a habit event for when a habit is done for the day.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class AddHabitEventActivity extends AppCompatActivity {
    private static final String TAG = "ADD_HABIT_EVENT";

    private ActivityAddHabitEventBinding binding;
    private FloatingActionButton backButton;
    private FloatingActionButton checkButton;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private String title;
    private String documentTitle;
    private TextView activityTitle;
    private EditText commentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityAddHabitEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(DayHabitsFragment.TITLE_DAY_HABIT);
        documentTitle = intent.getStringExtra(DayHabitsFragment.TITLE_HABIT_EVENT);

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

    /**
     * Callback handler for when the check button is clicked.
     * Calls a method to add a habit event with optional comment.
     * Goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean checkButtonOnClick(View view) {
        final String comment = commentField.getText().toString();
        // create a hashmap to be inputted
        HashMap<String, String> data = new HashMap<>();
        data.put("habit", title);

        if (comment.trim().length()>0) { // for when there exists a comment
            data.put("comment", comment);
        }
        else { // for when the comment is empty
            data.put("comment", "");
        }
        // make a call to the database and then close the activity
        addHabitEventToDatabase(data);
        finish();
        return true;
    }

    /**
     * Adds habit event for the given habit.
     * @param data
     * The data to be put in the Events document fields.
     * Currently: optional comment and habit
     */
    public void addHabitEventToDatabase(HashMap<String,String> data) {
        String users_collection_name = "Users";
        String habit_events_collection_name = "Events";

        // adds to a sub-collection of Habits of the current user
        final CollectionReference collectionReference = db.collection(users_collection_name).document(uid).
                collection(habit_events_collection_name);

        collectionReference
                .document(documentTitle)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString()); }
                });
    }
}
