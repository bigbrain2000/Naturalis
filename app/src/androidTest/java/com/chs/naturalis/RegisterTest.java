package com.chs.naturalis;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterTest extends TestCase {

    @Rule
    private final ActivityScenarioRule<Register> mainActivityActivityTestRule = new ActivityScenarioRule<>(Register.class);

    private final String NAME = "Alex";
    private final String PASSWORD = "Jmekerie";
    private final UUID randomUUID = UUID.randomUUID();
    private final String EMAIL = randomUUID.toString().replaceAll("_", "");
    private final String PHONE_NUMBER = "0123456789";
    private final String ADDRESS = "Timisoara";

    @Test
    public void ceva() {
        // type text in the EditText field
        onView(withId(R.id.name)).perform(typeText(NAME));
        onView(withId(R.id.password)).perform(typeText(PASSWORD));
        onView(withId(R.id.email)).perform(typeText(EMAIL));
        onView(withId(R.id.phoneNumber)).perform(typeText(PHONE_NUMBER));
        onView(withId(R.id.address)).perform(typeText(ADDRESS));

        // close the keyboard
        closeSoftKeyboard();

        onView(withId(R.id.loginButton)).perform(click());
    }
}