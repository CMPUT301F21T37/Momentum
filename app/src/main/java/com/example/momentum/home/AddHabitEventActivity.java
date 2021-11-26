package com.example.momentum.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.momentum.databinding.ActivityAddHabitEventBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * An activity that lets the user add a habit event for when a habit is done for the day.
 * @author Kaye Ena Crayzhel F. Misay
 */
public class AddHabitEventActivity extends AppCompatActivity {
    private static final String TAG = "ADD_HABIT_EVENT";

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
    private static final int PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 105;

    private ImageView mImageView;
    private Button openCameraBtn, openGalleryBtn;
    private String currentPhotoPath;
    private CollectionReference imageUriReference;
    private DocumentReference eventReference;
    private StorageReference storageReference;
    private Context context;

    ActivityResultLauncher<Intent> CameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == CAMERA_REQUEST_CODE) {
                        if (result.getResultCode() == RESULT_OK) {
                            File f = new File(currentPhotoPath);
                            mImageView.setImageURI(Uri.fromFile(f));
                            Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f));

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mediaScanIntent);

                            uploadImageToFirebase(f.getName(), contentUri);
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> GalleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == GALLERY_REQUEST_CODE) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri contentUri = result.getData().getData();
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "JPEG_" + timeStamp + getFileExt(contentUri);
                            Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);
                            mImageView.setImageURI(contentUri);

                            uploadImageToFirebase(imageFileName, contentUri);
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // creates the activity view
        binding = ActivityAddHabitEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        backButton.setOnClickListener(this :: backButtonOnClick);

        // title of the current day habit for the habit event
        activityTitle = binding.AddHabitEventTitle;
        activityTitle.setText(title);

        // optional comment for the habit event
        commentField = binding.AddHabitEventComment;

        // add/check button to add the habit event
        checkButton = binding.addHabitEventDone;
        checkButton.setOnClickListener(this :: checkButtonOnClick);

        storageReference = FirebaseStorage.getInstance().getReference();
        eventReference = db.collection("Users").document(uid).
                collection("Events").document(title);

        mImageView = binding.getImageView;

        openCameraBtn = binding.cameraBtn;
        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });

        openGalleryBtn = binding.galleryBtn;
        openGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                GalleryActivityResultLauncher.launch(gallery);
                //startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });


    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.CAMERA}, PERMISSION_CODE);
        }
        dispatchTakePictureIntent();
    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user pressed Allow or Deny from Permission Request Popup
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void uploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child("images/" + name);
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



        imageUriReference = eventReference.collection("Images" + name);
        imageUriReference.document(title)
                .set(contentUri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("tag", "Image Saved Successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", "Image Uploaded Fail." + e.toString()); }
                });



    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(); //wrong

            } catch (IOException ex) {
                // Error occurred while creating the File
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    CameraActivityResultLauncher.launch(takePictureIntent);

                }
                CameraActivityResultLauncher.launch(takePictureIntent);

            }
            // Continue only if the File was successfully created

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

    /**
     * Callback handler for when the check button is clicked.
     * Calls a method to add a habit event with optional comment.
     * Goes back to the previous fragment.
     * @param view
     * Current view associated with the listener.
     * @return
     * 'true' to confirm with the listener
     */
    private boolean checkButtonOnClick(View view) {
        final String comment = commentField.getText().toString();
        // create a hashmap to be inputted
        HashMap<String, String> data = new HashMap<>();
        data.put("habit", title);

        if (comment.trim().length()>0) { // for when there exists a comment
            data.put("comment", comment);
        }
        else { // for when the comment is empty
            data.put("comment", "");
        }
        // make a call to the database and then close the activity
        addHabitEventToDatabase(data);
        finish();
        return true;
    }

    /**
     * Adds habit event for the given habit.
     * @param data
     * The data to be put in the Events document fields.
     * Currently: optional comment and habit
     */
    public void addHabitEventToDatabase(HashMap<String,String> data) {
        String users_collection_name = "Users";
        String habit_events_collection_name = "Events";

        // adds to a sub-collection of Habits of the current user
        final CollectionReference collectionReference = db.collection(users_collection_name).document(uid).
                collection(habit_events_collection_name);

        collectionReference
                .document(documentTitle)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString()); }
                });
    }
}
