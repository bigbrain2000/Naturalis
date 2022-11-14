package com.chs.naturalis;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.chs.exceptions.naturalis.FieldNotCompletedException;
import com.chs.naturalis.model.Product;
import com.chs.naturalis.model.User;
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
    private Boolean flag=true;
    private String categoryType;

    private DatabaseReference database;
    private DatabaseReference database2;
    private Product product;

    private static final Logger LOGGER = getLogger(AddProduct.class.getName());
    private static final int SPLASH_SCREEN = 100;

    private ArrayList<Product> productList = new ArrayList<>();;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setSpinner();
        addProduct();
        pushBackButton();
    }

    private void addProduct() {
        final String productDatabaseName = "Product";

        identifyTheProductFieldsById();

        database = FirebaseDatabase.getInstance().getReference().child(productDatabaseName);

        addProductButton.setOnClickListener(view -> {
            insertProductIntoDb();
        });

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


            product = new Product(findMaxIdFromProductsList());
           // product.setId(1);
            product.setName(name.getText().toString().trim());
            double priceDouble = Double.parseDouble(price.getText().toString().trim());
            product.setPrice(priceDouble);
            int quantityInt = Integer.parseInt(quantity.getText().toString().trim());
            product.setQuantity(quantityInt);
            product.setDescription(description.getText().toString().trim());
            product.setCategory(categoryType);

            LOGGER.info("Product added successfully.");
            makeText(AddProduct.this, "Product added successfully", LENGTH_LONG).show();
            //push them in the DB
            flag = true;
            database.push().setValue(product);
        } catch (FieldNotCompletedException e) {
           makeText(AddProduct.this, "Fields are not completed!", LENGTH_LONG).show();
           LOGGER.info("User account has not been created due to uncompleted fields.");
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
            flag = false;
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
            LOGGER.info("Transition to login was made.");
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

    /**
     * Retrieve a list with all the products from the database.
     *
     * @return The constructed list.
     */
    private ArrayList<Product> getProductsFromDatabase() {
        final String databaseName = "Product";
        database2 = FirebaseDatabase.getInstance().getReference().child(databaseName);

        database2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    productList.add(snapshot.getValue(Product.class));
                }

                if (!productList.isEmpty()) {
                    LOGGER.info("List is not empty.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
            }
        });

        LOGGER.info("Product database has been retrieved.");
        for (int i = 1; i < productList.size(); i++) {
            LOGGER.info("Product database " + productList.get(i));

        }
        return productList;
    }


    private int findMaxIdFromProductsList() {
        ArrayList<Product> productsList = getProductsFromDatabase();

        for (int i = 1; i < productsList.size(); i++) {
            LOGGER.info("Product database " + productList.get(i));

        }

        int max = productsList.get(0).getId();
        int productsListSize = productsList.size();

        // loop to find maximum from ArrayList
        for (int i = 1; i < productsListSize; i++) {
            if (productsList.get(i).getId() > max) {
                max = productsList.get(i).getId();
            }
        }

        return max + 1;
    }

}