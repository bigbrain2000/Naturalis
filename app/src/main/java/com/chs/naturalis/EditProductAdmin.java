package com.chs.naturalis;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.chs.naturalis.HomePageAdmin.getProductName;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

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

public class EditProductAdmin extends AppCompatActivity {

    private EditText category, name, price, description;
    private BottomNavigationView adminNavigationView;

    private DatabaseReference database;
    private float x1, x2, y1, y2;
    private final String DATABASE_NAME = "Product";
    private final List<Product> productList = new ArrayList<>();
    private Button saveProductButton;

    //Take the product name when the client clicked on in it within the ViewSyrupProducts class
    private final String PRODUCT_NAME = getProductName();

    private static final Logger LOGGER = getLogger(EditProductAdmin.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_edit_product_admin);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        viewProduct();

        saveProduct();

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
        adminNavigationView = findViewById(R.id.adminNavigationView);
        saveProductButton = findViewById(R.id.saveProductButton);
    }

    /**
     * View all the details about the product selected by the user.
     */
    private void viewProduct() {
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
                        if (product.getName().equals(PRODUCT_NAME)) {
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
                makeText(EditProductAdmin.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Set the changes done to the product when clicking the SAVE button.
     */
    private void saveProduct() {
        saveProductButton.setOnClickListener(v -> showAlertBoxForSavingProduct());
    }

    /**
     * Save the product from the database.
     */
    private void saveProductIntoDatabase() {
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

                    Product newProduct;
                    for (Product product : productList) {
                        if (product.getName().equals(PRODUCT_NAME)) {
                            newProduct = new Product();
                            newProduct.setId(product.getId());
                            newProduct.setName(product.getName());
                            newProduct.setPrice(product.getPrice());
                            newProduct.setCurrency(product.getCurrency());
                            newProduct.setCategory(product.getCategory());
                            newProduct.setQuantity(product.getQuantity());
                            newProduct.setDescription(product.getDescription());
                            productList.remove(product);
                            productList.add(newProduct);
                        }
                    }

                    database.setValue(productList);
                    LOGGER.info("Database has been updated.");
                } else {
                    LOGGER.info("DataSnapshot error.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(EditProductAdmin.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Defined an alert box in case the ADMIN wants to delete a product.
     * Yes, he deletes the selected product.
     * No, the database is not updated.
     */
    private void showAlertBoxForSavingProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProductAdmin.this);

        builder.setMessage("Do you want to save the changes?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //call the save product method
            saveProductIntoDatabase();

            //refresh the activity
            Intent intent = new Intent(EditProductAdmin.this, HomePageAdmin.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        adminNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.goBackButton:
                    transitionToHomePageAdminActivity();
                    return true;
                case R.id.addProductButton:
                    transitionToAddProductActivity();
                    return true;
                case R.id.logoutButton:
                    showAlertBoxForLogout();
                    return true;
            }
            return false;
        });
    }

    private void transitionToHomePageAdminActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(EditProductAdmin.this, HomePageAdmin.class);
            startActivity(intent);
            finish();
        });
    }

    private void transitionToAddProductActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(EditProductAdmin.this, AddProduct.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Defined an alert box in case the user wants to logout from the app.
     * Yes, he is redirected to Login page.
     * No, he stays in the same activity.
     */
    private void showAlertBoxForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProductAdmin.this);

        builder.setMessage("Do you want to exit the application?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(EditProductAdmin.this, Login.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}