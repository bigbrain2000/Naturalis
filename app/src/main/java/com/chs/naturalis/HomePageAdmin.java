package com.chs.naturalis;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

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

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_home_page_admin);

        super.onCreate(savedInstanceState);

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