package com.chs.naturalis;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
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
import android.view.MotionEvent;
import android.widget.Button;
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

public class ViewProductAdmin extends AppCompatActivity {

    private TextView category, name, price, description;
    private BottomNavigationView adminNavigationView;

    private DatabaseReference database;
    private float x1, x2, y1, y2;
    private final String DATABASE_NAME = "Product";
    private final List<Product> productList = new ArrayList<>();
    private Button editProductButton, deleteProductButton;

    //Take the product name when the client clicked on in it within the ViewSyrupProducts class
    private final String PRODUCT_NAME = getProductName();

    private static final Logger LOGGER = getLogger(ViewProductAdmin.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_view_product_admin);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        viewProduct();

        actionOnNavBarItemSelected();

        setDeleteButtonAction();

        setEditButtonAction();
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
        editProductButton = findViewById(R.id.editProductButton);
        deleteProductButton = findViewById(R.id.deleteProductButton);
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
                makeText(ViewProductAdmin.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Deletes the product from the database.
     */
    private void deleteProduct() {
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
                            productList.remove(product);
                            LOGGER.info("Product" + PRODUCT_NAME + " has been deleted.");
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
                makeText(ViewProductAdmin.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Set the action on the deleteProductButton to display the alert box.
     */
    private void setDeleteButtonAction() {
        deleteProductButton.setOnClickListener(v -> showAlertBoxForDeletingProduct());
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
            Intent intent = new Intent(ViewProductAdmin.this, HomePageAdmin.class);
            startActivity(intent);
            finish();
        });
    }

    private void transitionToAddProductActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(ViewProductAdmin.this, AddProduct.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Set the action on the editProductButton to open the {@link EditProductAdmin}
     */
    private void setEditButtonAction() {
        editProductButton.setOnClickListener(v ->
                new Handler().post(() -> {
                    Intent intent = new Intent(ViewProductAdmin.this, EditProductAdmin.class);
                    startActivity(intent);
                    finish();
                })
        );
    }

    /**
     * Sliding right opens the {@link Login} activity.
     * Sliding left opens the {@link ShoppingCart} activity.
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
                    Intent intent = new Intent(ViewProductAdmin.this, HomePageAdmin.class);
                    startActivity(intent);
                }

                if (x1 > x2) {
                    showAlertBoxForLogout();
                }
                break;
        }

        return false;
    }

    /**
     * Defined an alert box in case the user wants to logout from the app.
     * Yes, he is redirected to Login page.
     * No, he stays in the same activity.
     */
    private void showAlertBoxForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductAdmin.this);

        builder.setMessage("Do you want to exit the application?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(ViewProductAdmin.this, Login.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Defined an alert box in case the ADMIN wants to delete a product.
     * Yes, he deletes the selected product.
     * No, the database is not updated.
     */
    private void showAlertBoxForDeletingProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProductAdmin.this);

        builder.setMessage("Do you want to delete the product?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //call the delete product method
            deleteProduct();

            //refresh the activity
            Intent intent = new Intent(ViewProductAdmin.this, HomePageAdmin.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}