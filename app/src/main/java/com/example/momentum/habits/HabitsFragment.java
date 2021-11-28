package com.example.momentum.habits;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momentum.databinding.FragmentHabitsBinding;
import com.example.momentum.Habit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

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
    public static final String HABIT_ARRAY = "HABIT_ARRAY";
    public static final String HABIT_DELETE = "HABIT_DELETE";
    public static final String EVENTS_DELETE = "EVENTS_DELETE";

    private FragmentHabitsBinding binding;
    private HabitsAdapter habitsAdapter;
    private RecyclerView habitsRecyclerView;
    private ArrayList<Habit> habitsList;
    private ArrayList<Habit> habitsListHelper;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // binds the fragment to MainActivity and creates the view
        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        final CollectionReference habitsReference = db.collection("Users").
                document(uid).collection("Habits");

        // listener for the Firestore database to accept realtime updates
        habitsList = new ArrayList<>();
        habitsListHelper = new ArrayList<>();

        habitsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                // populate the habits list
                habitsList.clear();
                habitsListHelper.clear();

                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    // getting the data of a given habit document
                    String reason = (String) doc.getData().get("reason");
                    ArrayList<?> frequency = (ArrayList<?>) doc.getData().get("frequency");
                    Timestamp start_timestamp = (Timestamp) doc.getData().get("date");
                    Date start_date = start_timestamp.toDate();
                    String habit_title = doc.getId();
                    Boolean is_private = (Boolean) doc.getData().get("private");
                    String index_str = (String) doc.getData().get("order");
                    Integer index = Integer.valueOf(index_str);

                    // store it to the the view model
                    habitsListHelper.add(new Habit(habit_title, reason, start_date, is_private, frequency, index));
                }
                setCorrectOrder();
            }
        });
        // initiate display data
        // setting up the recycler view
        habitsRecyclerView = binding.allHabitsList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        habitsRecyclerView.setLayoutManager(layoutManager);

        // setting up the adapter
        habitsAdapter = new HabitsAdapter(getContext(), habitsList);
        habitsRecyclerView.setAdapter(habitsAdapter);

        // item touch helper
        ItemTouchHelper.Callback callback = new HabitsMoveCallback(habitsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(habitsRecyclerView);

        return root;
    }

    /**
     * Setting the correct order of all the habits based on the stored order
     */
    private void setCorrectOrder() {
        // initializing to all null
        for (int index = 0; index < habitsListHelper.size(); index++) {
            habitsList.add(null);
        }
        // adding appropriate indices
        for (int index = 0; index < habitsListHelper.size(); index++) {
            Log.d("order", "order: " + habitsListHelper.get(index).getOrder().toString());
            habitsList.set(habitsListHelper.get(index).getOrder(), habitsListHelper.get(index));
        }
        // Notifying the adapter to render any new data fetched from the cloud
        habitsAdapter.notifyDataSetChanged();
    }

    /**
     * Edits the order of the database
     * On complete, notify the habitsAdapter
     */
    private void editOrder() {
        // setting up the document references
        CollectionReference collectionReference = db.collection("Users").document(uid).
                collection("Habits");

        WriteBatch batch = db.batch();

        // updating the order of all habits
        for (int index = 0; index < habitsList.size(); index++) {
            Integer index_int = (Integer) index;
            batch.update(collectionReference.document((String) habitsList.get(index).getTitle()), "order", index_int.toString());
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                habitsAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * When view is destroyed, edit order and send binding to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editOrder();
        binding = null;
    }

    /**
     * On pause of the fragment, edit the order
     */
    @Override
    public void onPause() {
        super.onPause();
        editOrder();
    }

    /**
     * On stop of the fragment, edit the order
     */
    @Override
    public void onStop() {
        super.onStop();
        editOrder();
    }

    /**
     * On resume of the fragment, notify the adapter
     */
    @Override
    public void onResume() {
        super.onResume();
        habitsAdapter.notifyDataSetChanged();
    }
}