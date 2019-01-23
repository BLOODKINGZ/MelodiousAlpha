package com.melodious.application.melodiousalpha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.melodious.application.melodiousalpha.Models.User;

public class GetProfileActivity extends AppCompatActivity {

    private EditText locationView, aboutView;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private ProgressBar profileProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_profile);

        locationView = findViewById(R.id.profile_location);
        aboutView = findViewById(R.id.profile_about);
        saveButton = findViewById(R.id.save_button);
        profileProgressBar = findViewById(R.id.profile_progress_bar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileProgressBar.setVisibility(View.VISIBLE);

                databaseReference.child("location").setValue(locationView.getText().toString());
                databaseReference.child("about").setValue(aboutView.getText().toString());

                Intent intent = new Intent(GetProfileActivity.this, RecorderActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}
