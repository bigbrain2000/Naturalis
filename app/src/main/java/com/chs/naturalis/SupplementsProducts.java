package com.chs.naturalis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import android.content.Intent;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.chs.naturalis.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SupplementsProducts extends AppCompatActivity {

    private ListView supplementsProductListView;

    private DatabaseReference database;
    private final List<Product> productList = new ArrayList<>();
    private final List<Product> productList2 = new ArrayList<>();
    private static String productName;
    private final String DATABASE_NAME = "Product";

    private static final Logger LOGGER = getLogger(SupplementsProducts.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_supplements);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        setListViewItems();

        setActionOnItemClicked();
    }

    /**
     * Identify the activity field by their id.
     */
    private void identifyTheFieldsById() {
        supplementsProductListView = findViewById(R.id.supplementsProductListView);
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

                    supplementsProductListView.setOnItemClickListener((adapterView, view, position, id) -> {

                        //get the name of the selected item at the clicked position
                        productName = (String) adapterView.getItemAtPosition(position);

                        new Handler().post(() -> {
                            Intent intent = new Intent(SupplementsProducts.this, ViewSupplementProduct.class);
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
                makeText(SupplementsProducts.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
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
                        if (product.getQuantity() > 0 && product.getCategory().equals("Supplements")) {
                            productsNameList.add(product.getName());
                        }
                    }

                    //Set the listview items as the productsNameList objects.
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SupplementsProducts.this,
                            android.R.layout.simple_list_item_multiple_choice, productsNameList);
                    supplementsProductListView.setAdapter(adapter);
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(SupplementsProducts.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Getter for returning the name of the selected item from the Supplements list.
     *
     * @return The name of the selected item.
     */
    public static String getSupplementsProductName() {
        return productName;
    }
}