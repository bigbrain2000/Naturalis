package com.chs.naturalis;

import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.logging.Logger;

public class HomePageClient extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static final int SPLASH_SCREEN = 10;
    private static final Logger LOGGER = getLogger(HomePageClient.class.getName());
    private ImageView imageSyrup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_client);

        actionOnNavBarItemSelected();
        transitionToViewProductActivity();
    }

    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuHome:
                    return true;
                case R.id.menuCart:
                    return true;
                case R.id.menuAccount:
                    LOGGER.info("Transition to Profile activity was successful.");
                    transitionToProfileActivity();
                    return true;
                case R.id.menuLogout:
                    transitionToLoginActivity();
                    return true;
            }
            return false;
        });
    }

    private void transitionToProfileActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(HomePageClient.this, Profile.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }

    private void transitionToLoginActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(HomePageClient.this, Login.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }

    private void transitionToViewProductActivity() {
        imageSyrup = findViewById(R.id.imageSyrup);

        imageSyrup.setOnClickListener(v ->
                new Handler().post(() -> {
                    Intent intent = new Intent(HomePageClient.this, ViewSyrupProducts.class);
                    startActivity(intent);
                    finish();
                }));
    }
}