package com.example.momentum.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.R;
import com.example.momentum.databinding.FragmentDayHabitsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * A fragment in MainActivity from HomeFragment that shows the clicked date's habits.
 * @author: Kaye Ena Crayzhel F. Misay
 */
public class DayHabitsFragment extends Fragment {
    public static final String TITLE_DAY_HABIT = "HABIT_TITLE";
    public static final String MOTIVATION = "MOTIVATION";
    public static final String DATE_COMPARE_DAY_HABIT = "IS_DATE_CLICKED_CURRENT";
    public static final String CLICKED_DATE_STR = "DATE_CLICKED_STR";
    public static final String TITLE_HABIT_EVENT = "HABIT_EVENT_TITLE";
    public static final String CHECK_IF_HABIT_EVENT_EXISTS = "CHECK_HABIT_EVENT_EXISTENCE";

    private DayHabitsViewModel DayHabitsViewModel;
    private FragmentDayHabitsBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private String dayofWeek;
    private String dayTitle;
    private Boolean isDateClickedEqualCurrent;
    private ListView dayHabitsListView;
    private TextView titleText;
    private Date clickedDate;
    private String clickedDateStr;
    private String habitEventTitle;
    private ArrayAdapter<DayHabits> habitsAdapter;

    // global result launcher for when DayHabitsActivity is called
    // when a habit is done, notify the adapter to set the changes
    ActivityResultLauncher<Intent> DayHabitsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        habitsAdapter.notifyDataSetChanged();
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // binds the fragment to MainActivity and creates the view
        DayHabitsViewModel = new ViewModelProvider(this).get(DayHabitsViewModel.class);
        binding = FragmentDayHabitsBinding.inflate(inflater, container, false);
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
                DayHabitsViewModel.clearHabitsList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    ArrayList<?> frequency = (ArrayList<?>) doc.getData().get("frequency");
                    Timestamp start_timestamp = (Timestamp) doc.getData().get("date");
                    Date start_date = start_timestamp.toDate();
                    /*
                    - Current clicked date must be on or after the start date of a given habit
                    - There must be a frequency set (should be on add habits)
                    - Current day of the week should be in the frequency array
                     */
                    if ((clickedDate.compareTo(start_date) >= 0) && (frequency != null)
                            && frequency.contains(dayofWeek)) {
                        String habit_title = doc.getId();
                        String reason = (String) doc.getData().get("reason");
                        DayHabitsViewModel.addHabit(new DayHabits(habit_title, reason));
                    }
                }
                // Notifying the adapter to render any new data fetched from the cloud
                habitsAdapter.notifyDataSetChanged();
            }
        });

        /*
        receiving data to set view
        https://www.youtube.com/watch?v=iVxKMZ8sGXY
        Author: Oum Saokosal
         */
        Bundle bundle = getArguments();
        if (bundle != null){
            dayTitle = bundle.getString(HomeFragment.DATE_TITLE_DAY_HABIT);
            dayofWeek = bundle.getString(HomeFragment.DAY_OF_WEEK);
            isDateClickedEqualCurrent = bundle.getBoolean(HomeFragment.DATE_COMPARE_DAY_HABIT);
            clickedDate = (Date) bundle.getSerializable(HomeFragment.DATE_CLICKED_DAY_HABIT);
            clickedDateStr = bundle.getString(HomeFragment.DATE_CLICKED_DAY_HABIT_STR);
            changeDayTitle();
            showDayHabits();
        }

        // checks if a certain habit is clicked
        dayHabitsListView.setOnItemClickListener(this::onDayHabitsClick);

        return root;
    }

    /**
     * Callback handler for when a habit is clicked in the dayHabitsListView.
     * When a habit is clicked, it checks if a habit is completed for the clicked date.
     * If it is done, it checks if a habit event has already been completed for the day.
     * Else, it prompts the user to add a habit event.
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
    private boolean onDayHabitsClick(AdapterView<?> adapterView, View view, int position, long id) {
        DayHabits habit = (DayHabits) adapterView.getAdapter().getItem(position);
        CardView cardView = view.findViewById(R.id.card_view);
        habitEventTitle = habit.getDayHabitTitle() + ": " + clickedDateStr;

        // if the habit is completed for the current day, the user can add a habit event
        if ((cardView.getCardBackgroundColor().getDefaultColor() ==
                ContextCompat.getColor(getContext(), R.color.red_main))) {

            // need to check if a habit event already exists for the current habit for that day
            DocumentReference documentReference = db.collection("Users").document(uid).
                    collection("Events").document(habitEventTitle);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // if a habit event already exists, generate a prompt for the user
                            Log.d(CHECK_IF_HABIT_EVENT_EXISTS, "Document exists");
                            Toast.makeText(getContext(), "You have already added a habit event for today. Please edit or delete your event by clicking the 'Habit Events' button on the Home page.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // else, prompt the user to add a habit event by going to another activity
                            Log.d(CHECK_IF_HABIT_EVENT_EXISTS, "No such document");
                            Intent intent = new Intent(getContext(), AddHabitEventActivity.class);
                            intent.putExtra(TITLE_DAY_HABIT, habit.getDayHabitTitle());
                            intent.putExtra(TITLE_HABIT_EVENT, habitEventTitle);
                            startActivity(intent);
                        }
                    } else {
                        Log.d(CHECK_IF_HABIT_EVENT_EXISTS, "get failed with ", task.getException());
                    }
                }
            });
        }
        // else, the user can assign completion to the habit given clicked day == current day
        else {
            Intent intent = new Intent(getContext(), DayHabitsActivity.class);
            intent.putExtra(TITLE_DAY_HABIT, habit.getDayHabitTitle());
            intent.putExtra(MOTIVATION, habit.getDayHabitReason());
            intent.putExtra(DATE_COMPARE_DAY_HABIT, isDateClickedEqualCurrent);
            intent.putExtra(CLICKED_DATE_STR, clickedDateStr);
            DayHabitsActivityResultLauncher.launch(intent);
        }
        return true;
    }

    /**
     * It changes the title of the current day's habits view
     */
    public void changeDayTitle() {
        MutableLiveData<String> date_title_mutable = new MutableLiveData<>();
        date_title_mutable.setValue(dayTitle);
        DayHabitsViewModel.setTitle(date_title_mutable);
        titleText = binding.dayHabitTitle;
        DayHabitsViewModel.getTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                titleText.setText(title);
            }
        });
    }

    /**
     * It takes a list of DayHabits from the document to be added to the habits list adapter
     */
    public void showDayHabits() {
        dayHabitsListView = binding.dayHabitsList;

        DayHabitsViewModel.getHabitsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<DayHabits>>() {
            @Override
            public void onChanged(ArrayList<DayHabits> dayHabitsList) {
                habitsAdapter = new DayHabitsAdapter(getContext(),dayHabitsList, clickedDateStr);
                dayHabitsListView.setAdapter(habitsAdapter);
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
