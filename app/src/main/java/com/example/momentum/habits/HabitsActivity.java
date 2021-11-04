package com.example.momentum.habits;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.momentum.Habit;
import com.example.momentum.R;

import java.util.ArrayList;
import java.util.List;

public class HabitsActivity extends AppCompatActivity {
    private List<Habit> PersonList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_habits);
        HabitsAdapter adapter = new HabitsAdapter(HabitsActivity.this, R.layout.swipe_habits_activity_item, PersonList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }
}