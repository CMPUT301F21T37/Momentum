package com.example.momentum.habitEvents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.databinding.ActivityEditEventsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * An activity that lets the user edit the details of their habit events.
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 */
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
    private TextView titleView;
    private EditText reasonEdit;
    private FloatingActionButton editEventButton;

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

        // listener for when the check button is clicked
        editEventButton = binding.editEventDone;
        editEventButton.setOnClickListener(this::editEventButtonOnClick);
    }

    /**
     * A method that sets previous inputs from the user.
     */
    public void initializeValues() {
        // initializes the title
        titleView = binding.editEventTitle;
        titleView.setText(title);

        // initializes the reason
        reasonEdit = binding.editHabitEventComment;
        reasonEdit.setText(reason);

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

        // getting the new strings for newComment
        String newComment = reasonEdit.getText().toString();

        // updates the database then closes the activity
        editEventToDatabase(newComment);
        return true;
    }

    /**
     * Edits a given event.
     * @param newComment
     * The data to be put in Events field comment.
     */
    private void editEventToDatabase(String newComment) {
        String users_collection_name = "Users";
        String events_collection_name = "Events";

        // adds to a sub-collection of Habits of the current user
        final DocumentReference documentReference = db.collection(users_collection_name).document(uid).
                collection(events_collection_name).document(title);

        // this will overwrite the document since it is unknown what the user will change to use .update()
        documentReference
                .update("comment", newComment)
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
