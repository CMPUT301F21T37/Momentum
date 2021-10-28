package com.example.momentum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.momentum.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button habit_events_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // binds all of the fragments in the navigation bar and all the other fragments
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_settings, R.id.navigation_following, R.id.navigation_add_habit, R.id.navigation_sharing)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //switch to the habit events list page
        habit_events_btn = (Button) findViewById(R.id.view_all_habit_events_button);
        habit_events_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHabit_Events();
            }
        });
    }

    //method to open the list of habit events page
    public void openHabit_Events(){
        Intent intent = new Intent(this, Habit_Events.class);
        startActivity(intent);
    }
}