package com.example.momentum.habitEvents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.momentum.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Indiv_habitEvent_view extends AppCompatActivity {
    TextView title, he_date, comment;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indiv_habit_event_view);

        title = (TextView) findViewById(R.id.habit_event);
        he_date = (TextView) findViewById(R.id.event_recordDate);
        comment = (TextView) findViewById(R.id.comment);

        reff = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Habits").child("Events");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("title").getValue().toString();
                String date = snapshot.child("date").getValue().toString();
                String reason = snapshot.child("reason").getValue().toString();
                title.setText(name);
                he_date.setText(date);
                comment.setText(reason);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}