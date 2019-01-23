package com.melodious.application.melodiousalpha;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melodious.application.melodiousalpha.Models.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText mUsernameField, mEmailField, mPasswordField;
    private Button registerButton;
    private TextView mStatusTextView, mDetailTextView, goSignIn;
    private FirebaseAuth mAuth;
    private ProgressBar registerProgressBar;
    private String TAG = "RegisterLog";

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameField = findViewById(R.id.register_username);
        mEmailField = findViewById(R.id.register_email);
        mPasswordField = findViewById(R.id.register_password);
        mStatusTextView = findViewById(R.id.register_status);
        mDetailTextView = findViewById(R.id.temp_details);
        registerButton = findViewById(R.id.register_button);
        registerProgressBar = findViewById(R.id.register_progress_bar);
        goSignIn = findViewById(R.id.go_sign_in);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });

        goSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            //mStatusTextView.setVisibility(View.GONE);

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            reference = database.getReference("users");

                            User userModel = new User(mUsernameField.getText().toString());

                            reference.child(user.getUid()).setValue(userModel);

                            Log.v(TAG, "Data entry successful!");
                        }
                        else {
                            // If sign in fails, display a message to the User.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mStatusTextView.setVisibility(View.VISIBLE);
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void hideProgressDialog(){
        registerProgressBar.setVisibility(View.GONE);
        registerButton.setVisibility(View.VISIBLE);
        goSignIn.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog() {
        registerButton.setVisibility(View.GONE);
        goSignIn.setVisibility(View.GONE);
        registerProgressBar.setVisibility(View.VISIBLE);
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
            mPasswordField.setError("Required Password with Minimum 8 characters!");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            //mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, User.getEmail(), User.isEmailVerified()));

            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, User.getUid()));

            //findViewById(R.id.register_button).setVisibility(View.GONE);
            //findViewById(R.id.register_email).setVisibility(View.GONE);

            Intent intent = new Intent(RegisterActivity.this, GetProfileActivity.class);
            finish();
            startActivity(intent);

        } else {
            //mStatusTextView.setText(R.string.signed_out);
            //mDetailTextView.setText(null);

            findViewById(R.id.register_button).setVisibility(View.VISIBLE);
            findViewById(R.id.register_email).setVisibility(View.VISIBLE);
        }
    }


}
