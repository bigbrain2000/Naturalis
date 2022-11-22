package com.chs.naturalis;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.logging.Logger;

public class HomePageClient extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView imageSyrup;
    private float x1, x2, y1, y2;

    private static final Logger LOGGER = getLogger(HomePageClient.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_home_page_client);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        actionOnNavBarItemSelected();
        transitionToViewProductActivity();
    }

    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuHome:
                    return true;
                case R.id.menuCart:
                    LOGGER.info("Transition to Shopping Cart activity was successful.");
                    transitionToShoppingCartActivity();
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
        new Handler().post(() -> {
            Intent intent = new Intent(HomePageClient.this, Profile.class);
            startActivity(intent);
            finish();
        });
    }

    private void transitionToLoginActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(HomePageClient.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void transitionToViewProductActivity() {
        imageSyrup.setOnClickListener(v ->
                new Handler().post(() -> {
                    Intent intent = new Intent(HomePageClient.this, ViewSyrupProducts.class);
                    startActivity(intent);
                    finish();
                }));
    }

    private void transitionToShoppingCartActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(HomePageClient.this, ShoppingCart.class);
            startActivity(intent);
            finish();
        });
    }

    private void identifyTheFieldsById() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        imageSyrup = findViewById(R.id.imageSyrup);
    }

    /**
     * Sliding right opens the {@link ShoppingCart} activity.
     */
    public boolean onTouchEvent(MotionEvent touch) {
        switch (touch.getAction()) {
            case ACTION_DOWN:
                x1 = touch.getX();
                y1 = touch.getY();
                break;
            case ACTION_UP:
                x2 = touch.getX();
                y2 = touch.getY();

                if (x1 > x2) {
                    Intent intent = new Intent(HomePageClient.this, ShoppingCart.class);
                    startActivity(intent);
                }
                break;
        }

        return false;
    }
}