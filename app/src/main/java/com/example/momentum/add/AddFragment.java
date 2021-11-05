package com.example.momentum.add;

import static android.content.ContentValues.TAG;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @version 1.5
 * @author rittwage, misay
 *
 */
public class AddFragment extends Fragment{
    private AddViewModel AddViewModel;
    private FragmentAddHabitBinding binding;

    /**
     *
     */
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

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


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AddViewModel = new ViewModelProvider(this).get(AddViewModel.class);
        /**
         * Database
         */
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        binding = FragmentAddHabitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
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

        Activity activity = getActivity();
        final CollectionReference collectionreference = db.collection("Users").document(uid).collection("Habits");
        final Button create_button = binding.createHabitButton;

        // Date Picker dialog for edit_date
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                date.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePicker.show();
                datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_main));
                datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.red_main));
            }
        });
        // OnClickListener for the Create Habit button, when clicked reads all the other input
        // information to create a habit object with that info. This information is also
        // uploaded to the firestore database as fields in the habit document titled under title.
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If statement that checks for an empty title field, and prompts user to retry
                if(title == null || title.getText().toString().equals("")){
                    Log.w(TAG, "No Title Error");
                    Toast.makeText(activity, "Title Required",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    //try statement to ensure that when date is parsed invalid dates are not accepted
                    try {
                        //collects info from the edit_title, and edit_reason edittext objects
                        String t = title.getText().toString();
                        String r = reason.getText().toString();

                        Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
                        ArrayList<String> f = new ArrayList<String>();
                        //series of if statements to check each toggle button
                        if (mon.isChecked()){
                            f.add("Monday");
                        }
                        if (mon.isChecked()){
                            f.add("Tuesday");
                        }
                        if (wed.isChecked()){
                            f.add("Wednesday");
                        }
                        if (thu.isChecked()){
                            f.add("Thursday");
                        }
                        if (fri.isChecked()){
                            f.add("Friday");
                        }
                        if (sat.isChecked()){
                            f.add("Saturday");
                        }
                        if (sun.isChecked()){
                            f.add("Sunday");
                        }
                        //checks if the habit should be private or not
                        Boolean p = privacy.isChecked();
                        //constructor for the habit class
                        Habit habit = new Habit(t, r, d, p, f);
                        //hashmap of Strings and objects
                        HashMap<String, Object> data = new HashMap<>();
                        //puts key and object pairs in the data hashmap
                        data.put("date", d);
                        data.put("frequency",f);
                        data.put("private",p);
                        data.put("reason", r);

                        //uploads data into a firebase database into a document titled t
                        collectionreference
                                .document(t)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // These are a method which gets executed when the task is succeeded
                                        Log.d(TAG, "Data has been added successfully!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // These are a method which gets executed if there’s any problem
                                        Log.d(TAG, "Data could not be added!" + e.toString());
                                    }
                                });
                        //toast to show that a habit has been created
                        Toast.makeText(activity, "Habit Created",
                                Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Habit_created");
                        Log.w(TAG, habit.toString());
                        //call to the clear function to reset all changed variables in the habit fragment
                        clear();
                    }
                    catch (ParseException e){
                        Log.w(TAG, "Date Parse Error");
                        Toast.makeText(activity, "Invalid Date Format Entered", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return root;
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
