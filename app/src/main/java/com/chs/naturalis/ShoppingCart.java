package com.chs.naturalis;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShoppingCart extends AppCompatActivity {

    private Button scanQRButton;
    @SuppressLint("StaticFieldLeak")
    private static TextView displayQRCodeTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_shopping_cart);
        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        openCameraAction();
    }

    /**
     * Start the ScanQRCode activity
     */
    private void openCameraAction() {
        scanQRButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ScanQRCode.class)));
    }

    private void identifyTheFieldsById() {
        displayQRCodeTextView = findViewById(R.id.displayQRCodeTextView);
        scanQRButton = findViewById(R.id.scanQRButton);
    }

    /**
     * Field used in {@link ShoppingCart} for displaying the QR code after scanning.
     *
     * @return a Textview with the scanned QR code.
     */
    public static TextView getDisplayQRCodeTextView() {
        return displayQRCodeTextView;
    }
}