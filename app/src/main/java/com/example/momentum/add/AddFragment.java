package com.example.momentum.add;

import static android.content.ContentValues.TAG;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.momentum.Habit;
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
import java.util.Date;
import java.util.HashMap;

public class AddFragment extends Fragment{
    private AddViewModel AddViewModel;
    private FragmentAddHabitBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

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
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().length() > 20){
                    Log.w(TAG, "Title too long");
                    Toast.makeText(activity, "Title must be less than 20 chars",
                            Toast.LENGTH_SHORT).show();
                }
                else if (reason.getText().toString().length() > 30){
                    Log.w(TAG, "Reason too long");
                    Toast.makeText(activity, "Reason must be less than 30 chars",
                            Toast.LENGTH_SHORT).show();
                }
                else if(title == null || title.getText().toString().equals("")){
                    Log.w(TAG, "No Title Error");
                    Toast.makeText(activity, "Title Required",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        String t = title.getText().toString();
                        String r = reason.getText().toString();
                        Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date.getText().toString());
                        ArrayList<String> f = new ArrayList<String>();
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
                        Boolean p = privacy.isChecked();
                        Habit habit = new Habit(r, d, p, f);
                        HashMap<String, Habit> data = new HashMap<>();
                        data.put(t, habit);
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
                        Toast.makeText(activity, "Habit Created",
                                Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Habit_created");
                        Log.w(TAG, habit.toString());
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
