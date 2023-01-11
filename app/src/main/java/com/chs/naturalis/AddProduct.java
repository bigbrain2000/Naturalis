package com.chs.naturalis;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private Button addProductButton;
    private String categoryType;
    private BottomNavigationView adminNavigation;

    private DatabaseReference database;
    private Product product;
    private final String DATABASE_NAME = "Product";
    private final ArrayList<Product> productList = new ArrayList<>();

    private static final Logger LOGGER = getLogger(AddProduct.class.getName());

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_add_product);

        super.onCreate(savedInstanceState);

        identifyTheProductFieldsById();

        setSpinner();

        actionOnNavBarItemSelected();

        addProduct();

        //insertPredefinedProduct();
    }

    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        adminNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.goBackButton:
                    transitionToHomeActivity();
                    return true;
                case R.id.addProductButton:
                    return true;
                case R.id.logoutButton:
                    showAlertBoxForLogout();
                    return true;
            }
            return false;
        });
    }

    private void transitionToHomeActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(AddProduct.this, HomePageAdmin.class);
            startActivity(intent);
            finish();
        });
    }

    private void addProduct() {
        addProductButton.setOnClickListener(view -> insertProductIntoDb());
    }

    private void identifyTheProductFieldsById() {
        adminNavigation = findViewById(R.id.adminNavigation);
        name = findViewById(R.id.productName);
        price = findViewById(R.id.productPrice);
        quantity = findViewById(R.id.productQuantity);
        description = findViewById(R.id.productDescription);
        addProductButton = findViewById(R.id.addProductButton);
        category = findViewById(R.id.spinner1);
    }

    private void insertProductIntoDb() {
        try {
            checkAllFieldsAreCompleted(name, price, quantity, description);

            readFromTheDatabase();

            if (productList.size() > 0) {
                LOGGER.info("Product list is not empty.");
                int max = productList.get(productList.size() - 1).getId();

                product = new Product(max + 1);
                product.setName(name.getText().toString().trim());
                double priceDouble = Double.parseDouble(price.getText().toString().trim());
                product.setPrice(priceDouble);
                int quantityInt = Integer.parseInt(quantity.getText().toString().trim());
                product.setQuantity(quantityInt);
                product.setCategory(categoryType);
                product.setDescription(description.getText().toString().trim());
                product.setCurrency("lei");

                LOGGER.info("Product added successfully.");
                makeText(AddProduct.this, "Product added successfully", LENGTH_LONG).show();
                database.push().setValue(product);

                //after the product is inserted in the database, clear the fields
                clearFields();
            }
        } catch (FieldNotCompletedException e) {
            makeText(AddProduct.this, "Fields are not completed!", LENGTH_LONG).show();
            LOGGER.info("Product has not been created due to uncompleted fields.");
        }
    }

    private void readFromTheDatabase() {
        database = FirebaseDatabase.getInstance().getReference().child(DATABASE_NAME);

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
    }

    private void checkAllFieldsAreCompleted(@NotNull EditText name, @NotNull EditText price, @NotNull EditText quantity, @NotNull EditText description) throws FieldNotCompletedException {
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);
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
        Product product = new Product(1, "Linden tea", 22d, 11, "Linden tea has been used in folk medicine across cultures to relieve high blood pressure, calm anxiety, and soothe digestion.", "Tea", "lei");

        database = FirebaseDatabase.getInstance().getReference().child(DATABASE_NAME);
        database.push().setValue(product);
    }

    /**
     * Defined an alert box in case the user wants to logout from the app.
     * Yes, he is redirected to Login page.
     * No, he stays in the same activity.
     */
    private void showAlertBoxForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProduct.this);

        builder.setMessage("Do you want to exit the application?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(AddProduct.this, Login.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clearFields() {
        name.getText().clear();
        price.getText().clear();
        quantity.getText().clear();
        description.getText().clear();
    }
}