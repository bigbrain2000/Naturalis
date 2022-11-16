package com.chs.naturalis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;

public class HomePageAdmin extends AppCompatActivity {

    private LinearLayout addLinearLayout;
    private Button logoutButton;
    private static final int SPLASH_SCREEN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_admin);

        transitionToAddProductActivity();
        pressLogoutButton();



    }
    private void transitionToAddProductActivity() {
        addLinearLayout=findViewById(R.id.layoutAddProduct);

        addLinearLayout.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(HomePageAdmin.this, AddProduct.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN);
        });
    }

    private void pressLogoutButton(){
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(HomePageAdmin.this, Login.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN);
        });

    }
}