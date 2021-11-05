package com.example.momentum.habitEvents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.momentum.habitEvents.Event;
import com.example.momentum.habitEvents.HabitEventsViewModel;
import com.example.momentum.databinding.ActivityEditEventsBinding;
import com.example.momentum.habitEvents.HabitEventsAdapter;
import com.example.momentum.habitEvents.HabitEventsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class HabitsEventsEditActivity extends AppCompatActivity {
    public static final String EDIT_EVENT = "EDIT EVENT";

    private ActivityEditEventsBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference eventReference;

    private String title;
    private String reason;

    private FloatingActionButton backButton;
    private EditText titleEdit;
    private EditText reasonEdit;
    private Button editEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityEditEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitEventsFragment.EVENT_TITLE);
        reason = intent.getStringExtra(HabitEventsFragment.EVENT_COMMENT);


        // initialize previous values
        initializeValues();

        // back button to go back to previous fragment
        backButton = binding.editEventBack;
        backButton.setOnClickListener(this::backButtonOnClick);


        // listener for when the 'EDIT HABIT' button is clicked
        editEventButton = binding.editConfirmButton;
        editEventButton.setOnClickListener(this::editEventButtonOnClick);
    }

    /**
     * A method that sets previous inputs from the user.
     */
    public void initializeValues() {
        // initializes the title
        titleEdit = binding.eventTitleText;
        titleEdit.setText(title);

        // initializes the reason
        reasonEdit = binding.motivationText;
        reasonEdit.setText(reason);

    }


    /**
     * Callback handler for when the start delete text is clicked.
     * Starting date is not changeable (since it's the starting date)
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean startDateOnClick(View view) {
        Toast.makeText(HabitsEventsEditActivity.this, "You cannot change your habit start date.",
                Toast.LENGTH_SHORT).show();
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

    /**
     * Callback handler for when the edit button is clicked.
     * Calls a method to update the database.
     * Goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean editEventButtonOnClick(View view) {
        // initialize collection reference
        eventReference = db.collection("Users").document(uid).collection("Events");

        // getting the new strings for title and reason
        String newTitle = titleEdit.getText().toString();
        String newReason = reasonEdit.getText().toString();

        // add to a hashmap to update database
        HashMap<String, Object> data = new HashMap<>();
        data.put("reason", newReason);

        // if the title has changed, delete the old document (based on database structure)
        if (!newTitle.equals(title)) {
            Event event = new Event(title, reason);
            eventReference = db.collection("Users").document(uid).collection("Events");
            deleteOldEventDatabase(event);
        }
        // updates the database then closes the activity
        editEventToDatabase(data, newTitle);
        return true;
    }

    /**
     * Deletes the instance of the previous event in the database
     * @param event
     * Instance of Habit to be deleted
     */
    private void deleteOldEventDatabase(Event event) {
        eventReference
                .document(event.getTitle())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(HabitEventsAdapter.DELETE_EVENT, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(HabitEventsAdapter.DELETE_EVENT, "Error Deleting document", e);
                    }
                });
    }

    /**
     * Edits a given event.
     * @param data
     * The data to be put in Events fields.
     */
    private void editEventToDatabase(HashMap<String, Object> data, String newTitle) {
        String users_collection_name = "Users";
        String events_collection_name = "Events";

        // adds to a sub-collection of Habits of the current user
        final CollectionReference collectionReference = db.collection(users_collection_name).document(uid).
                collection(events_collection_name);

        // this will overwrite the document since it is unknown what the user will change to use .update()
        collectionReference
                .document(newTitle)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(EDIT_EVENT, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(EDIT_EVENT, "Data could not be added!" + e.toString());
                    }
                });
    }
}
