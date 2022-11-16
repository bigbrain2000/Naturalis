package com.chs.naturalis;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chs.exceptions.naturalis.FieldNotCompletedException;
import com.chs.naturalis.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Logger;

public class AddProduct extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText name, price, quantity, description;
    private Spinner category;
    private Button addProductButton, goBackToAdminPage;
    private String categoryType;

    private DatabaseReference database;
    private Product product;

    private static final Logger LOGGER = getLogger(AddProduct.class.getName());
    private static final int SPLASH_SCREEN = 100;

    private final ArrayList<Product> productList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setSpinner();
        addProduct();
        //insertPredefinedProduct();
        pushBackButton();
    }

    private void addProduct() {
        final String productDatabaseName = "Product";

        identifyTheProductFieldsById();

        database = FirebaseDatabase.getInstance().getReference().child(productDatabaseName);

        addProductButton.setOnClickListener(view -> insertProductIntoDb());
    }

    private void identifyTheProductFieldsById() {
        name = findViewById(R.id.productName);
        price = findViewById(R.id.productPrice);
        quantity = findViewById(R.id.productQuantity);
        description = findViewById(R.id.productDescription);
        addProductButton = findViewById(R.id.addProductButton);
    }

    private void insertProductIntoDb() {
        try {
            checkAllFieldsAreCompleted(name, price, quantity, description);
            final String databaseName = "Product";
            database = FirebaseDatabase.getInstance().getReference().child(databaseName);
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        productList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            productList.add(snapshot.getValue(Product.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    LOGGER.info("Error on retrieving data from database.");
                }
            });
            if (productList.size() > 0) {
                LOGGER.info("Product list is not empty.");
                int max = productList.get(productList.size() - 1).getId();

                product = new Product(max + 1);
                product.setName(name.getText().toString().trim());
                double priceDouble = Double.parseDouble(price.getText().toString().trim());
                product.setPrice(priceDouble);
                int quantityInt = Integer.parseInt(quantity.getText().toString().trim());
                product.setQuantity(quantityInt);
                product.setDescription(description.getText().toString().trim());
                product.setCategory(categoryType);
                product.setCurrency("lei");


                LOGGER.info("Product added successfully.");
                makeText(AddProduct.this, "Product added successfully", LENGTH_LONG).show();
                database.push().setValue(product);
            }
        } catch (FieldNotCompletedException e) {
            makeText(AddProduct.this, "Fields are not completed!", LENGTH_LONG).show();
            LOGGER.info("Product has not been created due to uncompleted fields.");
        }
    }

    private void checkAllFieldsAreCompleted(@NotNull EditText name,
                                            @NotNull EditText price,
                                            @NotNull EditText quantity,
                                            @NotNull EditText description) throws FieldNotCompletedException {
        checkFieldIsCompleted(name);
        checkFieldIsCompleted(price);
        checkFieldIsCompleted(quantity);
        checkFieldIsCompleted(description);
    }

    private void checkFieldIsCompleted(EditText field) throws FieldNotCompletedException {
        if (field.getText().toString().isEmpty()) {
            field.setError("Field cannot be empty");
            throw new FieldNotCompletedException();
        }
    }

    private void setSpinner() {
        category = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);
    }

    private void pushBackButton() {
        goBackToAdminPage = findViewById(R.id.goBackToAdminPage);

        goBackToAdminPage.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(AddProduct.this, HomePageAdmin.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN);
            LOGGER.info("Transition to homepage was made.");
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        categoryType = choice;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void insertPredefinedProduct() {
        Product product = new Product(1, "Linden tea", 22d, 11, "Linden tea has been used in folk medicine across cultures to relieve high blood pressure, calm anxiety, and soothe digestion.", "Tea" , "lei");


        final String productDatabaseName = "Product";
        database = FirebaseDatabase.getInstance().getReference().child(productDatabaseName);
        database.push().setValue(product);
    }
}