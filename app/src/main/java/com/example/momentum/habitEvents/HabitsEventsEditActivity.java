package com.example.momentum.habitEvents;

import static java.io.File.createTempFile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An activity that lets the user edit the details of their habit events.
 *
 * @author Kaye Ena Crayzhel F. Misay
 * @author Han Yan
 * @author Mohammed Alzafarani
 */
public class HabitsEventsEditActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static final String EDIT_EVENT = "EDIT EVENT";

    private boolean mCameraPermissionsGranted = false;
    private boolean mGalleryPermissionsGranted = false;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 102;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;


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
    private String imageNameStr;

    private FloatingActionButton backButton;
    private TextView titleView;
    private EditText reasonEdit;
    private FloatingActionButton editEventButton;

    private ImageView mImageView;
    private Button editCameraBtn, editGalleryBtn;
    private String currentPhotoPath;
    private StorageReference storageReference;
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
        binding = ActivityEditEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initializing the database
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        // a storage reference to save images
        storageReference = FirebaseStorage.getInstance().getReference();

        mImageView = binding.getImageView;

        // Get the Intent that started this activity and extract them
        Intent intent = getIntent();
        title = intent.getStringExtra(HabitEventsFragment.EVENT_TITLE);
        reason = intent.getStringExtra(HabitEventsFragment.EVENT_COMMENT);
        imageNameStr = intent.getStringExtra(HabitEventsFragment.EVENT_IMAGE);
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
                            event.getImageName().equals(event2.getImageName()) &&
                            event.getTitle().equals(event2.getTitle()) &&
                            event.getComment().equals(event2.getComment())) {

                        docName = doc.getId();
                    }
                }
            }
        });

        // initialize previous values
        initializeValues();

        editCameraBtn = binding.cameraBtn;
        editCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraPermission();

            }
        });

        editGalleryBtn = binding.galleryBtn;
        editGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGalleryPermission();
            }
        });

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

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            if(mCameraPermissionsGranted) {
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
        mCameraPermissionsGranted = false;
        mGalleryPermissionsGranted =false;

        switch (requestCode) {
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
                Toast.makeText(HabitsEventsEditActivity.this, "Image Is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HabitsEventsEditActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });



    }


    // create a file for image in gallery
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        String [] split = currentPhotoPath.split("Pictures/");
        imageNameStr = split[1];
        return image;
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

        // initializes the image
        if (imageNameStr != null) {
            setImage();
        }

    }

    // a method to get image uri and set it in the imageView`
    private void setImage(){
        StorageReference imageRef = storageReference.child("images/").child(imageNameStr);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(HabitsEventsEditActivity.this)
                        .load(uri)
                        .into(mImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HabitsEventsEditActivity.this, "No image uploaded for this event", Toast.LENGTH_SHORT).show();
            }
        });


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
        Event event = new Event(title, newComment, latitude, longitude, imageNameStr);


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
     * When the map is ready to start this method is called with a google map
     *
     * @param googleMap The ready google map
     */

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        // adding a marker on current user location
        LatLng latLng = new LatLng(latitude, longitude);
        moveCamera(latLng, DEFAULT_ZOOM);
        mMap.addMarker(new MarkerOptions().position(latLng));

    }


    /**
     * This is called when a user clicks on a map
     *
     * @param latLng The latLing the user clicked on
     */
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        Toast.makeText(HabitsEventsEditActivity.this, "Changed to marked location", Toast.LENGTH_SHORT).show();
    }
}
