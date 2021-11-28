package com.example.momentum.habitEvents;

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
import com.example.momentum.databinding.FragmentHabitEventsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment that shows all habit events of a given user.
 * @author Han Yan
 * @author Kaye Ena Crayzhel F. Misay
 */
public class HabitEventsFragment extends Fragment {
    public static final String EVENT_TITLE = "EVENT_TITLE";
    public static final String EVENT_COMMENT = "EVENT_COMMENT";
    public static final String EVENT_LATITUDE = "EVENT_LATITUDE";
    public static final String EVENT_LONGITUDE = "EVENT_LONGITUDE";
    public static final String EVENT_IMAGE = "EVENT_IMAGE";
    public static final String EVENT_OBJECT = "EVENT_OBJECT";

    private HabitEventsViewModel HabitEventsViewModel;
    private FragmentHabitEventsBinding binding;
    private ArrayAdapter<Event> eventsAdapter;
    private ListView eventsListView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // binds the fragment to MainActivity and creates the view
        HabitEventsViewModel = new ViewModelProvider(this).get(HabitEventsViewModel.class);
        binding = FragmentHabitEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        final CollectionReference eventsReference = db.collection("Users").document(uid).collection("Events");


        // listener for the Firestore database to accept realtime updates
        eventsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                HabitEventsViewModel.clearEventsList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    Event event = (Event) doc.toObject(Event.class);
                    // store it to the the view model
                    HabitEventsViewModel.addEvent(event);
                }
                // Notifying the adapter to render any new data fetched from the cloud
                eventsAdapter.notifyDataSetChanged();
            }
        });
        // initiates the display
        showAllEvents();

        return root;
    }

    /**
     * It takes a list of Event event from the collection to be added to the events list adapter
     */
    public void showAllEvents() {
        eventsListView = binding.allEventsList;

        HabitEventsViewModel.getEventsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> eventsList) {
                eventsAdapter = new HabitEventsAdapter(getContext(),eventsList);
                eventsListView.setAdapter(eventsAdapter);
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
