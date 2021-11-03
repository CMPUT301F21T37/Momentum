package com.example.momentum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class Habit_Events extends AppCompatActivity {
    private Button habitEvent_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_events);

        //set animation for he_btn
        //habitEvent_btn = findViewById(R.id.habit_event_btn);

        //final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.he_btn);
       // habitEvent_btn.startAnimation(animTranslate);

        //click habit event btn to see details
        habitEvent_btn = (Button) findViewById(R.id.habit_event_btn);
        habitEvent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openIndiv_Events();
            }
        });
    }

    //method to open individual habit events page
    public void openIndiv_Events(){
        Intent intent = new Intent(this, Indiv_habitEvent_view.class);
        startActivity(intent);
    }
    
}