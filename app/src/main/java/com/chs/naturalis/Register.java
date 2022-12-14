package com.chs.naturalis;

import static android.graphics.Color.BLACK;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Logger.getLogger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chs.exceptions.naturalis.FieldNotCompletedException;
import com.chs.exceptions.naturalis.InvalidEmailException;
import com.chs.exceptions.naturalis.UserEmailAlreadyExistsException;
import com.chs.naturalis.model.Discount;
import com.chs.naturalis.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private boolean flag = true;

    private static final Logger LOGGER = getLogger(Register.class.getName());

    /*
       Field used for making a delay of 100ms when switching the activities
     */
    private static final int SPLASH_SCREEN = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_register);
        super.onCreate(savedInstanceState);

        //function executed only once when the app is turned on for the first time
        //has the functionality of inserting the predefined admin in the DB
        insertPredefinedAdmin();

        registerUser();

        togglePasswordVisibility();

        pushSignInButton();

        registerButton.setBackgroundColor(Color.parseColor("#2BCC6F"));
        registerButton.setTextColor(BLACK);
        goBackToLogin.setBackgroundColor(Color.parseColor("#2BCC6F"));
        goBackToLogin.setTextColor(BLACK);

        //EXDE BIG CAT MOMENT
        MediaPlayer cat = MediaPlayer.create(this, R.raw.cat_sound);
        name.setOnTouchListener((view, motionEvent) -> {
            cat.start();
            return false;
        });
    }

    /**
     * Identify the activity field by their id.
     */
    private void identifyTheUserFieldsById() {
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        registerButton = findViewById(R.id.loginButton);
        goBackToLogin = findViewById(R.id.goBackToLogin);
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
                makeText(Register.this, "Error on retrieving data from database.", LENGTH_LONG).show();
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

    private void clearFields() {
        name.getText().clear();
        password.getText().clear();
        email.getText().clear();
        phoneNumber.getText().clear();
        address.getText().clear();
    }

    private void insertUserIntoDb() {
        try {
            user = new User();
            //get the values from the text fields
            String text_email = email.getText().toString().trim();
            String text_password = password.getText().toString().trim();
            String encoded_password = encodePassword(text_email, text_password);

            user.setName(name.getText().toString().trim());
            user.setPassword(encoded_password);
            user.setEmail(email.getText().toString().trim());
            user.setPhoneNumber(phoneNumber.getText().toString().trim());
            user.setAddress(address.getText().toString().trim());
            user.setRole("Client");

            checkAllFieldsAreCompleted(name, password, email, phoneNumber, address);

            checkUserEmailAlreadyExists(email.getText().toString());
            checkIfEmailIsValid(email.getText().toString().trim());

            LOGGER.info("User account has been created.");
            makeText(Register.this, "Your account has been successfully created!", LENGTH_LONG).show();
            //push them in the DB
            database.push().setValue(user);
            flag = true;
            LOGGER.info("User pushed to DB");

            clearFields();

        } catch (FieldNotCompletedException e) {
            makeText(Register.this, "Fields are not completed!", LENGTH_LONG).show();
            LOGGER.info("User account has not been created due to uncompleted fields.");
        } catch (InvalidEmailException e) {
            makeText(Register.this, e.getMessage(), LENGTH_LONG).show();
            LOGGER.info("User account has not been created due to invalid email.");
        } catch (UserEmailAlreadyExistsException e) {
            makeText(Register.this, e.getMessage(), LENGTH_LONG).show();
            LOGGER.info(e.getMessage());
        }
    }

    private void checkAllFieldsAreCompleted(@NotNull EditText name,
                                            @NotNull EditText password,
                                            @NotNull EditText email,
                                            @NotNull EditText phoneNumber,
                                            @NotNull EditText address) throws FieldNotCompletedException {
        checkFieldIsCompleted(name);
        checkFieldIsCompleted(password);
        checkFieldIsCompleted(email);
        checkFieldIsCompleted(phoneNumber);
        checkFieldIsCompleted(address);
    }

    /**
     * Check if the one field is completed.
     *
     * @param field The provided field.
     * @throws FieldNotCompletedException Exception thrown in case the the field is not completed.
     */
    private void checkFieldIsCompleted(EditText field) throws FieldNotCompletedException {
        if (field.getText().toString().isEmpty()) {
            field.setError("Field cannot be empty");
            flag = false;
            throw new FieldNotCompletedException();
        }
    }

    /**
     * Change the activity from Register to Login.
     */
    private void transitionToLoginPage() {
        registerButton = findViewById(R.id.loginButton);

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
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_24, 0);
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
     * When the SIGN IN button is pressed then the activity
     * is change from Register to Login.
     */
    private void pushSignInButton() {
        goBackToLogin.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Register.this, LeavesGifAnimation.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN);
            LOGGER.info("Transition to login was made.");
        });
    }

    /**
     * Query the database for finding if the name that a user introduced is already defined in the DB.
     *
     * @param email The user email introduced.
     * @throws UserEmailAlreadyExistsException The exception thrown if the username already exists.
     */
    private void checkUserEmailAlreadyExists(String email) throws UserEmailAlreadyExistsException {
        StringBuilder emailErrorMessage = new StringBuilder();

        emailErrorMessage.append("Email ").append(email).append(" already exists!");

        final ArrayList<User> userList = getUsersFromDatabase();
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                flag = false;
                LOGGER.info("User email is : " + user.getEmail());
                throw new UserEmailAlreadyExistsException(emailErrorMessage.toString());
            }
        }
    }

    /**
     * At starting the app, an admin account will always be present.
     */
    private void insertPredefinedAdmin() {
        final String adminEmail = "admin@yahoo.com";

        final ArrayList<User> userList = getUsersFromDatabase();
        for (User user : userList) {
            if (!user.getEmail().equals(adminEmail)) {
                User newUser = new User();

                newUser.setName("admin");
                newUser.setPassword("admin");
                newUser.setEmail("admin@yahoo.com");
                newUser.setPhoneNumber("1234567891");
                newUser.setAddress("Str.Bucuriei nr.7");
                newUser.setRole("Admin");
                database.push().setValue(newUser);
            }
        }
    }

    /**
     * Insert into the database the name of the Discount.
     */
    private void insertPredefinedDiscount() {
        Discount discount = new Discount("Naturalis");
        database = FirebaseDatabase.getInstance().getReference().child("Discount");
        database.push().setValue(discount);
    }

    @NotNull
    public static String encodePassword(@NotNull String salt,
                                        @NotNull String password) {
        MessageDigest md = getMessageDigest();
        md.update(salt.getBytes(UTF_8));

        byte[] hashedPassword = md.digest(password.getBytes(UTF_8));

        // This is the way a password should be encoded when checking the credentials
        //to be able to save in JSON format
        return new String(hashedPassword, UTF_8).replace("\"", "");
    }

    private static MessageDigest getMessageDigest() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 does not exist!");
        }
        return md;
    }

}