package com.example.momentum.habits;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.momentum.Habit;
import com.example.momentum.MainActivity;
import com.example.momentum.databinding.ActivityEditHabitBinding;
import com.example.momentum.login.LoginActivity;
import com.example.momentum.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * An activity that lets the user edit a habit.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class HabitsEditActivity extends AppCompatActivity {
    public static final String EDIT_HABIT = "EDIT HABIT";

    private ActivityEditHabitBinding binding;

    // database initialization
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference habitsReference;

    // intent extras from fragment
    private String title;
    private String reason;
    private ArrayList<?> frequency;
    private Boolean isPrivate;
    private Date date;

    // buttons to check
    private FloatingActionButton backButton;
    private EditText titleEdit;
    private EditText reasonEdit;
    private Button editHabitButton;
    private TextView startDate;
    private ToggleButton mon;
    private ToggleButton tue;
    private ToggleButton wed;
    private ToggleButton thu;
    private ToggleButton fri;
    private ToggleButton sat;
    private ToggleButton sun;
    private SwitchCompat privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityEditHabitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(Constants.HABIT_TITLE);
        reason = intent.getStringExtra(Constants.HABIT_REASON);
        isPrivate = intent.getBooleanExtra(Constants.HABIT_PRIVACY, true);
        date = (Date) intent.getSerializableExtra(Constants.HABIT_DATE);
        frequency = (ArrayList<?>) intent.getStringArrayListExtra(Constants.HABIT_FREQUENCY);

        // initialize previous values
        initializeValues();

        // back button to go back to previous fragment
        backButton = binding.editHabitBack;
        backButton.setOnClickListener(this::backButtonOnClick);

        // listener for when the startDate is clicked
        startDate.setOnClickListener(this::startDateOnClick);

        // listener for when the 'EDIT HABIT' button is clicked
        editHabitButton = binding.editConfirmButton;
        editHabitButton.setOnClickListener(this::editHabitButtonOnClick);
    }

    /**
     * A method that sets previous inputs from the user.
     */
    public void initializeValues() {
        // initializes the title
        titleEdit = binding.habitTitleText;
        titleEdit.setText(title);

        // initializes the reason
        reasonEdit = binding.motivationText;
        reasonEdit.setText(reason);

        // initializes the starting date
        String str_date = formatDate();
        startDate = binding.dateText;
        startDate.setText(str_date);

        // initializes privacy
        privacy = binding.editPrivacySwitch;
        privacy.setChecked(isPrivate);

        // initializes frequency
        mon = binding.monButton;
        tue = binding.tueButton;
        wed = binding.wedButton;
        thu = binding.thuButton;
        fri = binding.friButton;
        sat = binding.satButton;
        sun = binding.sunButton;
        checkFrequency();
    }

    /**
     * Helper method to format the date
     * @return
     * An instance of date that is string
     */
    private String formatDate() {
        String month = (String) DateFormat.format("MMMM", date);
        String day = (String) DateFormat.format("dd", date);
        String year = (String) DateFormat.format("yyyy", date);
        String str_date = month + " " + day + ", " + year;

        return str_date;
    }

    /**
     * Helper method to check frequency
     */
    private void checkFrequency() {
        for (int i = 0; i < frequency.size(); i++) {
            String day = (String) frequency.get(i);
            switch(day) {
                case "Monday":
                    mon.setChecked(true);
                    break;
                case "Tuesday":
                    tue.setChecked(true);
                    break;
                case "Wednesday":
                    wed.setChecked(true);
                    break;
                case "Thursday":
                    thu.setChecked(true);
                    break;
                case "Friday":
                    fri.setChecked(true);
                    break;
                case "Saturday":
                    sat.setChecked(true);
                    break;
                case "Sunday":
                    sun.setChecked(true);
                    break;
            }
        };
    }

    /**
     * Callback handler for when the start delete text is clicked.
     * Starting date is not changeable (since it's the starting date)
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean startDateOnClick(View view) {
        Toast.makeText(HabitsEditActivity.this, "You cannot change your habit start date.",
                Toast.LENGTH_SHORT).show();
        return true;
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
     * Callback handler for when the edit button is clicked.
     * Calls a method to update the database.
     * Goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean editHabitButtonOnClick(View view) {
        // initialize collection reference
        habitsReference = db.collection("Users").document(uid).collection("Habits");

        // getting the new strings for title and reason
        String newTitle = titleEdit.getText().toString();
        String newReason = reasonEdit.getText().toString();

        // getting the new frequencies
        ArrayList<String> newFrequency = new ArrayList<>();
        if (mon.isChecked()){
            newFrequency.add("Monday");
        }
        if (mon.isChecked()){
            newFrequency.add("Tuesday");
        }
        if (wed.isChecked()){
            newFrequency.add("Wednesday");
        }
        if (thu.isChecked()){
            newFrequency.add("Thursday");
        }
        if (fri.isChecked()){
            newFrequency.add("Friday");
        }
        if (sat.isChecked()){
            newFrequency.add("Saturday");
        }
        if (sun.isChecked()){
            newFrequency.add("Sunday");
        }

        if (newTitle.trim().length() == 0) {
            Toast.makeText(HabitsEditActivity.this, "Title must not be left blank.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (newReason.trim().length() == 0) {
            Toast.makeText(HabitsEditActivity.this, "Reason must not be left blank.",
                    Toast.LENGTH_SHORT).show();
        }
        // if the new frequency is empty, show a prompt to the customer saying they should choose at least one
        else if (newFrequency.isEmpty()) {
            Toast.makeText(HabitsEditActivity.this, "Please choose at least one day to do your habit!",
                    Toast.LENGTH_SHORT).show();
        }
        // else, proceed with the update since all checks have been made
        else {
            // getting the privacy of the given habit
            Boolean newIsPrivate = privacy.isChecked();

            // add to a hashmap to update database
            HashMap<String, Object> data = new HashMap<>();
            data.put("date", date);
            data.put("frequency",newFrequency);
            data.put("private",newIsPrivate);
            data.put("reason", newReason);

            // get the current order
            DocumentReference documentReference = db.collection("Users").document(uid).
                    collection("Habits").document(title);

            documentReference
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String order_str = (String) documentSnapshot.getData().get("order");
                            data.put("order", order_str);

                            // if the title has changed, delete the old document (based on database structure)
                            if (!newTitle.equals(title)) {
                                editHabitToDatabaseNew(data, newTitle);
                            }
                            else {
                                // updates the database then closes the activity
                                editHabitToDatabaseOld(data, newTitle);
                            }
                        }
                    });
        }
        return true;
    }

    /**
     * Edits a given habit.
     * @param data
     * The data to be put in Habit fields.
     */
    private void editHabitToDatabaseOld(HashMap<String, Object> data, String newTitle) {
        String users_collection_name = "Users";
        String habits_collection_name = "Habits";

        // adds to a sub-collection of Habits of the current user
        final CollectionReference collectionReference = db.collection(users_collection_name).document(uid).
                collection(habits_collection_name);

        // this will overwrite the document since it is unknown what the user will change to use .update()
        collectionReference
                .document(newTitle)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(EDIT_HABIT, "Data has been added successfully!");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(EDIT_HABIT, "Data could not be added!" + e.toString());
                    }
                });
    }

    /**
     * Edits a given habit.
     * @param data
     * The data to be put in Habit fields.
     */
    private void editHabitToDatabaseNew(HashMap<String, Object> data, String newTitle) {
        String users_collection_name = "Users";
        String habits_collection_name = "Habits";

        // adds to a sub-collection of Habits of the current user
        final CollectionReference collectionReference = db.collection(users_collection_name).document(uid).
                collection(habits_collection_name);

        // this will overwrite the document since it is unknown what the user will change to use .update()
        collectionReference
                .document(newTitle)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(EDIT_HABIT, "Data has been added successfully!");
                        // delete the old activity
                        habitsReference
                                .document(title)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(HabitsAdapter.DELETE_HABIT, "DocumentSnapshot successfully deleted!");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(HabitsAdapter.DELETE_HABIT, "Error Deleting document", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(EDIT_HABIT, "Data could not be added!" + e.toString());
                    }
                });
    }
}
