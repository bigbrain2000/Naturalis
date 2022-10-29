package com.chs.naturalis;

import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chs.exceptions.naturalis.FieldNotCompletedException;
import com.chs.exceptions.naturalis.InvalidEmailException;
import com.chs.exceptions.naturalis.UsernameAlreadyExistsException;
import com.chs.naturalis.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Register extends AppCompatActivity {

    private EditText name, password, email, phoneNumber, address;
    private Button registerButton;
    private Button goBackToLogin;
    private DatabaseReference database;
    private User user;
    private boolean passwordVisible = false;
    private final ArrayList<User> userList = new ArrayList<>();

    private static final Logger LOGGER = getLogger(Register.class.getName());

    /*
       Field used for making a delay of 100ms when switching the activities
     */
    private static final int SPLASH_SCREEN = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUser();

        togglePasswordVisibility();

        pushSignInButton();
    }

    private ArrayList<User> getUsersFromDatabase() {
        final String userDatabaseName = "User";
        database = FirebaseDatabase.getInstance().getReference().child(userDatabaseName); //we can insert users in this way in the DB

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
                Toast.makeText(Register.this, "Error on retrieving data from database.", Toast.LENGTH_LONG).show();
            }
        });

        LOGGER.info("User database has been retrieved.");
        return userList;
    }

    private void registerUser() {
        final String userDatabaseName = "User";
        identifyTheUserFieldsById();

        database = FirebaseDatabase.getInstance().getReference().child(userDatabaseName); //we can insert users in this way in the DB

        registerButton.setOnClickListener(view -> {
            insertUserIntoDb();

            if (flag)
                transitionToLoginPage();
        });
    }

    private void identifyTheUserFieldsById() {
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        registerButton = findViewById(R.id.registerButton);
    }

    boolean flag = true;

    private void insertUserIntoDb() {
        try {
            user = new User();
            //get the values from the text fields
            user.setName(name.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());
            user.setPhoneNumber(phoneNumber.getText().toString().trim());
            user.setAddress(address.getText().toString().trim());

            checkAllFieldsAreCompleted(name.getText().toString(), password.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), address.getText().toString());

            checkUserAlreadyExists(name.getText().toString());
            checkIfEmailIsValid(email.getText().toString().trim());

            LOGGER.info("User account has been created.");
            Toast.makeText(Register.this, "Your account has been successfully created!", Toast.LENGTH_LONG).show();
            //push them in the DB
            database.push().setValue(user);
            flag = true;
            LOGGER.info("User pushed to DB");

        } catch (FieldNotCompletedException e) {
            Toast.makeText(Register.this, "Fields are not completed!", Toast.LENGTH_LONG).show();
            LOGGER.info("User account has not been created due to uncompleted fields.");
        } catch (InvalidEmailException e) {
            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
            LOGGER.info("User account has not been created due to invalid email.");
        } catch (UsernameAlreadyExistsException e) {
            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
            LOGGER.info(e.getMessage());
        }
    }

    private void checkAllFieldsAreCompleted(@NotNull String name, @NotNull String password, @NotNull String email, @NotNull String phoneNumber, @NotNull String address) throws FieldNotCompletedException {

        if (name.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty() || phoneNumber.trim().isEmpty() || address.trim().isEmpty()) {
            flag = false;
            throw new FieldNotCompletedException();
        }
    }

    /**
     * Change the activity from Register to Login.
     */
    private void transitionToLoginPage() {
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN);

            LOGGER.info("Transition to login was made.");
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    /**
     * Based on a counter we can toggle the password visibility.
     * If the user clicks the invisibility icon once then the password will be visible.
     * If the user clicks the visibility icon once then the password will be invisible.
     */ private void togglePasswordVisibility() {
        password.setOnTouchListener((view, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() > password.getRight() - password.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = password.getSelectionEnd();
                    if (passwordVisible) {
                        //set drawable image here
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        //for hide password
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        //for hide password
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    password.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Call the isValidEmailAddress method and throw an exception is the email is not valid.
     *
     * @param email The given email in RFC822 format
     * @throws InvalidEmailException The exception thrown if the email is not valid.
     */
    public void checkIfEmailIsValid(@NotNull String email) throws InvalidEmailException {
        StringBuilder emailErrorMessage = new StringBuilder();

        emailErrorMessage.append("Email ").append(email).append(" is not valid!");

        if (!isValidEmailAddress(email)) {
            flag = false;
            throw new InvalidEmailException(emailErrorMessage.toString());
        }
    }

    /**
     * Check if the user email is valid by parsing the given string and
     * create an InternetAddress.
     *
     * @param email The given email in RFC822 format
     * @return True if the given email is valid and false otherwise
     */
    private boolean isValidEmailAddress(@NotNull String email) {

        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
            LOGGER.info("The email was valid");
        } catch (AddressException ex) {
            result = false;
            LOGGER.info("The email was not valid");
        }

        return result;
    }

    /**
     * When the SIGNIN button is pressed then the activity
     * is change from Register to Login.
     */
    private void pushSignInButton() {
        goBackToLogin = findViewById(R.id.goBackToLogin);

        goBackToLogin.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN);
            LOGGER.info("Transition to login was made.");
        });
    }

    /**
     * Query the database for finding if the name that a user introduced is already defined in the DB.
     *
     * @param username The user name introduced.
     * @throws UsernameAlreadyExistsException The exception thrown if the username already exists.
     */
    private void checkUserAlreadyExists(String username) throws UsernameAlreadyExistsException {
        StringBuilder nameErrorMessage = new StringBuilder();

        nameErrorMessage.append("Username ").append(username).append(" already exists!");

        final ArrayList<User> userList = getUsersFromDatabase();
        for (User user : userList) {
            if (user.getName().equals(username)) {
                flag = false;
                LOGGER.info("User is : " + user.getName());
                throw new UsernameAlreadyExistsException(nameErrorMessage.toString());
            }
        }
    }
}