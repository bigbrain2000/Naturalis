package com.chs.naturalis;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chs.naturalis.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ShoppingCart extends AppCompatActivity {

    private Button scanQRButton;
    @SuppressLint("StaticFieldLeak")
    private static TextView displayQRCodeTextView;
    private BottomNavigationView bottomNavigationView;
    private ListView shoppingCartListView;

    private DatabaseReference database;
    private final List<Product> productList = new ArrayList<>();
    private final String DATABASE_NAME = "Product";
    private float x1, x2, y1, y2;

    private static final Logger LOGGER = getLogger(ShoppingCart.class.getName());

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

        setListViewItems();

        actionOnNavBarItemSelected();
    }

    private void identifyTheFieldsById() {
        displayQRCodeTextView = findViewById(R.id.displayQRCodeTextView);
        scanQRButton = findViewById(R.id.scanQRButton);
        shoppingCartListView = findViewById(R.id.shoppingCartListView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuHome:
                    LOGGER.info("Transition to HomePageClient activity was successful.");
                    transitionToHomePageActivity();
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
        new Handler().post(() -> {
            Intent intent = new Intent(ShoppingCart.this, Profile.class);
            startActivity(intent);
            finish();
        });
    }

    private void transitionToLoginActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(ShoppingCart.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void transitionToHomePageActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(ShoppingCart.this, HomePageClient.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Start the ScanQRCode activity
     */
    private void openCameraAction() {
        scanQRButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ScanQRCode.class)));
    }

    /**
     * Field used in {@link ShoppingCart} for displaying the QR code after scanning.
     * Maybe remove it in the future.
     *
     * @return a Textview with the scanned QR code.
     */
    public static TextView getDisplayQRCodeTextView() {
        return displayQRCodeTextView;
    }

    /**
     * Insert the products names from the database into the list view.
     */
    private void setListViewItems() {
        database = FirebaseDatabase.getInstance().getReference().child(DATABASE_NAME);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LOGGER.info("Product database has been retrieved for setting the list view.");

                    productList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                    }

                    if (!productList.isEmpty()) {
                        LOGGER.info("List is not empty.");
                    }

                    //Retrieve the products from database and create a list with their names.
                    List<String> productsNameList = new ArrayList<>();
                    for (Product product : productList) {
                        if (product.getQuantity() > 0) {
                            productsNameList.add(product.getName());
                        }
                    }

                    //Set the listview items as the productsNameList objects.
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ShoppingCart.this,
                            android.R.layout.simple_list_item_multiple_choice, productsNameList);
                    shoppingCartListView.setAdapter(adapter);
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(ShoppingCart.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sliding right opens the {@link ShoppingCart} activity.
     * Sliding left opens the {@link HomePageClient} activity.
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
                    Intent intent = new Intent(ShoppingCart.this, Profile.class);
                    startActivity(intent);
                }

                if (x1 < x2) {
                    Intent intent = new Intent(ShoppingCart.this, HomePageClient.class);
                    startActivity(intent);
                }
                break;
        }

        return false;
    }
}