package com.example.momentum.sharing;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.momentum.Habit;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Custom View Holder for a given recycle view  and the Get followers' habits data with id
 * @author boxiao
 *
 */

public class SharingHabitViewModel extends ViewModel {
    private static final String TAG = SharingViewModel.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public MutableLiveData<List<Habit>> habits = new MutableLiveData<>();

    //Get followers' habits data with id
    public void getFollowerHabits(String id) {
        db.collection("Users")
                .document(id)
                .collection("Habits")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (queryDocumentSnapshots != null) {
                        List<Habit> habitList = new ArrayList<>();
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            // getting the data of a given habit document
                            Map<String, Object> data = doc.getData();
                            String reason = (String) data.get("reason");
                            ArrayList<?> frequency = (ArrayList<?>) data.get("frequency");
                            String habitTitle = doc.getId();
                            Boolean isPrivate = (Boolean) data.get("private");
                            Timestamp startTimestamp = (Timestamp) data.get("date");
                            String orderStr = (String) data.get("order");
                            Integer order = null;
                            if (orderStr != null) {
                                order = Integer.valueOf(orderStr);
                            }
                            Date startDate = null;
                            if (startTimestamp != null) {
                                startDate = startTimestamp.toDate();
                            }
                            // Filter private habits
                            if (isPrivate != null && !isPrivate) {
                                habitList.add(new Habit(habitTitle, reason, startDate, false, frequency, order));
                            }
                        }
                        habits.setValue(habitList);
                    }
                });
    }
}
