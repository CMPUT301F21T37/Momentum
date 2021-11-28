package com.example.momentum.habits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.momentum.databinding.ActivityVisualIndicatorBinding;
import com.example.momentum.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisualIndicatorActivity extends AppCompatActivity {
    private ActivityVisualIndicatorBinding binding;

    private String title;
    private String yearStr;
    private Integer yearInt;
    private ArrayList<?> frequency;
    private List<String> months;

    private FirebaseFirestore db;
    private String uid;

    private FloatingActionButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityVisualIndicatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set the database
        db = FirebaseFirestore.getInstance();

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(Constants.HABIT_TITLE);
        yearStr = intent.getStringExtra(Constants.HABIT_YEAR);
        uid = intent.getStringExtra(Constants.VISUAL_INDICATOR_USER);
        frequency = (ArrayList<?>) intent.getStringArrayListExtra(Constants.HABIT_FREQUENCY);

        // show the title
        setTitle();

        // initialize needed variables then set visual indicator
        yearInt = Integer.valueOf(yearStr);
        setVisualIndicator(title, uid);

        // back button to go back to previous fragment
        backButton = binding.visualIndicatorBack;
        backButton.setOnClickListener(this::backButtonOnClick);
    }


    /**
     * Helper method to set the title habit title
     */
    private void setTitle() {
        TextView activityTitle;
        activityTitle = binding.visualIndicatorTitle;
        activityTitle.setText(title);
    }

    /**
     * Sets the visual indicator of a given habit title and a given user
     * @param habit_title
     * Habit title to be checked
     * @param user_check
     * Current user to be checked
     */
    public void setVisualIndicator(String habit_title, String user_check) {
        months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

        Integer possibleDates;
        // setting the progress bar for every month
        for (int index = 0; index < months.size(); index++) {
            switch(months.get(index)) {
                case "Jan":
                    possibleDates = getPossibleDates(1);
                    getDoneDates("January", possibleDates);
                    break;
                case "Feb":
                    possibleDates = getPossibleDates(2);
                    getDoneDates("February", possibleDates);
                    break;
                case "Mar":
                    possibleDates = getPossibleDates(3);
                    getDoneDates("March", possibleDates);
                    break;
                case "Apr":
                    possibleDates = getPossibleDates(4);
                    getDoneDates("April", possibleDates);
                    break;
                case "May":
                    possibleDates = getPossibleDates(5);
                    getDoneDates("May", possibleDates);
                    break;
                case "Jun":
                    possibleDates = getPossibleDates(6);
                    getDoneDates("June", possibleDates);
                    break;
                case "Jul":
                    possibleDates = getPossibleDates(7);
                    getDoneDates("July", possibleDates);
                    break;
                case "Aug":
                    possibleDates = getPossibleDates(8);
                    getDoneDates("August", possibleDates);
                    break;
                case "Sep":
                    possibleDates = getPossibleDates(9);
                    getDoneDates("September", possibleDates);
                    break;
                case "Oct":
                    possibleDates = getPossibleDates(10);
                    getDoneDates("October", possibleDates);
                    break;
                case "Nov":
                    possibleDates = getPossibleDates(11);
                    getDoneDates("November", possibleDates);
                    break;
                case "Dec":
                    possibleDates = getPossibleDates(12);
                    getDoneDates("December", possibleDates);
                    break;
            }
        }
    }

    private Integer getPossibleDates(int monthCheck) {
        /*
        How to calculate how many week days in a month
        Reference: https://stackoverflow.com/questions/41456941/how-can-i-calculate-many-week-days-in-a-month
        By: Pradnyarani and edited by: Basil Bourque
         */
        LocalDate start = null;
        YearMonth month = YearMonth.of(yearInt, monthCheck);
        int possible_dates = 0;

        for (int index = 0; index < frequency.size(); index++) {
            if ("Monday".equals(frequency.get(index))) {
                start = month.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
            } else if ("Tuesday".equals(frequency.get(index))) {
                start = month.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
            } else if ("Wednesday".equals(frequency.get(index))) {
                start = month.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
            } else if ("Thursday".equals(frequency.get(index))) {
                start = month.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
            } else if ("Friday".equals(frequency.get(index))) {
                start = month.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
            } else if ("Saturday".equals(frequency.get(index))) {
                start = month.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            } else if ("Sunday".equals(frequency.get(index))) {
                start = month.atDay(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }

            int count = (int) ChronoUnit.WEEKS.between(start, month.atEndOfMonth()) + 1;
            possible_dates += count;
        }

        return (Integer) possible_dates;
    }

    private void getDoneDates(String curr_month, Integer possible_dates) {
        /*
        doing simple and compound queries
        Reference: https://firebase.google.com/docs/firestore/query-data/queries
        By: Firebase Documentation
         */
        CollectionReference collectionReference = db.collection("Users").document(uid).
                collection("Habits").document(title).collection("Done dates");

        collectionReference
                .whereEqualTo("month", curr_month)
                .whereEqualTo("year", yearStr)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // get the number of times the user has done the given habit in a given month and year
                            Integer done_dates = (Integer) task.getResult().size();
                            Log.d("count", "done dates: " + done_dates.toString());

                            // when done dates is computed, set the progress bar
                            updateProgressBar(curr_month, done_dates, possible_dates);
                        }
                    }
                });
    }

    private void updateProgressBar(String curr_month, Integer done_dates, Integer possible_dates) {
        // acquiring the percentage needed for the current progress bar
        double percentage = (done_dates / (double) possible_dates) * 100;
        int percentage_int = (int) percentage; // convert for progress bar update
        DecimalFormat decimalFormat = new DecimalFormat("###.#");
        String percentage_str = decimalFormat.format(percentage) + "%";

        /*
        Updating the progress bar (also referenced the use of its XML files)
        Reference:
        https://www.geeksforgeeks.org/how-to-create-circular-determinate-progressbar-in-android/
        By:GeeksForGeeks
         */
        switch(curr_month) {
            case "January":
                ProgressBar janBar = binding.progressBarJanuary;
                TextView janPercentage = binding.percentageJanuary;
                janBar.setProgress(percentage_int);
                janPercentage.setText(percentage_str);
                break;
            case "February":
                ProgressBar febBar = binding.progressBarFebruary;
                TextView febPercentage = binding.percentageFebruary;
                febBar.setProgress(percentage_int);
                febPercentage.setText(percentage_str);
                break;
            case "March":
                ProgressBar marBar = binding.progressBarMarch;
                TextView marPercentage = binding.percentageMarch;
                marBar.setProgress(percentage_int);
                marPercentage.setText(percentage_str);
                break;
            case "April":
                ProgressBar aprBar = binding.progressBarApril;
                TextView aprPercentage = binding.percentageApril;
                aprBar.setProgress(percentage_int);
                aprPercentage.setText(percentage_str);
                break;
            case "May":
                ProgressBar mayBar = binding.progressBarMay;
                TextView mayPercentage = binding.percentageMay;
                mayBar.setProgress(percentage_int);
                mayPercentage.setText(percentage_str);
                break;
            case "June":
                ProgressBar junBar = binding.progressBarJune;
                TextView junPercentage = binding.percentageJune;
                junBar.setProgress(percentage_int);
                junPercentage.setText(percentage_str);
                break;
            case "July":
                ProgressBar julBar = binding.progressBarJuly;
                TextView julPercentage = binding.percentageJuly;
                julBar.setProgress(percentage_int);
                julPercentage.setText(percentage_str);
                break;
            case "August":
                ProgressBar augBar = binding.progressBarAugust;
                TextView augPercentage = binding.percentageAugust;
                augBar.setProgress(percentage_int);
                augPercentage.setText(percentage_str);
                break;
            case "September":
                ProgressBar sepBar = binding.progressBarSeptember;
                TextView sepPercentage = binding.percentageSeptember;
                sepBar.setProgress(percentage_int);
                sepPercentage.setText(percentage_str);
                break;
            case "October":
                ProgressBar octBar = binding.progressBarOctober;
                TextView octPercentage = binding.percentageOctober;
                octBar.setProgress(percentage_int);
                octPercentage.setText(percentage_str);
                break;
            case "November":
                ProgressBar novBar = binding.progressBarNovember;
                TextView novPercentage = binding.percentageNovember;
                novBar.setProgress(percentage_int);
                novPercentage.setText(percentage_str);
                break;
            case "December":
                ProgressBar decBar = binding.progressBarDecember;
                TextView decPercentage = binding.percentageDecember;
                decBar.setProgress(percentage_int);
                decPercentage.setText(percentage_str);
                break;
        }
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
}


