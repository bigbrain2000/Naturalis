package com.chs.naturalis;

import static com.chs.naturalis.Login.getLoggedUser;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chs.naturalis.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.logging.Logger;

public class Profile extends AppCompatActivity {

    private TextView name, password, email, phoneNumber, address;
    private BottomNavigationView bottomNavigationView;
    private static final int SPLASH_SCREEN = 1000;

    private static final Logger LOGGER = getLogger(Profile.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        identifyTheUserFieldsById();
        User user = getLoggedUser();

        actionOnNavBarItemSelected();
        setTextFieldsWithUserData(user);
    }

    private void setTextFieldsWithUserData(User user) {
        name.setText(user.getName());
        password.setText(user.getPassword());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());
        address.setText(user.getAddress());
    }

    private void identifyTheUserFieldsById() {
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
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
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Profile.this, HomePageClient.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }

    private void transitionToLoginActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}