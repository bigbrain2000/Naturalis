package com.chs.naturalis;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.chs.naturalis.TeaProducts.getTeaProductName;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chs.naturalis.model.Product;
import com.chs.naturalis.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ViewTeaProduct extends AppCompatActivity {

    private TextView category, name, price, description;
    private BottomNavigationView bottomNavigationView;

    private final List<Product> productList = new ArrayList<>();
    private final List<Product> productListCart = new ArrayList<>();
    private DatabaseReference database;
    private DatabaseReference databaseCart;
    private final String DATABASE_NAME = "Product";
    private float x1, x2, y1, y2;

    private static final Logger LOGGER = getLogger(ViewTeaProduct.class.getName());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_view_tea_product);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        viewProduct();

        actionOnNavBarItemSelected();
    }

    /**
     * Identify the activity field by their id.
     */
    private void identifyTheFieldsById() {
        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    /**
     * View all the details about the syrup product selected by the user.
     */
    private void viewProduct() {
        //Take the product name when the client clicked on in it within the ViewSyrupProducts class
        String productName = getTeaProductName();

        database = FirebaseDatabase.getInstance().getReference().child(DATABASE_NAME);

        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LOGGER.info("Product database has been retrieved.");
                    productList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                    }

                    if (!productList.isEmpty()) {
                        LOGGER.info("List is not empty.");
                    }

                    for (Product product : productList) {
                        if (product.getName().equals(productName)) {
                            name.setText(product.getName());
                            price.setText(product.getPrice() + " " + product.getCurrency());
                            category.setText(product.getCategory());
                            description.setText(product.getDescription());
                        }
                    }
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(ViewTeaProduct.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.buyButton:
                    LOGGER.info("User bought a product");
                    insertBoughtProductIntoDb();
                    return true;
                case R.id.goBackButton:
                    transitionToHomePageClientActivity();
                    return true;
            }
            return false;
        });
    }

    /**
     * By the user email, who is unique, all the items selected by him to be added to cart
     * it will be inserted in the database.
     */
    private void insertBoughtProductIntoDb() {
        String productName = getTeaProductName();
        User user = Login.getLoggedUser();
        String userEmail = user.getEmail();
        final String emailSubstring = "@yahoo.com";

        if (userEmail != null && userEmail.length() > 0) {
            userEmail = userEmail.substring(0, userEmail.length() - emailSubstring.length());
        }

        String databaseName = userEmail;

        databaseCart = FirebaseDatabase.getInstance().getReference().child(databaseName);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    productListCart.add(snapshot.getValue(Product.class));
                }

                for (Product product : productListCart) {
                    if (product.getName().equals(productName)) {
                        databaseCart.push().setValue(product);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
            }
        });
    }

    private void transitionToHomePageClientActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(ViewTeaProduct.this, HomePageClient.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Sliding left opens the {@link TeaProducts} activity.
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
                    Intent intent = new Intent(ViewTeaProduct.this, TeaProducts.class);
                    startActivity(intent);
                }
                break;
        }

        return false;
    }
}