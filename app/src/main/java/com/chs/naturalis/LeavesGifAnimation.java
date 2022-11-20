package com.chs.naturalis;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import pl.droidsonroids.gif.GifImageView;

public class LeavesGifAnimation extends AppCompatActivity {

    private Animation leaves_animation;
    private GifImageView gif_leaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_leaves_gif_animation);

        super.onCreate(savedInstanceState);

        leaves_animation = AnimationUtils.loadAnimation(this, R.anim.leaves_animation);
        gif_leaves = findViewById(R.id.gif_leaves);
        gif_leaves.setAnimation(leaves_animation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LeavesGifAnimation.this, Login.class);
            startActivity(intent);
            finish();
        }, 4000);
    }
}