package com.melodious.application.melodiousalpha;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class IntroductionActivity extends AppCompatActivity {
    ViewPager viewPager;
    SlideshowAdapter slideshowAdapter;
    Button continueButton;
    Handler handler;
    Runnable runnable;
    Timer timer;

    private int timerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        viewPager = findViewById(R.id.image_pager);

        continueButton = findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroductionActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+ 1 , true);
                if(viewPager.getCurrentItem() == 2){
                    continueButton.setVisibility(View.VISIBLE);
                }
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(timerCount > 2){
                    //timer.cancel();
                }
                handler.post(runnable);
                timerCount++;
            }
        }, 800, 800);


        slideshowAdapter = new SlideshowAdapter(this);

        viewPager.setAdapter(slideshowAdapter);
    }
}
