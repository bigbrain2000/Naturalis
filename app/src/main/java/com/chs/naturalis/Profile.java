package com.chs.naturalis;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static com.chs.naturalis.Login.getLoggedUser;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chs.naturalis.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.logging.Logger;

public class Profile extends AppCompatActivity {

    private TextView name, email, phoneNumber, address;
    private BottomNavigationView bottomNavigationView;
    //private ImageView profileFrame;

    private float x1, x2, y1, y2;

    private static final Logger LOGGER = getLogger(Profile.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_profile);

        super.onCreate(savedInstanceState);

        identifyTheUserFieldsById();
        User user = getLoggedUser();

        actionOnNavBarItemSelected();
        setTextFieldsWithUserData(user);

        //profileFrame.setVisibility(View.VISIBLE);
    }

    /**
     * Identify the activity field by their id.
     */
    private void identifyTheUserFieldsById() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
       // profileFrame = findViewById(R.id.profileFrame);
    }


    private void setTextFieldsWithUserData(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());
        address.setText(user.getAddress());
    }

    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuHome:
                    LOGGER.info("Transition to HomePage activity was successful.");
                    transitionToHomeActivity();
                    return true;
                case R.id.menuCart:
                    LOGGER.info("Transition to Shopping cart activity was successful.");
                    transitionToShoppingCartActivity();
                    return true;
                case R.id.menuAccount:
                    //Already in this activity, just return true
                    return true;
                case R.id.menuLogout:
                    showAlertBoxForLogout();
                    return true;
            }
            return false;
        });
    }

    private void transitionToHomeActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(Profile.this, HomePageClient.class);
            startActivity(intent);
            finish();
        });
    }

    private void transitionToShoppingCartActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(Profile.this, ShoppingCart.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Sliding right opens the {@link Login} activity.
     * Sliding left opens the {@link ShoppingCart} activity.
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

                if (x1 < x2) {
                    Intent intent = new Intent(Profile.this, ShoppingCart.class);
                    startActivity(intent);
                }

                if (x1 > x2) {
                    showAlertBoxForLogout();
                }
                break;
        }

        return false;
    }

    /**
     * Defined an alert box in case the user wants to logout from the app.
     * Yes, he is redirected to Login page.
     * No, he stays in the same activity.
     */
    private void showAlertBoxForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

        builder.setMessage("Do you want to exit the application?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}