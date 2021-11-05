package com.example.momentum.habits;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.momentum.Habit;
import com.example.momentum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A class extending to an Array Adapter that keeps a list of habits for HabitsFraagment up-to-date.
 * It carries listeners to view details of a habit, edit a habit, and delete a habit.
 * @author Kaye Ena Crayzhel F. Misay
 * @author Boxiao Li
 */
public class HabitsAdapter extends ArrayAdapter<Habit>{
    public static final String DELETE_HABIT = "DELETE_HABIT";
    public static final String DELETE_EVENT = "DELETE_EVENT";

    private ArrayList<Habit> habits;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference habitsReference;
    private CollectionReference eventsReference;

    public HabitsAdapter(Context context, ArrayList<Habit> habits){
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }

    /* Override the getView() method, which will be called when each child item is clicked to the screen */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        ViewHolder viewHolder;
        Habit habit = getItem(position); // Get the Habit instance of the current item

        /*
        On How to create a functional layout for listview with buttons
        https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android
         */
        if(view == null){
            // if view is null, inflate the layout
            view = LayoutInflater.from(context).inflate(R.layout.content_card_view_edit_delete, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.cardView = view.findViewById(R.id.card_view_edit_delete);
            viewHolder.deleteButton = view.findViewById(R.id.card_view_delete);
            viewHolder.editButton = view.findViewById(R.id.card_view_edit);
            view.setTag(viewHolder);
        }
        else {
            // else, use the previous one
            viewHolder = (ViewHolder) view.getTag();
        }

        // sets the habit list name
        TextView habitTitle = view.findViewById(R.id.card_view_edit_delete_text);
        habitTitle.setText(habit.getTitle());

        // event monitoring response part
        // sets a listener for the card view to show habit details
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHabitDetails(habit);
            }
        });

        // sets a listener for the delete button
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClicked(habit);
            }
        });

        // sets a listener for the edit button
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditClicked(habit);
            }
        });
        return view;
    }

    /**
     * Internal class to use to save instances of the given variables
     */
    class ViewHolder {
        private FloatingActionButton editButton;
        private FloatingActionButton deleteButton;
        private CardView cardView;

    }
    /**
     * Helper method to set the database
     */
    private void setDatabase() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        eventsReference = db.collection("Users").document(uid).collection("Events");
        habitsReference = db.collection("Users").document(uid).collection("Habits");
    }

    /**
     * Method to show a habit's details by going to another activity
     * @param habit
     * An instance of habit to be shown
     */
    private void showHabitDetails(Habit habit) {
        Intent intent = new Intent(getContext(), ViewHabitActivity.class);
        intent.putExtra(HabitsFragment.HABIT_TITLE, habit.getTitle());
        intent.putExtra(HabitsFragment.HABIT_REASON, habit.getReason());
        intent.putExtra(HabitsFragment.HABIT_FREQUENCY, habit.getWeekly_frequency());
        intent.putExtra(HabitsFragment.HABIT_PRIVACY, habit.isPrivate_account());
        intent.putExtra(HabitsFragment.HABIT_DATE, habit.getDate());
        context.startActivity(intent);
    }

    /**
     * Method to show an alert dialog to the user to confirm deletion of habit
     * @param habit
     * An instance of Habit habit to be deleted
     */
    private void onDeleteClicked(Habit habit) {
        setDatabase(); // set an instance of the database
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure you want to delete " + habit.getTitle() + "?");
        alertDialog.setPositiveButton("Delete", (dialog, id) -> {
            deleteHabit(habit);
            deleteHabitEvents(habit);
            dialog.cancel();
        });
        alertDialog.setNegativeButton("Cancel", (dialog,id) -> dialog.cancel());

        AlertDialog dialogBuilder = alertDialog.create();
        dialogBuilder.show();
        dialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_main));
        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_main));
    }

    /**
     * method to delete a habit
     * @param habit
     * an instance of habit to be deleted
     */
    private void deleteHabit(Habit habit) {
        habitsReference
                .document(habit.getTitle())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(DELETE_HABIT, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(DELETE_HABIT, "Error Deleting document", e);
                    }
                });
        this.notifyDataSetChanged();
    }

    /**
     * Helper method to delete all habit events of a given habit.
     * @param habit
     * An instance of habit to be deleted
     */
    private void deleteHabitEvents (Habit habit) {
        List<String> eventsTitle = new ArrayList<>();
        /*
        gather all the events given the query whereEqualTo
        https://firebase.google.com/docs/firestore/query-data/get-data
         */
        eventsReference
                .whereEqualTo("habit", habit.getTitle())
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
                            Log.d(DELETE_EVENT, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * A helper method for the asynchronous get() method of the collection reference
     * @param eventsTitle
     * a list of the habit events to be deleted
     */
    private void deleteHabitEventsHelper(List<String> eventsTitle) {
        // delete all events with habit fields = deleted habit
        for (String eventTitle : eventsTitle) {
            eventsReference
                    .document(eventTitle)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(DELETE_EVENT, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(DELETE_EVENT, "Error Deleting document", e);
                        }
                    });
        }
        this.notifyDataSetChanged();

    }

    /**
     * Method to go to another activity to let the user edit a given habit
     * @param habit
     * An instance of Habit to be edited
     */
    private void onEditClicked(Habit habit) {
        Intent intent = new Intent(getContext(), HabitsEditActivity.class);
        intent.putExtra(HabitsFragment.HABIT_TITLE, habit.getTitle());
        intent.putExtra(HabitsFragment.HABIT_REASON, habit.getReason());
        intent.putExtra(HabitsFragment.HABIT_FREQUENCY, habit.getWeekly_frequency());
        intent.putExtra(HabitsFragment.HABIT_PRIVACY, habit.isPrivate_account());
        intent.putExtra(HabitsFragment.HABIT_DATE, habit.getDate());
        context.startActivity(intent);
    }
}