package com.example.momentum.camera;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.momentum.databinding.ActivityAddHabitEventBinding;
import com.example.momentum.home.DayHabitsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
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
import java.util.Locale;

public class Camera extends FragmentActivity {


    private ActivityAddHabitEventBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;
    private String documentTitle;

    private static final int PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 105;

    private ImageView mImageView;
    private Button openCameraBtn, openGalleryBtn;
    private String currentPhotoPath;
    private CollectionReference imageCollectionReference;
    private DocumentReference eventReference;
    private StorageReference storageReference;
    private Uri imageUri;
    private Context context;


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
        documentTitle = intent.getStringExtra(DayHabitsFragment.TITLE_HABIT_EVENT);

        // a storage reference to save images
        storageReference = FirebaseStorage.getInstance().getReference();

        // a document reference of current event
        eventReference = db.collection("Users").document(uid).
                collection("Events").document(documentTitle);

        // a collection reference for image uri
        imageCollectionReference = eventReference.collection("Images");

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
            }
        });



    }

    private ActivityResultLauncher<Intent> CameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == CAMERA_REQUEST_CODE) {
                        if (result.getResultCode() == RESULT_OK) {
                            // set image in ImageView
                            File f = new File(currentPhotoPath);
                            imageUri = Uri.fromFile(f);
                            mImageView.setImageURI(imageUri);

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(imageUri);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(mediaScanIntent);

                            //call method to upload image to firebase storage
                            uploadImageToFirebase();
                        }
                    }
                }
            });


    private ActivityResultLauncher<Intent> GalleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == GALLERY_REQUEST_CODE) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            // set image in ImageView
                            imageUri = result.getData().getData();
                            mImageView.setImageURI(imageUri);

                            //call method to upload image to firebase storage
                            uploadImageToFirebase();
                        }
                    }
                }
            });

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.CAMERA}, PERMISSION_CODE);
        }
        dispatchTakePictureIntent();
    }




    // a method to upload image to firebase storage
    private void uploadImageToFirebase() {
        // form a filename for images
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);

        //save image in storage
        StorageReference image = storageReference.child("images/" + fileName);
        image.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mImageView);
                    }
                });
                Toast.makeText(Camera.this, "Image Is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Camera.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // save image uri in event collection
        imageCollectionReference
                .add(imageUri)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("tag", "Image Uri is uploaded.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tag", "Image Uri is fail to upload.");
            }
        });



    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
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
                    imageUri = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    CameraActivityResultLauncher.launch(takePictureIntent);

                }
                CameraActivityResultLauncher.launch(takePictureIntent);

            }
            // Continue only if the File was successfully created

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera.", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
