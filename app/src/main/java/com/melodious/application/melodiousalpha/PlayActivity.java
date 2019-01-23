package com.melodious.application.melodiousalpha;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.melodious.application.melodiousalpha.Utilites.Utils;

public class PlayActivity extends AppCompatActivity {
    ImageView backButton, playButton, stopButton, playerSkin;
    TextView melodyName, timerView, melodyDurationView, noInternetText;
    String melodyPath;
    MediaPlayer mediaPlayer;
    CountDownTimer countDownTimer;
    ProgressBar progressBar, playProgressBar;
    Bundle bundle;
    StorageReference storage;
    LinearLayout playTab;
    //checking if playing Melody
    static boolean playingMelody = false;

    @Override
    public void onBackPressed() {
        if(playingMelody){
            stopButton.performClick();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        backButton = findViewById(R.id.back);
        playButton = findViewById(R.id.play_big);
        stopButton = findViewById(R.id.stop_big);
        playerSkin = findViewById(R.id.profile_image);
        melodyName = findViewById(R.id.melody_name);
        timerView = findViewById(R.id.timer);
        melodyDurationView = findViewById(R.id.melody_duration);
        noInternetText = findViewById(R.id.no_internet_text);
        progressBar = findViewById(R.id.progressBar);
        playTab = findViewById(R.id.play_tab);
        playProgressBar = findViewById(R.id.play_progress_bar);

        storage = FirebaseStorage.getInstance().getReference("melodies");

        bundle = getIntent().getExtras();

        stopButton.setEnabled(false);
        stopButton.setAlpha((float) .4);

        melodyName.setText(bundle.getCharSequence("Name"));
        //melodyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Melody recordings/" + getIntent().getStringExtra("Melody");
        //melodyPath = storage.child(bundle.getString("Name")).getDownloadUrl().toString();

        storage.child(bundle.getString("Name")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for melody
                melodyPath = uri.toString();
                playProgressBar.setVisibility(View.GONE);
                playTab.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                playProgressBar.setVisibility(View.GONE);
                noInternetText.setVisibility(View.VISIBLE);
                Toast.makeText(PlayActivity.this, "Error" + exception, Toast.LENGTH_SHORT).show();
            }
        });

        melodyName.setText(bundle.getString("Name").split("\\.")[0]);

        //setting data source to get the info before starting playing
        melodyDurationView.setText(bundle.getCharSequence("Duration"));


        //setting up buttons
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer = new MediaPlayer();

                //self terminating on finishing
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.release();

                        playButton.setEnabled(true);
                        playButton.setAlpha((float) 1);
                        backButton.setEnabled(true);
                        backButton.setAlpha((float) 1);
                        stopButton.setEnabled(false);
                        stopButton.setAlpha((float) .4);


                        playerSkin.clearAnimation();
                        progressBar.setProgress(100);

                        playingMelody = false;

                        Toast.makeText(getApplicationContext(), "Playing stopped.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                try
                {
                    mediaPlayer.setDataSource(melodyPath);
                    mediaPlayer.prepare();
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Error : "  + e,
                            Toast.LENGTH_SHORT).show();
                }

                final int duration = mediaPlayer.getDuration();
                final int msToConsider = (int) duration / 100;
                progressBar.setProgress(0);

                countDownTimer = new CountDownTimer(duration, 1000) {
                    public void onTick(long millisUntilFinished) {

                        long passedTime = duration - millisUntilFinished;


                        int min  = (int) (passedTime / 1000) / 60;
                        int sec = (int) ((passedTime - (min * 60 * 1000)) / 1000);


                        progressBar.setProgress((int) ((duration - millisUntilFinished) / msToConsider));

                        String counter = String.format("%02d : %02d", min, sec);
                        timerView.setText(counter);
                    }

                    public void onFinish() {
                    }
                }.start();

                mediaPlayer.start();
                playingMelody = true;

                Animation animation = new AlphaAnimation((float) 0.4, 1); // Change rotation from fully visible to invisible
                animation.setDuration(1000); // duration - half a second
                animation.setInterpolator(new LinearInterpolator()); // do not alter
                // animation
                // rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation
                // infinitely
                animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
                // end so the button will
                // fade back in
                playerSkin.startAnimation(animation);

                playButton.setEnabled(false);
                playButton.setAlpha((float) .4);
                backButton.setEnabled(false);
                backButton.setAlpha((float) .4);
                stopButton.setEnabled(true);
                stopButton.setAlpha((float) 1);

                Toast.makeText(getApplicationContext(), "Playing...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer !=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();

                    playingMelody = false;
                }

                countDownTimer.cancel();
                countDownTimer.onFinish();
                playButton.setEnabled(true);
                playButton.setAlpha((float) 1);
                backButton.setEnabled(true);
                backButton.setAlpha((float) 1);
                stopButton.setEnabled(false);
                stopButton.setAlpha((float) .4);

                playerSkin.clearAnimation();

                Toast.makeText(getApplicationContext(), "Playing stopped.",
                        Toast.LENGTH_SHORT).show();
            }
        });



    }
}
