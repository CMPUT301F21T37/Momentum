package com.example.momentum.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.MainActivity;
import com.example.momentum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText usernameText;
    EditText emailText;
    EditText passwordText;
    EditText confirmPasswordText;
    TextView signInRedirect;
    Button signUpButton;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Database
        db = FirebaseFirestore.getInstance();

        // Initialize class variables
        usernameText = findViewById(R.id.usernameSignUpScreen);
        emailText = findViewById(R.id.emailSignUpScreen);
        passwordText = findViewById(R.id.passwordSignUpScreen);
        confirmPasswordText = findViewById(R.id.confirmPasswordSignUpScreen);
        signInRedirect = findViewById(R.id.signInRedirect);
        signUpButton = findViewById(R.id.signUpButton);

        // user clicks on Sign In text
        signInRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent); //switch activity
            }
        });

        // user clicks on the signUp button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (usernameText == null || usernameText.getText().toString().equals("")) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter a username!",
                            Toast.LENGTH_SHORT).show();

                } else if (emailText == null || emailText.getText().toString().equals("")) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter your email!",
                            Toast.LENGTH_SHORT).show();

                } else if (passwordText == null || passwordText.getText().toString().equals("")) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter your password!",
                            Toast.LENGTH_SHORT).show();

                } else if (confirmPasswordText == null || confirmPasswordText.getText().toString().equals("")) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please confirm your password!",
                            Toast.LENGTH_SHORT).show();

                } else if (!checkEmail(emailText.getText().toString())) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter a correct email address!",
                            Toast.LENGTH_SHORT).show();

                } else if (!passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "The passwords don't match!",
                            Toast.LENGTH_SHORT).show();

                } else if (passwordText.getText().toString().length() < 8) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "The password must be at least 8 characters",
                            Toast.LENGTH_SHORT).show();

                } else {
                    // Sign up the user
                    Toast.makeText(SignUpActivity.this, "Signing Up!",
                            Toast.LENGTH_SHORT).show();
                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up success, update UI with the user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(SignUpActivity.this, "Sign Up Successful!",
                                                Toast.LENGTH_SHORT).show();
                                        setUsername(user);
                                        addUserToDatabase(user);
                                        updateUI(user);
                                    } else {
                                        // If sign up fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
            }
        });
    }

    /**
     * This method adds the user to the FireStore database
     *
     * @param user The user that just logged in
     */
    private void addUserToDatabase(FirebaseUser user) {
        String users_collection_name = "Users";
        String users_id = user.getUid();
        HashMap<String, String> emptyMap = new HashMap<>();

        db.collection(users_collection_name).document(users_id).set(emptyMap);
    }

    /**
     * This method sets the username of the user
     *
     * @param user The user that logged in.
     */
    private void setUsername(FirebaseUser user) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(usernameText.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });


    }

    /**
     * This method check if the email entered is correct
     *
     * @param email The email entered
     * @return A boolean indicating whether the email is valid or not
     */
    private boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    /**
     * This method sends the user to the MainActivity
     *
     * @param user The user that just logged in
     */
    public void updateUI(FirebaseUser user) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent); //switch activity
        SignUpActivity.this.finish(); // close Login activity
    }
}
