package com.example.momentum.login;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        usernameText = findViewById(R.id.usernameSignUpScreen);
        emailText = findViewById(R.id.emailSignUpScreen);
        passwordText = findViewById(R.id.passwordSignUpScreen);
        confirmPasswordText = findViewById(R.id.confirmPasswordSignUpScreen);
        signInRedirect = findViewById(R.id.signInRedirect);
        signUpButton = findViewById(R.id.signUpButton);

        signInRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent); //switch activity
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usernameText == null || usernameText.getText().toString().equals("")){
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter a username!",
                            Toast.LENGTH_SHORT).show();

                }else if (emailText == null || emailText.getText().toString().equals("")){
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter your email!",
                            Toast.LENGTH_SHORT).show();

                }else if (passwordText == null || passwordText.getText().toString().equals("")){
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter your password!",
                            Toast.LENGTH_SHORT).show();

                }else if (confirmPasswordText == null || confirmPasswordText.getText().toString().equals("")){
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please confirm your password!",
                            Toast.LENGTH_SHORT).show();

                }else if (!checkEmail(emailText.getText().toString())){
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "Please enter a correct email address!",
                            Toast.LENGTH_SHORT).show();

                }else if (!passwordText.getText().toString().equals(confirmPasswordText.getText().toString())){
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "The passwords don't match!",
                            Toast.LENGTH_SHORT).show();

                }else if (passwordText.getText().toString().length() < 8){
                    Log.w(TAG, "LoginFailure");
                    Toast.makeText(SignUpActivity.this, "The password must be at least 8 characters",
                            Toast.LENGTH_SHORT).show();

                }else{
                    //TODO: SIGN THE USER UP
                    // TODO: make a user object in firebase
                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
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

    private boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
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
     * @param user The user that just logged in
     */
    public void updateUI(FirebaseUser user) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent); //switch activity
        SignUpActivity.this.finish(); // close Login activity
    }
}
