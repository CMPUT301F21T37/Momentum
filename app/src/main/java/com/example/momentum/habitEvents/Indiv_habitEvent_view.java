package com.example.momentum.habitEvents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.example.momentum.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * an activity show individual view of a habit event
 * comment can be edited
 * image and location uploading will be done by next project part
 * @author Han Yan
 */
public class Indiv_habitEvent_view extends AppCompatActivity {
    TextView title, he_date;
    EditText comment;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indiv_habit_event_view);

        title = (TextView) findViewById(R.id.habit_event);
        he_date = (TextView) findViewById(R.id.event_recordDate);
        comment = (EditText) findViewById(R.id.comment);

        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Habits").child("Events"); //not sure the correct path
        myRef.addValueEventListener(new ValueEventListener() {
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

        //check if the comment has been changed
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                myRef.setValue(comment.getText().toString());
            }
        });
    }
}