package com.chs.naturalis;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import pl.droidsonroids.gif.GifImageView;

public class CuteElephantGifAnimation extends AppCompatActivity {

    private Animation cute_elephant_animation;
    private GifImageView gif_elephant;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_cute_elephant_gif_animation);

        super.onCreate(savedInstanceState);

        cute_elephant_animation = AnimationUtils.loadAnimation(this, R.anim.leaves_animation);
        gif_elephant = findViewById(R.id.gif_elephant);
        gif_elephant.setAnimation(cute_elephant_animation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(CuteElephantGifAnimation.this, HomePageClient.class);
            startActivity(intent);
            finish();
        }, 4000);
    }
}