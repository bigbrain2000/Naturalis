package com.chs.naturalis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Animation topAnimation, bottomAnimation;
    private ImageView logoNameImage, logoTreeImage;

    /*
    Field used for making a delay of 3s when switching the activities
    */
    private static final int SPLASH_SCREEN = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logoNameImage = findViewById(R.id.logoNameImage);
        logoTreeImage = findViewById(R.id.logoTreeImage);

        logoTreeImage.setAnimation(topAnimation);
        logoNameImage.setAnimation(bottomAnimation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}