package com.chs.naturalis;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.chs.naturalis.HomePageAdmin.getProductName;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chs.naturalis.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AdminViewProduct extends AppCompatActivity {

    private TextView category, name, price, description;
    private Button goBackButton;

    private final List<Product> productList = new ArrayList<>();
    private DatabaseReference database;
    private final String DATABASE_NAME = "Product";
    private static final int SPLASH_SCREEN = 100;


    private static final Logger LOGGER = getLogger(AdminViewProduct.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_admin_view_product);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        viewProduct();

        pushBackButton();

    }

    private void identifyTheFieldsById() {
        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        //  insertBoughtProductIntoDb();
        goBackButton = findViewById(R.id.goBackButton);
    }

    private void viewProduct() {
        //Take the product name when the client clicked on in it within the ViewTeaProduct class
        String productName = getProductName();

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
                makeText(AdminViewProduct.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    private void pushBackButton() {
        goBackButton = findViewById(R.id.goBackButton);

        goBackButton.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(AdminViewProduct.this, HomePageAdmin.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN);
            LOGGER.info("Transition to homepage was made.");
        });
    }


}