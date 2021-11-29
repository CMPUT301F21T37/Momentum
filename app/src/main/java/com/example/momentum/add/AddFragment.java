package com.example.momentum.add;

import static android.content.ContentValues.TAG;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.momentum.Habit;
import com.example.momentum.R;
import com.example.momentum.databinding.FragmentAddHabitBinding;
import com.example.momentum.habits.HabitsEditActivity;
import com.example.momentum.home.AddHabitEventActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A class that adds a habit to the database and updates the habit count.
 * @version 2.0
 * @author rittwage, misay
 */
public class AddFragment extends Fragment {
    public static final String UPDATE_COUNT = "UPDATE_COUNT";
    public static final String CHECK_IF_HABIT_EXISTS = "DOES_THE_HABIT_EXISTS";
    private FragmentAddHabitBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference collectionReference;

    //initialize interface values
    EditText title;
    EditText reason;
    EditText date;
    ToggleButton mon;
    ToggleButton tue;
    ToggleButton wed;
    ToggleButton thu;
    ToggleButton fri;
    ToggleButton sat;
    ToggleButton sun;
    Switch privacy;

    HashMap<String, Object> data;
    String title_str;
    ArrayList<String> frequency;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // binding the fragment to main activity
        binding = FragmentAddHabitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // binding the buttons/views
        title = binding.editTitle;
        reason = binding.editReason;
        date = binding.editDate;
        mon = binding.monButton;
        tue = binding.tueButton;
        wed = binding.wedButton;
        thu = binding.thuButton;
        fri = binding.friButton;
        sat = binding.satButton;
        sun = binding.sunButton;
        privacy = binding.habitPrivacy;
        Button create_button = binding.createHabitButton;

        // initializing the database
        collectionReference = db.collection("Users").document(uid).collection("Habits");

        // listener for when the data editText is clicked to show an DatePicker dialog
        date.setOnClickListener(this::DateTextOnClick);

        // listener for when the create habit button is clicked
        create_button.setOnClickListener(this::CreateButtonOnClick);

        return root;
    }

    /**
     * Callback handler for when the date editText is clicked
     * Prompt the user to enter a date using the datePicker dialog
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean DateTextOnClick(View view) {
        // initialization
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // datePicker listener
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        date.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
        datePicker.show();

        // changing the colors of the button
        datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_main));
        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_main));

        return true;
    }

    /**
     * Callback handler for when the create habit button is clicked
     * Do a series of checks to make sure that the inputs are correct
     * If at least one is incorrect, relay a toast message to the user
     * If all are correct, update the firebase database to add the habit
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean CreateButtonOnClick(View view) {
        frequency = new ArrayList<>();
        
        //series of if statements to check each toggle button
        if (mon.isChecked()){
            frequency.add("Monday");
        }
        if (mon.isChecked()){
            frequency.add("Tuesday");
        }
        if (wed.isChecked()){
            frequency.add("Wednesday");
        }
        if (thu.isChecked()){
            frequency.add("Thursday");
        }
        if (fri.isChecked()){
            frequency.add("Friday");
        }
        if (sat.isChecked()){
            frequency.add("Saturday");
        }
        if (sun.isChecked()){
            frequency.add("Sunday");
        }

        // If statement that checks for an empty title field, and prompts user to retry
        if(title == null || title.getText().toString().equals("")){
            Log.w(TAG, "No Title Error");
            Toast.makeText(getContext(), "Title Required",
                    Toast.LENGTH_SHORT).show();
        }
        else if(reason == null || reason.getText().toString().equals("")){
            Log.w(TAG, "No Reason Error");
            Toast.makeText(getContext(), "Reason Required",
                    Toast.LENGTH_SHORT).show();
        }
        else if (frequency.isEmpty()) {
            Toast.makeText(getContext(), "Please choose at least one day to do your habit!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            // collects info from the edit_title then checks if a habit with the same title already exists
            title_str = title.getText().toString();
            checkIfHabitExists();
        }
        return true;
    }

    /**
     * Check if a habit with the same title as entered by the user already exists
     * If it exists, show a toast to the user
     * Else, check the correct date format, if it's okay, start adding the habit to the database
     */
    private void checkIfHabitExists() {
        DocumentReference documentReference = db.collection("Users").document(uid).
                collection("Habits").document(title_str);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // if a habit with the same title already exists, generate a prompt
                        Log.d(CHECK_IF_HABIT_EXISTS, "Document exists");
                        Toast.makeText(getContext(), "You have already added a habit of the same title. Edit or delete your habit on the Habits page.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // else, add the habit
                        Log.d(CHECK_IF_HABIT_EXISTS, "No such document");
                        try {
                            String r = reason.getText().toString();

                            Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
                            //checks if the habit should be private or not
                            Boolean p = privacy.isChecked();

                            //hashmap of Strings and objects
                            data = new HashMap<>();
                            //puts key and object pairs in the data hashmap
                            data.put("date", d);
                            data.put("frequency",frequency);
                            data.put("private",p);
                            data.put("reason", r);

                            getOrder();
                        }
                        catch (ParseException e){
                            Log.w(TAG, "Date Parse Error");
                            Toast.makeText(getActivity(), "Invalid Date Format Entered", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d(CHECK_IF_HABIT_EXISTS, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Get the current number of the habits, then set that as the order of the new habit
     * On Success, add the data
     * then set a new count for all the habits
     */
    private void getOrder() {
        // creating reference to the count of the habits
        DocumentReference documentReference = db.collection("Users").document(uid).
                collection("HabitCount").document("count");

        documentReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String count_str = (String) documentSnapshot.getData().get("habits");
                        data.put("order", count_str);
                        addData();

                        // set a new count since we're adding a new habit
                        Integer count = Integer.valueOf(count_str);
                        Integer new_count = count + 1;
                        String new_count_str = new_count.toString();
                        setNewCount(new_count_str);
                    }
                });
    }

    /**
     * When all data are gathered, add it to the database
     * On success, make a toast to say that a habit is created then clear the fields
     */
    private void addData() {
        //uploads data into a firebase database into a document titled t
        collectionReference
                .document(title_str)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Data has been added successfully!");
                        //toast to show that a habit has been created
                        Toast.makeText(getActivity(), "Habit Created", Toast.LENGTH_LONG).show();
                        //call to the clear function to reset all changed variables in the habit fragment
                        clear();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }

    /**
     * Clear all the fields after a habit is created
     */
    private void clear(){
        //sets all input fields to their original values
        title.setText("");
        reason.setText("");
        date.setText("");
        mon.setChecked(false);
        tue.setChecked(false);
        wed.setChecked(false);
        thu.setChecked(false);
        fri.setChecked(false);
        sat.setChecked(false);
        sun.setChecked(false);
        privacy.setChecked(false);
    }

    /**
     * Set the new number of habits
     * @param count
     * new count of the habits in String format
     */
    private void setNewCount(String count) {
        /*
        To update a document:
        https://saveyourtime.medium.com/firebase-cloud-firestore-add-set-update-delete-get-data-6da566513b1b
         */
        DocumentReference documentReference = db.collection("Users").document(uid).
                collection("HabitCount").document("count");

        documentReference
                .update("habits", count)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(UPDATE_COUNT, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(UPDATE_COUNT, "Error updating document", e);
                    }
                });
    }

    /**
     * When view is destroyed, send binding to null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
