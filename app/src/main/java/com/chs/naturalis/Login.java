package com.chs.naturalis;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.util.logging.Logger.getLogger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chs.exceptions.naturalis.FieldNotCompletedException;
import com.chs.naturalis.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class Login extends AppCompatActivity {

    private Button registerButton;
    private Button loginButton;
    private EditText email;
    private EditText password;
    private DatabaseReference database;
    private final ArrayList<User> userList = new ArrayList<>();
    private boolean flag = true;

    private static final Logger LOGGER = getLogger(Login.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        switchActivityToRegister();

        switchActivityBasedUserRole();
    }

    /**
     * If the SING UP button is clicked then the activity is switched from Login to Register.
     */
    private void switchActivityToRegister() {
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> new Handler().postDelayed(() -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        }, 1));
    }


    /**
     * Retrieve a list with all the users from the database.
     *
     * @return the found list.
     */
    private ArrayList<User> getUsersFromDatabase() {
        final String userDatabaseName = "User";
        database = FirebaseDatabase.getInstance().getReference().child(userDatabaseName);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userList.add(snapshot.getValue(User.class));
                }

                if (!userList.isEmpty()) {
                    LOGGER.info("List is not empty.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LOGGER.info("Error on retrieving data from database.");
                makeText(Login.this, "Error on retrieving data from database.", LENGTH_LONG).show();
            }
        });

        LOGGER.info("User database has been retrieved.");
        return userList;
    }

    /**
     * Based on the user credentials, he will be associated with a number.
     * If the user who is trying to login is a Client then return 1.
     * If the user who is trying to login is an Admin then return 2.
     * If there is an error at logging then return 0.
     *
     * @return an int
     */
    private int getValueBasedOnUserRole() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        final ArrayList<User> usersFromDatabase = getUsersFromDatabase();

        for (User user : usersFromDatabase) {
            if (Objects.equals(email.getText().toString(), user.getEmail()) && Objects.equals(password.getText().toString(), user.getPassword()))
                if (user.getRole().equals("Client")) {
                    return 1;
                } else {
                    return 2;
                }
        }

        return 0;
    }

    /**
     * Based on the associated user number then the login will be performed.
     * If the user who is trying to login is a Client then switch activity to HomePageClient.
     * If the user who is trying to login is an Admin then switch activity to HomePageAdmin.
     * If there is an error at logging then log a message.
     */
    private void switchActivityBasedUserRole() {
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> new Handler().postDelayed(() -> {
            int value = getValueBasedOnUserRole();
            LOGGER.info(value + " valoare");

            try {
                if (!flag)
                    checkAllFieldsAreCompleted(email.getText().toString(), password.getText().toString());
                if (value == 1) {


                    Intent intent = new Intent(Login.this, HomePageClient.class);
                    startActivity(intent);
                    finish();
                }
            } catch (FieldNotCompletedException e) {
                makeText(Login.this, "Fields are not completed!", LENGTH_LONG).show();
                LOGGER.info("User has not been logged in due to uncompleted fields.");
            }

            try {
                checkAllFieldsAreCompleted(email.getText().toString(), password.getText().toString());
                if (value == 2) {


                    Intent intent = new Intent(Login.this, HomePageAdmin.class);
                    startActivity(intent);
                    finish();
                }
            } catch (FieldNotCompletedException e) {
                makeText(Login.this, "Fields are not completed!", LENGTH_LONG).show();
                LOGGER.info("User has not been logged in due to uncompleted fields.");
            }
        }, 1));
    }


    private void checkAllFieldsAreCompleted(@NotNull String email,
                                            @NotNull String password) throws FieldNotCompletedException {

        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            flag = false;
            throw new FieldNotCompletedException();
        }
    }
}