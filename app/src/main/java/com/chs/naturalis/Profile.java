package com.chs.naturalis;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static com.chs.naturalis.Login.getLoggedUser;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chs.naturalis.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.logging.Logger;

public class Profile extends AppCompatActivity {

    private TextView name, email, phoneNumber, address;
    private BottomNavigationView bottomNavigationView;
    private ImageView profileFrame;

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

        profileFrame.setVisibility(100);
    }

    private void setTextFieldsWithUserData(User user) {
        name.setText(user.getName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());
        address.setText(user.getAddress());
    }

    private void identifyTheUserFieldsById() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        profileFrame = findViewById(R.id.profileFrame);
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
                    // TODO
                    return true;
                case R.id.menuAccount:
                    return true;
                case R.id.menuLogout:
                    transitionToLoginActivity();
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

    private void transitionToLoginActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            finish();
        });
    }


    //TODO: slide left to go to cart, slide right to go to logout and define an alert box for it

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
//                    Intent intent = new Intent(Profile.this, Logout.class);
//                    startActivity(intent);
                }
                break;
        }

        return false;
    }
}