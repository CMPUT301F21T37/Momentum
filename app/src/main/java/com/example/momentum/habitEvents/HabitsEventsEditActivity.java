package com.example.momentum.habitEvents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.momentum.R;
import com.example.momentum.databinding.ActivityEditEventsBinding;
import com.example.momentum.home.AddHabitEventActivity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * An activity that lets the user edit the details of their habit events.
 *
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 * @author Mohammed Alzafarani
 */
public class HabitsEventsEditActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static final String EDIT_EVENT = "EDIT EVENT";


    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 5;

    private ActivityEditEventsBinding binding;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private CollectionReference eventReference;
    private String docName;
    private Event event;

    private String title;
    private String reason;
    private double latitude;
    private double longitude;

    private FloatingActionButton backButton;
    private TextView titleView;
    private EditText reasonEdit;
    private FloatingActionButton editEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityEditEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitEventsFragment.EVENT_TITLE);
        reason = intent.getStringExtra(HabitEventsFragment.EVENT_COMMENT);
        latitude = intent.getDoubleExtra(HabitEventsFragment.EVENT_LATITUDE, 0);
        longitude = intent.getDoubleExtra(HabitEventsFragment.EVENT_LONGITUDE, 0);
        event = (Event) intent.getSerializableExtra(HabitEventsFragment.EVENT_OBJECT);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        CollectionReference eventsReference = db.collection("Users").document(uid).collection("Events");

        eventsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Event event2 = (Event) doc.toObject(Event.class);
                    if (event.getLatitude() == event2.getLatitude() &&
                            event.getLongitude() == event2.getLongitude() &&
                            event.getTitle().equals(event2.getTitle()) &&
                            event.getComment().equals(event2.getComment())) {

                        docName = doc.getId();
                    }
                }
            }
        });

        // initialize previous values
        initializeValues();

        // back button to go back to previous fragment
        backButton = binding.editEventBack;
        backButton.setOnClickListener(this::backButtonOnClick);

        // listener for when the check button is clicked
        editEventButton = binding.editEventDone;
        editEventButton.setOnClickListener(this::editEventButtonOnClick);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.editMap);

        mapFragment.getMapAsync(this);
    }

    /**
     * A method that sets previous inputs from the user.
     */
    public void initializeValues() {
        // initializes the title
        titleView = binding.editEventTitle;
        titleView.setText(title);

        // initializes the reason
        reasonEdit = binding.editHabitEventComment;
        reasonEdit.setText(reason);
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
     * Callback handler for when the edit button is clicked.
     * Calls a method to update the database.
     * Goes back to the previous fragment.
     *
     * @param view Current view associated with the listener.
     * @return 'true' to confirm with the listener
     */
    private boolean editEventButtonOnClick(View view) {
        // initialize collection reference
        eventReference = db.collection("Users").document(uid).collection("Events");

        // getting the new strings for newComment
        String newComment = reasonEdit.getText().toString();
        Event event = new Event(title, newComment, latitude, longitude);

        // updates the database then closes the activity

        editEventToDatabase(event);
        finish();
        return true;
    }

    /**
     * Edits a given event.
     *
     * @param event The data to be put in Events field comment.
     */
    private void editEventToDatabase(Event event) {
        String users_collection_name = "Users";
        String events_collection_name = "Events";

        // adds to a sub-collection of Habits of the current user
        final DocumentReference documentReference = db.collection(users_collection_name).document(uid).
                collection(events_collection_name).document(docName);

        documentReference
                .set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditEventToDatabase", "Data has been edited successfully!");
                        Toast.makeText(HabitsEventsEditActivity.this, "Event Updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("EditEventToDatabase", "Data could not be edited!" + e.toString());
                    }
                });
    }


    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        LatLng latLng = new LatLng(latitude, longitude);
        moveCamera(latLng, DEFAULT_ZOOM);
        mMap.addMarker(new MarkerOptions().position(latLng));

    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        Toast.makeText(HabitsEventsEditActivity.this, "Changed to marked location", Toast.LENGTH_SHORT).show();
    }
}
