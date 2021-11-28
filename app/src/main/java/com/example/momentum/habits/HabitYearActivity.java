package com.example.momentum.habits;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.databinding.ActivityVisualIndicatorBinding;
import com.example.momentum.databinding.ActivityYearHabitBinding;
import com.example.momentum.home.DayHabits;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * A class that lets the user pick a year to see a habit's visual indicator.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class HabitYearActivity extends AppCompatActivity {
    public static final String HABIT_YEAR = "HABIT_YEAR";

    private ActivityYearHabitBinding binding;

    private String title;
    private ArrayList<?> frequency;

    private FirebaseFirestore db;
    private String uid;

    private FloatingActionButton backButton;
    private ListView yearsListView;
    private ArrayAdapter<String> yearsAdapter;
    private ArrayList<String> yearsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityYearHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitsFragment.HABIT_TITLE);
        uid = intent.getStringExtra(ViewHabitActivity.VISUAL_INDICATOR_USER);
        frequency = (ArrayList<?>) intent.getStringArrayListExtra(HabitsFragment.HABIT_FREQUENCY);

        // initializing the database
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users").document(uid)
                .collection("Habits").document(title).collection("Years");

        // listener for the Firestore database to accept realtime updates
        yearsList = new ArrayList<>();
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                yearsList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String year = doc.getId();
                    yearsList.add(year);
                }
                // Notifying the adapter to render any new data fetched from the cloud
                yearsAdapter.notifyDataSetChanged();
            }
        });

        // setting up the adapter
        yearsListView = binding.yearHabitList;
        yearsAdapter = new HabitYearAdapter(this, yearsList);
        yearsListView.setAdapter(yearsAdapter);

        // listener for the listview
        yearsListView.setOnItemClickListener(this :: onYearsClick);

        // back button listener
        backButton = binding.yearHabitBack;
        backButton.setOnClickListener(this :: backButtonOnClick);
    }

    /**
     * Callback handler for when a year is clicked
     * When a year is clicked, it goes to the VisualIndicatorActivity
     * @param adapterView
     * @param view
     * @param position
     * @param id
     * @return
     */
    private boolean onYearsClick(AdapterView<?> adapterView, View view, int position, long id) {
        String year = (String) adapterView.getAdapter().getItem(position);

        Intent intent = new Intent(HabitYearActivity.this, VisualIndicatorActivity.class);
        intent.putExtra(HabitsFragment.HABIT_TITLE, title);
        intent.putExtra(HABIT_YEAR, year);
        intent.putExtra(ViewHabitActivity.VISUAL_INDICATOR_USER, uid);
        intent.putExtra(HabitsFragment.HABIT_FREQUENCY, frequency);
        startActivity(intent);
        return true;
    }

    /**
     * Callback handler for when the back button is clicked.
     * Goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean backButtonOnClick(View view) {
        finish();
        return true;
    }

}
