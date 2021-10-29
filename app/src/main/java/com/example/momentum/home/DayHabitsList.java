package com.example.momentum.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.momentum.R;

import java.util.ArrayList;

public class DayHabitsList extends ArrayAdapter<DayHabits> {
    private ArrayList<DayHabits> habits;
    private Context context;

    public DayHabitsList(Context context, ArrayList<DayHabits> habits){
        super(context,0, habits);
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

        DayHabits habit = habits.get(position);

        TextView habitTitle = view.findViewById(R.id.card_view_text);
        habitTitle.setText(habit.getDayHabitTitle());

        return view;
    }
}
