package com.example.momentum.home;

import static java.io.File.createTempFile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.fragment.app.FragmentActivity;

import com.example.momentum.R;
import com.example.momentum.databinding.ActivityAddHabitEventBinding;
import com.example.momentum.habitEvents.Event;
import com.example.momentum.habitEvents.HabitsEventsEditActivity;
import com.example.momentum.utils.Constants;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An activity that lets the user add a habit event for when a habit is done for the day.
 *
 * @author Kaye Ena Crayzhel F. Misay
 * @author Mohammed Alzafarani
 */
public class AddHabitEventActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

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

    private boolean mCameraPermissionsGranted = false;
    private boolean mGalleryPermissionsGranted = false;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 102;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    private ImageView mImageView;
    private Button openCameraBtn, openGalleryBtn;
    private String currentPhotoPath;
    private StorageReference storageReference;
    private String imageName;
    private Context context;

    // a launcher for camera
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // set image in ImageView
                        // form a filename for images
                        File f = new File(currentPhotoPath);
                        Uri contentUri = Uri.fromFile(f);
                        mImageView.setImageURI(contentUri);

                        //call method to upload image to firebase storage
                        String fileName = f.getName();
                        uploadImageToFirebase(fileName, contentUri);
                    }
                }
            });


    // a launcher for gallery
    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // set image in ImageView
                        Uri contentUri = result.getData().getData();
                       // String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
                       // String imageFileName = "JPEG_" + timeStamp + getFileExt(contentUri);
                       // Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);
                        File f = new File(currentPhotoPath);
                        mImageView.setImageURI(contentUri);
                        String fileName = f.getName();

                        //call method to upload image to firebase storage
                        uploadImageToFirebase(fileName, contentUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityAddHabitEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAddHabit);

        mapFragment.getMapAsync((OnMapReadyCallback) this);

        getLocationPermission();

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // Get the Intent that started this activity and extract the strings
        Intent intent = getIntent();
        title = intent.getStringExtra(Constants.TITLE_DAY_HABIT);
        documentTitle = intent.getStringExtra(Constants.TITLE_HABIT_EVENT);

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

        // a storage reference to save images
        storageReference = FirebaseStorage.getInstance().getReference();

        mImageView = binding.getImageView;


        openCameraBtn = binding.cameraBtn;
        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraPermission();

            }
        });

        openGalleryBtn = binding.galleryBtn;
        openGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGalleryPermission();
            }
        });


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
                        LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
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


    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            if (mCameraPermissionsGranted) {
                imageFile = createImageFile();
                Uri contentUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                cameraActivityResultLauncher.launch(takePictureIntent);
            }
        } catch (IOException ex) {

        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        File imageFile = null;
        try{
            if (mGalleryPermissionsGranted){
                imageFile = createImageFile();
                Uri contentUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", imageFile);
                gallery.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                galleryActivityResultLauncher.launch(gallery);
            }
        } catch (IOException ex){

        }
    }

    // a method to upload image to firebase storage
    private void uploadImageToFirebase(String fileName, Uri contentUri) {
        //save image in storage
        StorageReference image = storageReference.child("images/" + fileName);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mImageView);
                    }
                });
                Toast.makeText(AddHabitEventActivity.this, "Image Is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddHabitEventActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    // create a file for image in gallery
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        String [] split = currentPhotoPath.split("Pictures/");
        imageName = split[1];
        return image;
    }


    /**
     * Callback handler for when the back button is clicked.
     * Goes back to the previous fragment.
     *
     * @param view Current view associated with the listener.
     * @return 'true' to confirm with the listener
     */
    private boolean backButtonOnClick(View view) {
        mImageView.setImageURI(null);
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
            event = new Event(title, comment, 0, 0, imageName);
        } else {
            event = new Event(title, comment, userLocation.getLatitude(), userLocation.getLongitude(), imageName);
        }


        // make a call to the database and then close the activity
        addHabitEventToDatabase(event);
        mImageView.setImageURI(null);
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

    private void getGalleryPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                READ_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mGalleryPermissionsGranted = true;
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this, permissions, GALLERY_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions, GALLERY_PERMISSION_REQUEST_CODE);
        }

    }


    private void getCameraPermission() {
        String[] permissions = {Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            mCameraPermissionsGranted = true;
            openCamera();
        } else{
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_REQUEST_CODE);
        }

    }


    //check if the permission is given to the app
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;
        mCameraPermissionsGranted = false;
        mGalleryPermissionsGranted =false;

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
            case CAMERA_PERMISSION_REQUEST_CODE: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mCameraPermissionsGranted = false;
                        return;
                    }
                }
                mCameraPermissionsGranted = true;
            }
            case GALLERY_PERMISSION_REQUEST_CODE: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mGalleryPermissionsGranted = false;
                        return;
                    }
                }
                mGalleryPermissionsGranted = true;
            }
        }
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setOnMapClickListener((GoogleMap.OnMapClickListener) this);

    }


    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        userLocation = new Location("");
        userLocation.setLatitude(latLng.latitude);
        userLocation.setLongitude(latLng.longitude);
        Toast.makeText(AddHabitEventActivity.this, "Marked location selected!", Toast.LENGTH_SHORT).show();
    }
}





