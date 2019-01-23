package com.melodious.application.melodiousalpha.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melodious.application.melodiousalpha.LoginActivity;
import com.melodious.application.melodiousalpha.Models.User;
import com.melodious.application.melodiousalpha.R;

public class ProfileFragment extends DialogFragment {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private TextView nameField, locationField, aboutField;
    private Bundle bundle;
    private Button signOutButton;
    private AlertDialog alertDialog;

    public ProfileFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.profile_dialog, null);

        nameField = view.findViewById(R.id.username_field);
        locationField = view.findViewById(R.id.location_field);
        aboutField = view.findViewById(R.id.about_field);
        signOutButton = view.findViewById(R.id.sign_out_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                alertDialog.dismiss();
            }
        });

        //fetching passed values
        bundle = getArguments();
        nameField.setText(bundle.getString("username"));
        locationField.setText(bundle.getString("location"));
        aboutField.setText(bundle.getString("about"));

        builder.setView(view);

        alertDialog = builder.create();

        return alertDialog;
    }
}
