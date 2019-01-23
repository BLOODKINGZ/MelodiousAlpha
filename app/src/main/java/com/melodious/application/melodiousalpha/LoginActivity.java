package com.melodious.application.melodiousalpha;

import android.content.Intent;
import android.preference.PreferenceGroup;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmailField, mPasswordField;
    private Button loginButton;
    private TextView mLoginStatus, goSignUp;
    private ProgressBar mLoginProgressBar;
    private FirebaseAuth mAuth;

    private String TAG = "LoginTag";

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if User is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
// [END on_start_check_user]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.login_email);
        mPasswordField = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        mLoginProgressBar = findViewById(R.id.login_progress_bar);
        mLoginStatus = findViewById(R.id.login_status);
        goSignUp = findViewById(R.id.go_sign_up);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(LoginActivity.this, RecorderActivity.class);
                //finish();
                //startActivity(intent);
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });

        goSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the User.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mLoginStatus.setVisibility(View.VISIBLE);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void hideProgressDialog(){
        mLoginProgressBar.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        goSignUp.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog() {
        loginButton.setVisibility(View.GONE);
        goSignUp.setVisibility(View.GONE);
        mLoginProgressBar.setVisibility(View.VISIBLE);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        }
        else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmailField.setError("Valid Email Address Required.");
                valid = false;
            }
            else {
                mEmailField.setError(null);
            }
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            //mLoginStatus.setText(getString(R.string.emailpassword_status_fmt, User.getEmail(), User.isEmailVerified()));

            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, User.getUid()))

            Intent intent = new Intent(LoginActivity.this, RecorderActivity.class);
            finish();
            startActivity(intent);
        }
        else {
            //mLoginStatus.setText(R.string.signed_out);
            //mDetailTextView.setText(null);

            loginButton.setVisibility(View.VISIBLE);
            mEmailField.setVisibility(View.VISIBLE);
        }
    }
}
