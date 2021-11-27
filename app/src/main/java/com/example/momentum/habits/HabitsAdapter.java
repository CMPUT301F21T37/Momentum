package com.example.momentum.habits;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momentum.Habit;
import com.example.momentum.MainActivity;
import com.example.momentum.R;
import com.example.momentum.add.AddFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Version 2.0:
 * A class extending to a RecyclerView adapter that keeps a list of habits for HabitsFragment up-to-date.
 * It also implements HabitsMoveCallback for when a row in the RecyclerView is dragged/moved.
 * It carries listeners to view details of a habit, edit a habit, and delete a habit.
 * @author Kaye Ena Crayzhel F. Misay
 *
 * Note:
 * This was updated from extending to an Array Adapter to a RecyclerView Adapter for drag and drop reordering.
 * First Version authors:
 * @author Kaye Ena Crayzhel F. Misay
 * @author Boxiao Li
 */
public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.MyViewModel>
        implements HabitsMoveCallback.RecyclerViewRowTouchHelperContract {
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
        this.habits = habits;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_card_view_edit_delete, parent,false);
        return new MyViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewModel holder, int position) {
        Log.d("order", "habitsList adapter:" + habits.toString());
        Habit habit = habits.get(position); // Get the Habit instance of the current item

        // sets the habit list name
        holder.habitTitle.setText(habit.getTitle());

        // event monitoring response part
        // sets a listener for the card view to show habit details
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHabitDetails(habit);
            }
        });

        // sets a listener for the delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClicked(habit);
            }
        });

        // sets a listener for the edit button
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditClicked(habit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    @Override
    public void onRowMoved(int from, int to) {
        Integer size = getItemCount();
        Integer fromPos = from;
        Integer toPos = to;

        String size_str = size.toString() + " From: " + fromPos + " To: " + toPos;
        Log.d("Size", size_str);

        if (from < to) {
            for (int index = from; index < to; index++) {
                Collections.swap(habits, index, index+1);
            }
        }
        else {
            for (int index = from; index > to; index--) {
                Collections.swap(habits, index, index-1);
            }
        }
        this.notifyItemMoved(from, to);
    }

    @Override
    public void onRowSelected(MyViewModel viewHolder) {
        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.super_dark_grey));
    }

    @Override
    public void onRowClear(MyViewModel viewHolder) {
        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
    }

    public class MyViewModel extends RecyclerView.ViewHolder {

        private FloatingActionButton editButton;
        private FloatingActionButton deleteButton;
        private CardView cardView;
        private TextView habitTitle;

        public MyViewModel (View view) {
            super(view);
            habitTitle = view.findViewById(R.id.card_view_edit_delete_text);
            cardView = view.findViewById(R.id.card_view_edit_delete);
            deleteButton = view.findViewById(R.id.card_view_delete);
            editButton = view.findViewById(R.id.card_view_edit);
        }
    }

    /**
     * Helper method to set the database
     */
    private void setDatabase() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
    }

    /**
     * Method to show a habit's details by going to another activity
     * @param habit
     * An instance of habit to be shown
     */
    private void showHabitDetails(Habit habit) {
        Intent intent = new Intent(context, ViewHabitActivity.class);
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
        // create a list of the habit names
        ArrayList<String> habit_titles = new ArrayList<>();
        for (int index = 0; index < habits.size(); index++) {
            habit_titles.add(habits.get(index).getTitle());
        }

        Intent intent = new Intent(context, DeleteHabitActivity.class);
        intent.putExtra(HabitsFragment.HABIT_TITLE, habit.getTitle());
        intent.putExtra(HabitsFragment.HABIT_REASON, habit.getReason());
        intent.putExtra(HabitsFragment.HABIT_ARRAY, habit_titles);
        context.startActivity(intent);
    }

    /**
     * Method to go to another activity to let the user edit a given habit
     * @param habit
     * An instance of Habit to be edited
     */
    private void onEditClicked(Habit habit) {
        Intent intent = new Intent(context, HabitsEditActivity.class);
        intent.putExtra(HabitsFragment.HABIT_TITLE, habit.getTitle());
        intent.putExtra(HabitsFragment.HABIT_REASON, habit.getReason());
        intent.putExtra(HabitsFragment.HABIT_FREQUENCY, habit.getWeekly_frequency());
        intent.putExtra(HabitsFragment.HABIT_PRIVACY, habit.isPrivate_account());
        intent.putExtra(HabitsFragment.HABIT_DATE, habit.getDate());
        context.startActivity(intent);
    }
}