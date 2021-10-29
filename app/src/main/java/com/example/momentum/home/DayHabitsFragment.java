package com.example.momentum.home;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class DayHabitsFragment extends Fragment {
    private DayHabitsViewModel DayHabitsViewModel;
    private FragmentDayHabitsBinding binding;
    private FirebaseFirestore db;
    private String dayofWeek;
    private ListView habitsList;
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
                    String habit_title = doc.getId();
                    DayHabitsViewModel.addHabit(new DayHabits(habit_title));
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
            showDayHabits(habitsReference);
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

    public void showDayHabits(CollectionReference habitsReference) {
        habitsList = binding.dayHabitsList;

        DayHabitsViewModel.getHabitsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<DayHabits>>() {
            @Override
            public void onChanged(ArrayList<DayHabits> dayHabitsList) {
                habitsAdapter = new DayHabitsList(getContext(),dayHabitsList);
                habitsList.setAdapter(habitsAdapter);
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
