package com.chs.naturalis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
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

public class TeaProducts extends AppCompatActivity {

    private ListView teaProductListView;

    private DatabaseReference database;
    private final List<Product> productList = new ArrayList<>();
    private final List<Product> productList2 = new ArrayList<>();
    private static String productName;
    private final String DATABASE_NAME = "Product";
    private float x1, x2, y1, y2;

    private static final Logger LOGGER = getLogger(TeaProducts.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_tea_products);

        super.onCreate(savedInstanceState);

        identifyTheFieldsById();

        setListViewItems();

        setActionOnItemClicked();
    }

    /**
     * Identify the activity field by their id.
     */
    private void identifyTheFieldsById() {
        teaProductListView = findViewById(R.id.teaProductListView);
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

                    teaProductListView.setOnItemClickListener((adapterView, view, position, id) -> {

                        //get the name of the selected item at the clicked position
                        productName = (String) adapterView.getItemAtPosition(position);

                        new Handler().post(() -> {
                            Intent intent = new Intent(TeaProducts.this, ViewTeaProduct.class);
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
                makeText(TeaProducts.this, "Error on retrieving data from database.", LENGTH_LONG).show();
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
                        if (product.getQuantity() > 0 && product.getCategory().equals("Tea")) {
                            productsNameList.add(product.getName());
                        }
                    }

                    //Set the listview items as the productsNameList objects.
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TeaProducts.this,
                            android.R.layout.simple_list_item_multiple_choice, productsNameList);
                    teaProductListView.setAdapter(adapter);
                } else {
                    LOGGER.info("DataSnapshot error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(TeaProducts.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });
    }

    /**
     * Getter for returning the name of the selected item from the Tea list.
     *
     * @return The name of the selected item.
     */
    public static String getTeaProductName() {
        return productName;
    }

    /**
     * Sliding left opens the {@link HomePageClient} activity.
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
                    Intent intent = new Intent(TeaProducts.this, HomePageClient.class);
                    startActivity(intent);
                }

                if (x1 > x2) {
                    return true;
                }
                break;
        }

        return false;
    }
}