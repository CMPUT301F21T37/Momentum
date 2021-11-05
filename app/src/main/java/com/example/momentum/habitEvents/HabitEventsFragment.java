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
 * A fragment that shows all habit events of a given user.
 * @author Han Yan
 * @author Kaye Ena Crayzhel F. Misay
 */
public class HabitEventsFragment extends Fragment {
    public static final String EVENT_TITLE = "EVENT_TITLE";
    public static final String EVENT_COMMENT = "EVENT_COMMENT";

    private HabitEventsViewModel HabitEventsViewModel;
    private FragmentHabitEventsBinding binding;
    private ArrayAdapter<Event> eventsAdapter;
    private ListView habitsListView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // binds the fragment to MainActivity and creates the view
        HabitEventsViewModel = new ViewModelProvider(this).get(HabitEventsViewModel.class);
        binding = FragmentHabitEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        final CollectionReference habitsReference = db.collection("Users").
                document(uid).collection("Events");


        // listener for the Firestore database to accept realtime updates
        habitsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                HabitEventsViewModel.clearEventsList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    // getting the data of a given habit document
                    String comment = (String) doc.getData().get("comment");
                    String habit_title = doc.getId();

                    // store it to the the view model
                    HabitEventsViewModel.addEvent(new Event(habit_title, comment));
                }
                // Notifying the adapter to render any new data fetched from the cloud
                eventsAdapter.notifyDataSetChanged();
            }
        });
        // initiates the display
        showAllEvents();

        // checks if a certain habit is clicked
        habitsListView.setOnItemClickListener(this :: onEventClick);

        return root;
    }

    /**
     * It takes a list of Event event from the collection to be added to the events list adapter
     */
    public void showAllEvents() {
        habitsListView = binding.allEventsList;

        HabitEventsViewModel.getHabitsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> eventsList) {
                eventsAdapter = new HabitEventsAdapter(getContext(),eventsList);
                habitsListView.setAdapter(eventsAdapter);
            }
        });
    }

    /**
     * Callback handler for when a habit is clicked in the habitsListView.
     * When a habit is clicked, it goes to another activity that shows its details.
     * @param adapterView
     * View of the adapter associated with the listener.
     * @param view
     * Current general view associated with the listener.
     * @param position
     * Position in the adapter of what was clicked.
     * @param id
     * ID associated with the adapter.
     * @return
     * 'true' to confirm with the listener.
     */
    private boolean onEventClick(AdapterView<?> adapterView, View view, int position, long id) {
        Event event = (Event) adapterView.getAdapter().getItem(position); // get the event details

        // show the user a view of its habit details by going to ViewHabitActivity
        Intent intent = new Intent(getContext(), Indiv_habitEvent_view.class);
        intent.putExtra(EVENT_TITLE, event.getTitle());
        intent.putExtra(EVENT_COMMENT, event.getComment());
        startActivity(intent);

        return true;
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
