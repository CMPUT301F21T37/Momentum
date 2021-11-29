package com.example.momentum.habits;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.momentum.Habit;
import com.example.momentum.R;
import com.example.momentum.add.AddFragment;
import com.example.momentum.databinding.ActivityDayHabitsBinding;
import com.example.momentum.databinding.ActivityDeleteHabitBinding;
import com.example.momentum.databinding.ActivityEditHabitBinding;
import com.example.momentum.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A class that lets the user delete a habit.
 * It also handles the reordering of all habits given the deleted one.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class DeleteHabitActivity extends AppCompatActivity {

    ActivityDeleteHabitBinding binding;

    // database initialization
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    private String title;
    private String reason;
    private ArrayList<?> habits;

    private FloatingActionButton backButton;
    private TextView motivation;
    private TextView habitTitle;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityDeleteHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(Constants.HABIT_TITLE);
        reason = intent.getStringExtra(Constants.HABIT_REASON);
        habits = (ArrayList<?>) intent.getStringArrayListExtra(Constants.HABIT_ARRAY);

        // set the title
        habitTitle = binding.deleteHabitTitle;
        habitTitle.setText(title);

        // set the motivation
        motivation = binding.deleteHabitMotivation;
        SpannableStringBuilder display_reason = new SpannableStringBuilder("Motivation \n" + reason);
        StyleSpan bold = new StyleSpan(Typeface.BOLD);
        display_reason.setSpan(bold, 0,10, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make motivation bold
        motivation.setText(display_reason);

        // back button to go back to previous fragment
        backButton = binding.deleteHabitBack;
        backButton.setOnClickListener(this::backButtonOnClick);

        // listener for when the DELETE button is clicked
        deleteButton = binding.deleteHabitButton;
        deleteButton.setOnClickListener(this::deleteButtonOnClick);
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
     * Callback handler for when the delete button is clicked.
     * Goes back to the previous fragment.
     * First, it gets the document to be deleted. On success, it will edit the order first.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean deleteButtonOnClick(View view) {
        DocumentReference documentReference = db.collection("Users").document(uid).
                collection("Habits").document(title);

        documentReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // get the current order to reorder the habits before deleting
                        String order_str = (String) documentSnapshot.getData().get("order");
                        Integer order = Integer.valueOf(order_str);
                        editOrder(order);
                    }
                });

        return true;
    }

    /**
     * A method that edits the order from the database.
     * Using WriteBatch, the method will decrease the order of all habits below the deleted habit.
     * On complete, it will decrease the habit count.
     * @param order
     * Current order of the habit to be deleted.
     */
    private void editOrder(Integer order) {
        // setting up the document references
        CollectionReference collectionReference = db.collection("Users").document(uid).
                collection("Habits");

        WriteBatch batch = db.batch();

        // updating the order of all habits
        for (int index = order + 1; index < habits.size(); index++) {
            Integer index_int = (Integer) index - 1;
            batch.update(collectionReference.document((String) habits.get(index)), "order", index_int.toString());
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                decreaseHabitCount();
            }
        });
    }

    /**
     * A method that updates the habit count.
     * On Success, it will delete the habit.
     */
    private void decreaseHabitCount() {
        // getting the number of habits based on the list size
        Integer count = (Integer) habits.size() - 1;

        /*
        Reference on how to update a document
        https://saveyourtime.medium.com/firebase-cloud-firestore-add-set-update-delete-get-data-6da566513b1b
         */
        DocumentReference documentReference = db.collection("Users").document(uid).
                collection("HabitCount").document("count");

        documentReference
                .update("habits", count.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(AddFragment.UPDATE_COUNT, "DocumentSnapshot successfully updated!");
                        deleteHabit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(AddFragment.UPDATE_COUNT, "Error updating document", e);
                    }
                });
    }

    /**
     * A method that deletes the given habit.
     * On success, it will delete all the corresponding habit events.
     */
    private void deleteHabit() {
        CollectionReference habitsReference = db.collection("Users").document(uid).collection("Habits");

        habitsReference
                .document(title)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(Constants.HABIT_DELETE, "DocumentSnapshot successfully deleted!");
                        deleteHabitEvents();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Constants.HABIT_DELETE, "Error Deleting document", e);
                    }
                });
    }

    /**
     * A method that deletes all habit events that correspond with the habit to deleted.
     * On complete, it takes all habit events equal to the current event.
     * Then, it calls its helper method to delete it.
     */
    private void deleteHabitEvents () {
        List<String> eventsTitle = new ArrayList<>();
        CollectionReference eventsReference = db.collection("Users").document(uid).collection("Events");

        //gather all the events given the query whereEqualTo
        //https://firebase.google.com/docs/firestore/query-data/get-data

        eventsReference
                .whereEqualTo("habit", title) // delete all habit events that have the same title
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // adding the events to be deleted
                                eventsTitle.add(document.getId());
                            }
                            deleteHabitEventsHelper(eventsTitle);
                        } else {
                            Log.d(Constants.EVENTS_DELETE, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * A helper method for the deleteHabitEvents to delete all the corresponding events using WriteBatch.
     * On complete, go back to the previous fragment.
     * @param eventsTitle
     */
    private void deleteHabitEventsHelper(List<String> eventsTitle) {
        // setting up the document references
        CollectionReference eventsReference = db.collection("Users").document(uid).collection("Events");

        WriteBatch batch = db.batch();

        // updating the order of all habits
        for (String eventTitle : eventsTitle) {
            batch.delete(eventsReference.document(eventTitle));
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }
}