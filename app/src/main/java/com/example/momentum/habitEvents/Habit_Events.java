package com.example.momentum.habitEvents;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;


import com.example.momentum.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * an activity shows list of habit events
 * click items on list to view the individual habit event details
 * click delete button and click the habit event that user wants to remove
 * @author Han Yan
 */
public class Habit_Events extends AppCompatActivity {
    ListView listView;
    Button delete_habitEvent_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_events);

        listView = findViewById(R.id.habit_event_listView);

        //create a list for habit events
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        //create reference of database and retrieve habit events collection from db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Habits").child("Events");//not sure the correct path
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //click item on the list and switch to individual view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                startActivity(new Intent(Habit_Events.this, Indiv_habitEvent_view.class));
            }

        });

        //delete a habit event
        delete_habitEvent_btn =findViewById(R.id.delete_he_btn);
        delete_habitEvent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String itemValue = (String)listView.getItemAtPosition(position);
                        adapter.remove(itemValue);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });



    }




}