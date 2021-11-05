package com.example.momentum.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.R;
import com.example.momentum.databinding.ActivityDayHabitsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * An activity that lets the user assign completion for the clicked habit iff the clicked date is the current date.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class DayHabitsActivity extends AppCompatActivity {
    private final String TAG = "ADD_DONE_DATE";
    private ActivityDayHabitsBinding binding;
    private FloatingActionButton backButton;
    private TextView activityTitle;
    private TextView habitReason;
    private Button doneHabitButton;
    private String title;
    private String reason;
    private String clickedDateStr;
    private Boolean isDateClickedEqualCurrent;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityDayHabitsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(DayHabitsFragment.TITLE_DAY_HABIT);
        reason = intent.getStringExtra(DayHabitsFragment.MOTIVATION);
        isDateClickedEqualCurrent = intent.getBooleanExtra(DayHabitsFragment.DATE_COMPARE_DAY_HABIT, true);
        clickedDateStr = intent.getStringExtra(DayHabitsFragment.CLICKED_DATE_STR);

        // back button to go back to previous dayHabitsFragment
        backButton = binding.dayHabitsBack;
        backButton.setOnClickListener(this :: backButtonOnClick);

        // title of the current day habit
        activityTitle = binding.DayHabitsTitleDone;
        activityTitle.setText(title);

        // motivation of the current day habit
        habitReason = binding.dayHabitsMotivation;
        SpannableStringBuilder display_reason = new SpannableStringBuilder("Motivation \n" + reason);
        StyleSpan bold = new StyleSpan(Typeface.BOLD);
        display_reason.setSpan(bold, 0,10, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make motivation bold
        habitReason.setText(display_reason);

        // 'DONE' button to set that a habit is done for the day (only work for today's date)
        doneHabitButton = binding.doneDayHabitButton;
        doneHabitButton.setOnClickListener(this :: doneButtonOnClick);

    }

    /**
     * Callback handler for when the back button is clicked.
     * Goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean backButtonOnClick(View view) {
        finish();
        return true;
    }

    /**
     * Callback handler for when the 'DONE' button is clicked.
     * It checks when the clicked date and the current day are the same.
     * If it is, set completion to the clicked habit.
     * If not, generate a prompt to show to the user and goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean doneButtonOnClick(View view) {
        if (!isDateClickedEqualCurrent) {
            // tell the user that they cannot complete a habit and address the reason
            String dayHabitsInfo = "The clicked date is not today's date. The habit cannot be completed.";
            showCustomToast(dayHabitsInfo);
        }
        else {
            // adds to the database of the current date, prompts the user to add a habit event,
            // and go back to previous fragment
            addDoneDateToDatabase();
            String addHabitEventInfo = "Click the habit again to add a habit event.";
            showCustomToast(addHabitEventInfo);
            setResult(RESULT_OK);
            finish();
        }
        return true;
    }

    /**
     * Helper method that creates a custom toast.
     * @param textData
     * The text to be shown on the custom toast.
     */
    private void showCustomToast(String textData) {
        /*
        creating a custom toast layout
        https://www.viralandroid.com/2015/09/custom-android-toast-example.html
        Author: Pacific Regmi
         */
        LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.customToast));

        // sets the text
        TextView toastText = (TextView) toastLayout.findViewById(R.id.customToastText);
        toastText.setText(textData);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * Adds the date that is clicked to the database given the clicked habit
     */
    private void addDoneDateToDatabase() {
        String users_collection_name = "Users";
        String habits_collection_name = "Habits";
        String done_dates_collection_name = "Done dates";
        HashMap<String, Boolean> data = new HashMap<>();

        // adds to a sub-collection of Events of the current user
        final CollectionReference collectionReference = db.collection(users_collection_name).document(uid)
                .collection(habits_collection_name).document(title).collection(done_dates_collection_name);

        data.put("done",true); // to fill data
        collectionReference
                .document(clickedDateStr)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString()); }
                });
    }
}
