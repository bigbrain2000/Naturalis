package com.chs.naturalis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import com.chs.naturalis.R;


public class Login extends AppCompatActivity {

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }, 1);
        });
    }
}