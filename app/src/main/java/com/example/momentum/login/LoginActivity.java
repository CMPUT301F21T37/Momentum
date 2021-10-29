package com.example.momentum.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.momentum.MainActivity;
import com.example.momentum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;
    Button loginButton;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize class variables
        emailText = findViewById(R.id.emailAddressEditText);
        passwordText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // click on login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: FIX THE ISSUE OF emailText and passwordText == NULL
                if (emailText == null) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(LoginActivity.this, "Please enter your email!",
                            Toast.LENGTH_SHORT).show();
                } else if (passwordText == null) {
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(LoginActivity.this, "Please enter your password!",
                            Toast.LENGTH_SHORT).show();
                } else {

                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * This method sends the user to the MainActivity
     * @param user The user that just logged in
     */
    public void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent); //switch activity
        LoginActivity.this.finish(); // close Login activity
    }
}
