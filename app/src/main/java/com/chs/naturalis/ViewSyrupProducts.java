package com.chs.naturalis;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class ViewSyrupProducts extends AppCompatActivity {

    private ListView productListView;
    private DatabaseReference database;
    private final List<Product> productList = new ArrayList<>();
    private final List<Product> productList2 = new ArrayList<>();
    private static String productName;
    private final String DATABASE_NAME = "Product";
    private static final int SPLASH_SCREEN = 10;

    private static final Logger LOGGER = getLogger(ViewSyrupProducts.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_syrup_products);

        identifyTheFieldsById();

        setListViewItems();

        setActionOnItemClicked();
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

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(ViewSyrupProducts.this, ViewProduct.class);
                            startActivity(intent);
                            finish();
                        }, SPLASH_SCREEN);
                        LOGGER.info("Transition to ViewProduct was made.");
                    });
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(ViewSyrupProducts.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    private void identifyTheFieldsById() {
        productListView = findViewById(R.id.productListView);
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewSyrupProducts.this,
                            android.R.layout.simple_list_item_multiple_choice, productsNameList);
                    productListView.setAdapter(adapter);
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(ViewSyrupProducts.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Getter for returning the name of the selected item from the Syrup list.
     *
     * @return The name of the selected item.
     */
    public static String getProductName() {
        return productName;
    }
}