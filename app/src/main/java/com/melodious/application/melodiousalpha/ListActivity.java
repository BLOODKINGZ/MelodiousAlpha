package com.melodious.application.melodiousalpha;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melodious.application.melodiousalpha.MelodyAdapter.MelodyAdapterOnClickHandler;
import com.melodious.application.melodiousalpha.Models.Melody;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements MelodyAdapterOnClickHandler {

    private ImageView goBackArrow, happyWarn;
    private RecyclerView uploadRecycler;
    private MelodyAdapter melodyAdapter;
    private LinearLayout warnLayout;
    private ProgressBar listProgressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference metadataDatabase;

    private ArrayList<Melody> melodyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        uploadRecycler = findViewById(R.id.uploads_recycler);
        goBackArrow = findViewById(R.id.go_back);
        warnLayout = findViewById(R.id.warn_layout);
        happyWarn = findViewById(R.id.warn_layout_image);
        listProgressBar = findViewById(R.id.list_progress_bar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        uploadRecycler.setLayoutManager(linearLayoutManager);
        uploadRecycler.setHasFixedSize(true);
        melodyAdapter = new MelodyAdapter(this);
        uploadRecycler.setAdapter(melodyAdapter);

        melodyList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        metadataDatabase = FirebaseDatabase.getInstance().getReference("metadata");

        metadataDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("ListLog", dataSnapshot.getValue().toString());
                //melodyList.clear();
//                for(DataSnapshot melodies : dataSnapshot.getChildren()){
//                    Melody currentMelody = melodies.getValue(Melody.class);
//                    melodyList.add(currentMelody);
//                    Log.d("Listinglog", currentMelody.toString());
//                }
                Melody currentMelody = dataSnapshot.getValue(Melody.class);
                melodyList.add(currentMelody);
                Log.d("ListLog", melodyList.toString());
                melodyAdapter.setMelodyData(melodyList);
                Log.d("ListLog", "added to adapter");
                listProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        goBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //loadMelody();
    }

    private void loadMelody(){
        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/melody recordings/";
        File directory = new File(path);

        if(!directory.exists()){
            uploadRecycler.setVisibility(View.INVISIBLE);
            warnLayout.setVisibility(View.VISIBLE);

            RotateAnimation rotate = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, .7f, Animation.RELATIVE_TO_SELF, 1f);
            rotate.setDuration(500);
            rotate.setRepeatCount(Animation.INFINITE);
            rotate.setRepeatMode(Animation.RELATIVE_TO_PARENT);
            rotate.setInterpolator(new LinearInterpolator());
            happyWarn.startAnimation(rotate);
        }
        else {
            //File[] files = directory.listFiles();
            //melodyAdapter.setMelodyData(files);

        }*/
    }

    @Override
    public void onClick(Melody melody) {
        Intent intent = new Intent(ListActivity.this, PlayActivity.class);
        //intent.putExtra("Melody", melody);
        Bundle bundle = new Bundle();
        bundle.putString("Name", melody.getName());
        bundle.putString("Author", melody.getAuthor());
        bundle.putString("Duration", melody.getDuration());
        bundle.putInt("Likes", melody.getLikes());
        intent.putExtras(bundle);
        startActivity(intent);
        //Context context = this;
        //Toast.makeText(context, Melody.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
}
