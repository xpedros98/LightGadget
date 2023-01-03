package com.example.lightgadget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Welcome extends AppCompatActivity {

    // Welcome activity duration time
    private static int WELCOME_SCREEN = 2000; // [ms]

    // Welcome animations
    Animation cloud_1_animation, cloud_2_animation, cloud_3_animation, cloud_4_animation, cloud_5_animation, cloud_6_animation, cloud_7_animation, cloud_8_animation, cloud_9_animation, the_simpsons;
    ImageView cloud_1, cloud_2, cloud_3, cloud_4, cloud_5, cloud_6, cloud_7, cloud_8, cloud_9, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome_activity);

        System.out.println("420 420 420 420 420");

        // Declarations of objects.
        title = findViewById(R.id.title);
        cloud_1 = findViewById(R.id.cloud_1);
        cloud_2 = findViewById(R.id.cloud_2);
        cloud_3 = findViewById(R.id.cloud_3);
        cloud_4 = findViewById(R.id.cloud_4);
        cloud_5 = findViewById(R.id.cloud_5);
        cloud_6 = findViewById(R.id.cloud_6);
        cloud_7 = findViewById(R.id.cloud_7);
        cloud_8 = findViewById(R.id.cloud_8);
        cloud_9 = findViewById(R.id.cloud_9);

        // Animations
        cloud_1_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_1);
        cloud_2_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_2);
        cloud_3_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_3);
        cloud_4_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_4);
        cloud_5_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_5);
        cloud_6_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_6);
        cloud_7_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_7);
        cloud_8_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_8);
        cloud_9_animation = AnimationUtils.loadAnimation(this, R.anim.cloud_9);
        the_simpsons = AnimationUtils.loadAnimation(this, R.anim.the_simpsons);
        the_simpsons.setDuration(WELCOME_SCREEN);

        // Start animations
        cloud_1.startAnimation(cloud_1_animation);
        cloud_2.startAnimation(cloud_2_animation);
        cloud_3.startAnimation(cloud_3_animation);
        cloud_4.startAnimation(cloud_4_animation);
        cloud_5.startAnimation(cloud_5_animation);
        cloud_6.startAnimation(cloud_6_animation);
        cloud_7.startAnimation(cloud_7_animation);
        cloud_8.startAnimation(cloud_5_animation);
        cloud_9.startAnimation(cloud_9_animation);
        title.startAnimation(the_simpsons);

        // Manage the welcome activity duration time.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Welcome.this,  Main.class);

                // Open new activity and close the current one.
                startActivity(intent);

                // Animation for the activities' transition.
                overridePendingTransition(R.anim.in_fade, R.anim.out_expand_fade);

                finish();
            }
        }, WELCOME_SCREEN);
    }
}