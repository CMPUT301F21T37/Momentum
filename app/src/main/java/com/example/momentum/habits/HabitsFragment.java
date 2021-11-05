package com.example.momentum.habits;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.databinding.FragmentHabitsBinding;
import com.example.momentum.Habit;
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
 * A fragment in MainActivity from HomeFragment that shows all habits.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class HabitsFragment extends Fragment {
    public static final String HABIT_TITLE = "HABIT_TITLE";
    public static final String HABIT_REASON = "HABIT_REASON";
    public static final String HABIT_FREQUENCY = "HABIT_FREQUENCY";
    public static final String HABIT_PRIVACY = "HABIT_PRIVACY";
    public static final String HABIT_DATE = "HABIT_DATE";

    private HabitsViewModel HabitsViewModel;
    private FragmentHabitsBinding binding;
    private ArrayAdapter<Habit> habitsAdapter;
    private ListView habitsListView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // binds the fragment to MainActivity and creates the view
        HabitsViewModel = new ViewModelProvider(this).get(HabitsViewModel.class);
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        final CollectionReference habitsReference = db.collection("Users").
                document(uid).collection("Habits");

        // listener for the Firestore database to accept realtime updates
        habitsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                HabitsViewModel.clearHabitsList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    // getting the data of a given habit document
                    String reason = (String) doc.getData().get("reason");
                    ArrayList<?> frequency = (ArrayList<?>) doc.getData().get("frequency");
                    Timestamp start_timestamp = (Timestamp) doc.getData().get("date");
                    Date start_date = start_timestamp.toDate();
                    String habit_title = doc.getId();
                    Boolean is_private = (Boolean) doc.getData().get("private");

                    // store it to the the view model
                    HabitsViewModel.addHabit(new Habit(habit_title, reason, start_date, is_private, frequency));
                }
                // Notifying the adapter to render any new data fetched from the cloud
                habitsAdapter.notifyDataSetChanged();
            }
        });
        // initiates the display
        showAllHabits();

        return root;
    }

    /**
     * It takes a list of Habit habits from the collection to be added to the habits list adapter
     */
    public void showAllHabits() {
        habitsListView = binding.allHabitsList;

        HabitsViewModel.getHabitsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Habit>>() {
            @Override
            public void onChanged(ArrayList<Habit> dayHabitsList) {
                habitsAdapter = new HabitsAdapter(getContext(),dayHabitsList);
                habitsListView.setAdapter(habitsAdapter);
            }
        });
    }

    /**
     * When view is destroyed, set binding to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
