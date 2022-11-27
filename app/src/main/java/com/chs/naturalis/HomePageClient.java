package com.chs.naturalis;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.logging.Logger;

public class HomePageClient extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView imageSyrup;
    private LinearLayout layoutTea;
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
        transitionToViewSyrupProductActivity();
        transitionToViewTeaProductActivity();
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
                    showAlertBoxForLogout();
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

    private void transitionToViewSyrupProductActivity() {
        imageSyrup.setOnClickListener(v ->
                new Handler().post(() -> {
                    Intent intent = new Intent(HomePageClient.this, ViewSyrupProducts.class);
                    startActivity(intent);
                    finish();
                }));
    }

    private void transitionToViewTeaProductActivity() {
        layoutTea.setOnClickListener(v ->
                new Handler().post(() -> {
                    Intent intent = new Intent(HomePageClient.this, ViewTeaProducts.class);
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
        layoutTea = findViewById(R.id.layoutTea);
    }

    /**
     * Defined an alert box in case the user wants to logout from the app.
     * Yes, he is redirected to Login page.
     * No, he stays in the same activity.
     */
    private void showAlertBoxForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageClient.this);

        builder.setMessage("Do you want to exit the application?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(HomePageClient.this, Login.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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