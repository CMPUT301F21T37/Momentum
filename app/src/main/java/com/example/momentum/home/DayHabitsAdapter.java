package com.example.momentum.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.momentum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A custom list that extends to an array adapter that keeps the list of dayHabits up-to-date.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class DayHabitsAdapter extends ArrayAdapter<DayHabits> {
    private static final String TAG = "DOCUMENT_EXISTENCE";

    private ArrayList<DayHabits> habits;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private String clickedDateStr;

    public DayHabitsAdapter(Context context, ArrayList<DayHabits> habits, String clickedDateStr){
        super(context, 0, habits);
        this.habits = habits;
        this.context = context;
        this.clickedDateStr = clickedDateStr;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_card_view, parent,false);
        }
        DayHabits habit = habits.get(position);

        // change the color of the card view and the habit title if the habit is completed for that day
        changeViewGivenHabitCompletion(habit.getDayHabitTitle(), view);

        // sets the habit list names
        TextView habitTitle = view.findViewById(R.id.card_view_text);
        habitTitle.setText(habit.getDayHabitTitle());

        return view;
    }

    /**
     * Changes the display of the card view of a given habit.
     * If the habit is completed for the clicked date: red_main background with white text color.
     * Else: default dark grey background with black text color.
     * @param habit_title
     * The habit name of the current habit.
     * @param view
     * Current view associated with the adapter.
     */
    private void changeViewGivenHabitCompletion(String habit_title, View view) {
        String users_collection_name = "Users";
        String habits_collection_name = "Habits";
        String done_dates_collection_name = "Done dates";
        CardView cardView = view.findViewById(R.id.card_view);
        TextView habitTitle = view.findViewById(R.id.card_view_text);

        // instance of the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        DocumentReference documentReference = db.collection(users_collection_name).document(uid).
                collection(habits_collection_name).document(habit_title).
                collection(done_dates_collection_name).document(clickedDateStr);

        /*
        How to check if a document exists in a collection
        https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection/53335711
        Author: Alex Mamo
        */
        // checks if the clicked habit on DayHabitsFragment is already completed for the day
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // if the habit is completed for the clicked date, change the color to red_main
                        int redBackgroundColor = ContextCompat.getColor(context, R.color.red_main);
                        int white = ContextCompat.getColor(context, R.color.white);
                        cardView.setCardBackgroundColor(redBackgroundColor);
                        habitTitle.setTextColor(white);
                        Log.d(TAG, "Document exists!");
                    } else {
                        // do the default color in the xml file
                        Log.d(TAG, "Document does not exist!");
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}
