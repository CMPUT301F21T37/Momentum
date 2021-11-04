package com.example.momentum.habits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.databinding.FragmentHabitsBinding;
import com.example.momentum.home.DayHabits;
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

public class HabitsFragment extends Fragment {
    private HabitsViewModel HabitsViewModel;
    private FragmentHabitsBinding binding;
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

        /*
        // listener for the Firestore database to accept realtime updates
        habitsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                HabitsViewModel.clearHabitsList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    ArrayList<?> frequency = (ArrayList<?>) doc.getData().get("frequency");
                    Timestamp start_timestamp = (Timestamp) doc.getData().get("date");
                    Date start_date = start_timestamp.toDate();
                    /*
                    - Current clicked date must be on or after the start date of a given habit
                    - There must be a frequency set (should be on add habits)
                    - Current day of the week should be in the frequency array
                     */
        /*
                    if ((clickedDate.compareTo(start_date) >= 0) && (frequency != null)
                            && frequency.contains(dayofWeek)) {
                        String habit_title = doc.getId();
                        String reason = (String) doc.getData().get("reason");
                        com.example.momentum.habits.HabitsViewModel.addHabit(new DayHabits(habit_title, reason));
                    }
                }
                // Notifying the adapter to render any new data fetched from the cloud
                habitsAdapter.notifyDataSetChanged();
            }
        });*/
        return root;
    }
}
