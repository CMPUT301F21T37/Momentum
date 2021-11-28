package com.example.momentum.habitEvents;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.momentum.R;
import com.example.momentum.databinding.ActivityIndivHabitEventViewBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * An activity that lets the user see their habit events and corresponding details.
 *
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 * @author Mohammed Alzafarani
 */
public class ViewHabitEventsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityIndivHabitEventViewBinding binding;
    private String title;
    private String reason;
    private double latitude;
    private double longitude;
    private FloatingActionButton backButton;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityIndivHabitEventViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitEventsFragment.EVENT_TITLE);
        reason = intent.getStringExtra(HabitEventsFragment.EVENT_COMMENT);
        latitude = intent.getDoubleExtra(HabitEventsFragment.EVENT_LATITUDE, 0);
        longitude = intent.getDoubleExtra(HabitEventsFragment.EVENT_LONGITUDE, 0);

        // set the displays
        setTitle();
        setComment();

        // Check if the user has the location permission
        getLocationPermission();

        // back button to go back to previous fragment
        backButton = binding.viewEventBack;
        backButton.setOnClickListener(this::backButtonOnClick);

    }


    /**
     * Helper method to set the title habit title
     */
    private void setTitle() {
        TextView activityTitle;
        activityTitle = binding.habitEvent;
        activityTitle.setText(title);
    }

    /**
     * Helper method to set the motivation/reason
     */
    private void setComment() {
        TextView motivation;
        motivation = binding.comment;
        motivation.setText(reason);
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
     * This moves the map camera to a latLing and a zoom
     *
     * @param latLng The latLing to go to
     * @param zoom   The zoom to display at
     */
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * This method initializes the map by calling support map fragment
     */
    private void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.individualMap);
        mapFragment.getMapAsync(ViewHabitEventsActivity.this);
    }

    /**
     * This method gets the location permission from the user.
     */
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    /**
     * This method requests the user to give access to their location
     *
     * @param requestCode  The request code
     * @param permissions  The permissions
     * @param grantResults The results
     */
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
                    initMap();
                }
            }
        }
    }

    /**
     * When the map is ready to start this method is called with a google map
     *
     * @param googleMap The ready google map
     */

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {

            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            LatLng latLng = new LatLng(latitude, longitude);
            moveCamera(latLng, DEFAULT_ZOOM);
            mMap.addMarker(new MarkerOptions().position(latLng));

        }


    }


}
