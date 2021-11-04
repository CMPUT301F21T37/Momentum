package com.example.momentum.habits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.momentum.Habit;
import com.example.momentum.R;
import com.example.momentum.home.DayHabits;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A custom list that extends to an array adapter that keeps the list of Habit habits up-to-date.
 * @author: Kaye Ena Crayzhel F. Misay
 */
public class HabitsAdapter extends ArrayAdapter<Habit> {
    private ArrayList<Habit> habits;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    public HabitsAdapter(Context context, ArrayList<Habit> habits){
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_card_view, parent,false);
        }
        Habit habit = habits.get(position);

        // sets the habit list names
        TextView habitTitle = view.findViewById(R.id.card_view_text);
        habitTitle.setText(habit.getTitle());

        return view;
    }

}
