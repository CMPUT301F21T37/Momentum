package com.example.momentum.habitEvents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.momentum.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HabitEventsAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    public HabitEventsAdapter(Context context, ArrayList<Event> habits){
        super(context, 0, habits);
        this.events = habits;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_card_view, parent,false);
        }
        Event event = events.get(position);

        // sets the habit list names
        TextView habitTitle = view.findViewById(R.id.card_view_text);
        habitTitle.setText(event.getTitle());

        return view;
    }

}
