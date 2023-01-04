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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

public class HomePageAdmin extends AppCompatActivity {

    private ListView productListView;
    private BottomNavigationView adminNavigation;

    private final List<Product> productList = new ArrayList<>();
    private final List<Product> productList2 = new ArrayList<>();
    private final String DATABASE_NAME = "Product";
    private static String productName;
    private DatabaseReference database;

    private static final Logger LOGGER = getLogger(HomePageAdmin.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_home_page_admin);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        setListViewItems();
        actionOnNavBarItemSelected();

        setActionOnItemClicked();
    }

    /**
     * Identify the activity field by their id.
     */
    private void identifyTheFieldsById() {
        adminNavigation = findViewById(R.id.adminNavigation);
        productListView = findViewById(R.id.productListView);

    }

    @SuppressLint("NonConstantResourceId")
    private void actionOnNavBarItemSelected() {
        adminNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.goBackButton:
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


    private void transitionToAddProductActivity() {
        new Handler().post(() -> {
            Intent intent = new Intent(HomePageAdmin.this, AddProduct.class);
            startActivity(intent);
            finish();
        });
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
                        productsNameList.add(product.getName());
                    }

                    //Set the listview items as the productsNameList objects.
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HomePageAdmin.this,
                            android.R.layout.simple_list_item_multiple_choice, productsNameList);
                    productListView.setAdapter(adapter);
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(HomePageAdmin.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * A user click on an a chosen item from the list and he is gonna be redirected to the
     * ViewProduct activity where he will see all the details about the selected product.
     */
    private void setActionOnItemClicked() {
        database = FirebaseDatabase.getInstance().getReference().child(DATABASE_NAME);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LOGGER.info("Product database has been retrieved.");
                    productList2.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList2.add(snapshot.getValue(Product.class));
                    }

                    if (!productList2.isEmpty()) {
                        LOGGER.info("List is not empty.");
                    }

                    productListView.setOnItemClickListener((adapterView, view, position, id) -> {

                        //get the name of the selected item at the clicked position
                        productName = (String) adapterView.getItemAtPosition(position);

                        new Handler().post(() -> {
                            Intent intent = new Intent(HomePageAdmin.this, ViewProductAdmin.class);
                            startActivity(intent);
                            finish();
                        });
                        LOGGER.info("Transition to ViewProduct was made.");
                    });
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(HomePageAdmin.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Defined an alert box in case the user wants to logout from the app.
     * Yes, he is redirected to Login page.
     * No, he stays in the same activity.
     */
    private void showAlertBoxForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageAdmin.this);

        builder.setMessage("Do you want to exit the application?");
        builder.setTitle("Alert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(HomePageAdmin.this, Login.class);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Getter for returning the name of the selected item from the products list.
     *
     * @return The name of the selected item.
     */
    public static String getProductName() {
        return productName;
    }
}