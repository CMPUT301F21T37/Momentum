package com.example.momentum.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.momentum.R;
import com.example.momentum.databinding.ActivityAddHabitEventBinding;
import com.example.momentum.habitEvents.Event;
import com.example.momentum.habitEvents.HabitsEventsEditActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * An activity that lets the user add a habit event for when a habit is done for the day.
 *
 * @author Kaye Ena Crayzhel F. Misay
 * @author Mohammed Alzafarani
 */
public class AddHabitEventActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private static final String TAG = "ADD_HABIT_EVENT";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private ActivityAddHabitEventBinding binding;
    private FloatingActionButton backButton;
    private FloatingActionButton checkButton;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private String title;
    private String documentTitle;
    private TextView activityTitle;
    private EditText commentField;
    private Button selectCurrentLocation;

    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 5;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityAddHabitEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapAddHabit);

        mapFragment.getMapAsync(this);

        getLocationPermission();

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(DayHabitsFragment.TITLE_DAY_HABIT);
        documentTitle = intent.getStringExtra(DayHabitsFragment.TITLE_HABIT_EVENT);

        // back button to go back to previous dayHabitsFragment
        backButton = binding.addHabitEventBack;
        backButton.setOnClickListener(this::backButtonOnClick);

        // title of the current day habit for the habit event
        activityTitle = binding.AddHabitEventTitle;
        activityTitle.setText(title);

        // optional comment for the habit event
        commentField = binding.AddHabitEventComment;

        // add/check button to add the habit event
        checkButton = binding.addHabitEventDone;
        checkButton.setOnClickListener(this::checkButtonOnClick);

        selectCurrentLocation = binding.AddHabitEventUserLocationButton;
        selectCurrentLocation.setOnClickListener(this::selectCurrentLocationOnClick);


    }

    private void selectCurrentLocationOnClick(View view) {
        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        userLocation = (Location) task.getResult();
                        LatLng latLng = new  LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                        moveCamera(latLng, DEFAULT_ZOOM);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        Toast.makeText(AddHabitEventActivity.this, "Current Location Selected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddHabitEventActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    /**
     * Callback handler for when the back button is clicked.
     * Goes back to the previous fragment.
     *
     * @param view Current view associated with the listener.
     * @return 'true' to confirm with the listener
     */
    private boolean backButtonOnClick(View view) {
        finish();
        return true;
    }

    /**
     * Callback handler for when the check button is clicked.
     * Calls a method to add a habit event with optional comment.
     * Goes back to the previous fragment.
     *
     * @param view Current view associated with the listener.
     * @return 'true' to confirm with the listener
     */
    private boolean checkButtonOnClick(View view) {
        final String comment = commentField.getText().toString();
        // create a hashmap to be inputted
        Event event;
        if (userLocation == null) {
            event = new Event(title, comment, 0, 0);
        } else {
            event = new Event(title, comment, userLocation.getLatitude(), userLocation.getLongitude());
        }


        // make a call to the database and then close the activity
        addHabitEventToDatabase(event);
        finish();
        return true;
    }

    /**
     * Adds habit event for the given habit.
     *
     * @param event The data to be put in the Events document fields.
     *              Currently: optional comment and habit
     */
    public void addHabitEventToDatabase(Event event) {
        String users_collection_name = "Users";
        String habit_events_collection_name = "Events";

        // adds to a sub-collection of Habits of the current user
        final CollectionReference collectionReference = db.collection(users_collection_name).document(uid).
                collection(habit_events_collection_name);

        collectionReference
                .document(documentTitle)
                .set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            userLocation = (Location) task.getResult();
                            moveCamera(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Toast.makeText(AddHabitEventActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                getDeviceLocation();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setOnMapClickListener(this);

    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        userLocation = new Location("");
        userLocation.setLatitude(latLng.latitude);
        userLocation.setLongitude(latLng.longitude);
        Toast.makeText(AddHabitEventActivity.this, "Marked location selected!", Toast.LENGTH_SHORT).show();
    }
}


