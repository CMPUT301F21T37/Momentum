package com.example.momentum.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.momentum.databinding.FragmentDayHabitsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DayHabitsFragment extends Fragment {
    private DayHabitsViewModel DayHabitsViewModel;
    private FragmentDayHabitsBinding binding;
    private FirebaseFirestore db;
    private String dayofWeek;
    private ListView dayHabitsListView;
    private TextView titleText;
    private ArrayAdapter<DayHabits> habitsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DayHabitsViewModel = new ViewModelProvider(this).get(DayHabitsViewModel.class);
        binding = FragmentDayHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        final CollectionReference habitsReference = db.collection("Habits");

        habitsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                DayHabitsViewModel.clearHabitsList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    ArrayList<?> frequency = (ArrayList<?>) doc.getData().get("frequency");
                    if (frequency != null && frequency.contains(dayofWeek)) {
                        String habit_title = doc.getId();
                        String reason = (String) doc.getData().get("reason");
                        DayHabitsViewModel.addHabit(new DayHabits(habit_title, reason));
                    }
                }
                habitsAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        });

        /*
        receiving the data date to the day habits fragment
        https://www.youtube.com/watch?v=iVxKMZ8sGXY
        Author: Oum Saokosal
         */
        Bundle bundle = getArguments();
        if (bundle != null){
            dayofWeek = bundle.getString("dayOfWeek");
            String date_title = bundle.getString("dateTitle");
            MutableLiveData<String> date_title_mutable = new MutableLiveData<>();
            date_title_mutable.setValue(date_title);
            DayHabitsViewModel.setTitle(date_title_mutable);
            changeDayTitle();
            showDayHabits();
        }

        return root;
    }

    /**
     * It changes the title of the current day's habits view
     */
    public void changeDayTitle() {
        titleText = binding.dayHabitTitle;
        DayHabitsViewModel.getTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                titleText.setText(title);
            }
        });
    }

    /**
     * It takes a list of DayHabits from the document to added to the habits list adapter
     */
    public void showDayHabits() {
        dayHabitsListView = binding.dayHabitsList;

        DayHabitsViewModel.getHabitsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<DayHabits>>() {
            @Override
            public void onChanged(ArrayList<DayHabits> dayHabitsList) {
                habitsAdapter = new DayHabitsList(getContext(),dayHabitsList);
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
