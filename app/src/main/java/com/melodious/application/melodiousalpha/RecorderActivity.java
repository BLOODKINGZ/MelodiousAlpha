package com.melodious.application.melodiousalpha;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.melodious.application.melodiousalpha.Fragments.ProfileFragment;
import com.melodious.application.melodiousalpha.Models.Melody;
import com.melodious.application.melodiousalpha.Models.User;
import com.melodious.application.melodiousalpha.Utilites.Utils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class RecorderActivity extends FragmentActivity{

    private ImageView record, stop_recording, play, stop_playing, playlist, cassette;
    private TextView status, fileName, createdOn, usernameSignOut;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;
    private String metaFileName;
    private Vibrator vibrator;
    private LinearLayout infoTab;
    private Button shareButton;

    //save data for profile dialog
    private String username, location, about;

    //authentication and servers
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storage;
    private DatabaseReference metadataDatabase, usersDatabase;
    private UploadTask uploadTask;

    static boolean recordingMelody = false;
    static boolean playingMelody = false;

    private static final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onStart() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(RecorderActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if(recordingMelody){
            stop_recording.performClick();
        }
        else if(playingMelody){
            stop_playing.performClick();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        //requesting runtime permission
        if(!checkPermissionFromDevice())
        {
            requestPermission();
        }

        record = (ImageView) findViewById(R.id.record);
        stop_recording = (ImageView) findViewById(R.id.stop_recording);
        play = (ImageView) findViewById(R.id.play);
        stop_playing = (ImageView) findViewById(R.id.stop_playing);
        playlist = (ImageView) findViewById(R.id.playlist);
        cassette = (ImageView) findViewById(R.id.cassette_tape);

        status = (TextView) findViewById(R.id.status);
        fileName = (TextView) findViewById(R.id.filename);
        createdOn = (TextView) findViewById(R.id.current_date);
        usernameSignOut = findViewById(R.id.user_name_sign_out);

        infoTab = findViewById(R.id.info_tab);
        shareButton = findViewById(R.id.share_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance().getReference().child("melodies");
        metadataDatabase = FirebaseDatabase.getInstance().getReference("metadata");
        usersDatabase = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid());

        stop_recording.setEnabled(false);
        stop_recording.setAlpha((float) .4);
        play.setEnabled(false);
        play.setAlpha((float) .4);
        stop_playing.setEnabled(false);
        stop_playing.setAlpha((float) .4);
        //shareButton.setEnabled(false);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("RegisterLog", dataSnapshot.toString());
                User user = dataSnapshot.getValue(User.class);

                //for passing on into profile dialog to save internet usage
                username = user.getName();
                location = user.getLocation();
                about = user.getAbout();

                usernameSignOut.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                if(checkPermissionFromDevice())
                {
                    String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/melody recordings/";
                    String currentFilename = Utils.getNextFilename(basePath);
                    metaFileName = firebaseUser.getUid() + "_" + currentFilename;
                    outputFile = basePath + "/" + firebaseUser.getUid() + "_" + currentFilename;

                    setupMediaRecorder();
                    try
                    {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        recordingMelody = true;

                        status.setText(getString(R.string.status_recording));
                        fileName.setText(currentFilename);

                        Date current_time = Calendar.getInstance().getTime();
                        createdOn.setText(current_time.toString());

                        Animation animation = new AlphaAnimation((float) 0.5, 0); // Change alpha from fully visible to invisible
                        animation.setDuration(500); // duration - half a second
                        animation.setInterpolator(new LinearInterpolator()); // do not alter
                        // animation
                        // rate
                        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
                        // infinitely
                        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
                        // end so the button will
                        // fade back in
                        cassette.startAnimation(animation);

                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Error : " + e, Toast.LENGTH_SHORT).show();
                    }

                    record.setEnabled(false);
                    record.setAlpha((float) .4);
                    stop_recording.setEnabled(true);
                    stop_recording.setAlpha((float) 1);
                    play.setEnabled(false);
                    play.setAlpha((float) .4);
                    stop_playing.setEnabled(false);
                    stop_playing.setAlpha((float) .4);
                    playlist.setEnabled(false);
                    playlist.setAlpha((float) .4);
                    infoTab.setVisibility(View.VISIBLE);
                    shareButton.setEnabled(false);

                    Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_SHORT).show();
                }
                else requestPermission();
            }
        });

        stop_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                cassette.clearAnimation();
                status.setText("Recording stopped!");
                try
                {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    recordingMelody = false;
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Error : " + e, Toast.LENGTH_SHORT).show();
                }

                record.setEnabled(true);
                record.setAlpha((float) 1);
                stop_recording.setEnabled(false);
                stop_recording.setAlpha((float) .4);
                play.setEnabled(true);
                play.setAlpha((float) 1);
                playlist.setEnabled(true);
                playlist.setAlpha((float) 1);
                shareButton.setEnabled(true);

                shareButton.performClick();

                Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                status.setText("Playing...");
                mediaPlayer = new MediaPlayer();

                //self terminating on finishing
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        playingMelody = false;

                        record.setEnabled(true);
                        record.setAlpha((float) 1);
                        stop_recording.setEnabled(false);
                        stop_recording.setAlpha((float) .4);
                        play.setEnabled(true);
                        play.setAlpha((float) 1);
                        stop_playing.setEnabled(false);
                        stop_playing.setAlpha((float) .4);
                        playlist.setEnabled(true);
                        playlist.setAlpha((float) 1);
                        shareButton.setEnabled(true);

                        status.setText("Playing stopped!");
                        vibrator.vibrate(100);
                        Toast.makeText(getApplicationContext(), "Playing stopped.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                try
                {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    playingMelody = true;
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Error : "  + e,
                            Toast.LENGTH_SHORT).show();
                }

                mediaPlayer.start();

                record.setEnabled(false);
                record.setAlpha((float) .4);
                play.setEnabled(false);
                play.setAlpha((float) .4);
                stop_playing.setEnabled(true);
                stop_playing.setAlpha((float) 1);
                playlist.setEnabled(false);
                playlist.setAlpha((float) .4);
                shareButton.setEnabled(false);

                Toast.makeText(getApplicationContext(), "Playing...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        stop_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status.setText("Playing stopped!");
                vibrator.vibrate(100);
                if(mediaPlayer !=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    playingMelody = false;
                }

                record.setEnabled(true);
                record.setAlpha((float) 1);
                stop_recording.setEnabled(false);
                stop_recording.setAlpha((float) .4);
                play.setEnabled(true);
                play.setAlpha((float) 1);
                stop_playing.setEnabled(false);
                stop_playing.setAlpha((float) .4);
                playlist.setEnabled(true);
                playlist.setAlpha((float) 1);
                shareButton.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Playing stopped.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(100);
                Intent intent = new Intent(RecorderActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        //this shareButton gets pressed automatically. Will have an update in future
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting up buttons
                record.setEnabled(false);
                record.setAlpha((float) .4);
                stop_recording.setEnabled(false);
                stop_recording.setAlpha((float) .4);
                play.setEnabled(false);
                play.setAlpha((float) .4);
                stop_playing.setEnabled(false);
                stop_playing.setAlpha((float) .4);
                playlist.setEnabled(false);
                playlist.setAlpha((float) .4);
                //shareButton.setVisibility(View.VISIBLE);

                status.setText("Uploading...");

                //uploading
                Uri file = Uri.fromFile(new File(outputFile));
                storage = FirebaseStorage.getInstance().getReference().child("melodies");
                StorageReference melody = storage.child(file.getLastPathSegment());

                uploadTask = melody.putFile(file);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        status.setText("Uploading Failed!");
                        Log.d("RecorderLog", "Error", e);
                        Toast.makeText(RecorderActivity.this, "Please try again!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        status.setText("Upload Completed!");
                        Toast.makeText(RecorderActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                        record.setEnabled(true);
                        record.setAlpha((float) 1);
                        play.setEnabled(true);
                        play.setAlpha((float) 1);
                        playlist.setEnabled(true);
                        playlist.setAlpha((float) 1);
                    }
                });

                //saving metadata
                Melody melody1 = new Melody(Utils.getDuration(outputFile), firebaseUser.getUid(), metaFileName, 0);
                metadataDatabase.child(metaFileName.split("\\.")[0]).setValue(melody1);
                //Toast.makeText(RecorderActivity.this, "Metadata set successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        //profile options
        usernameSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("location", location);
                bundle.putString("about", about);

                profileFragment.setArguments(bundle); //passing data onto dialog

                profileFragment.show(getSupportFragmentManager(), "Profile Fragment");

                onStart();
                //usernameSignOut.performClick(); //force intent to refresh
            }
        });

    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
    }

    private boolean checkPermissionFromDevice() {
        int write_to_external_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_to_external_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }

            break;
        }

    }

    private void refreshActivity(){
        onStart();
    }

}
