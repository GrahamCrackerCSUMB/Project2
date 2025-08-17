package com.example.dogtraininglog.intents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.app.Activity;
import android.app.Instrumentation;


import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.rule.IntentsRule;

import com.example.dogtraininglog.ManageUsersActivity;
import com.example.dogtraininglog.LoginActivity;
import com.example.dogtraininglog.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*Can we go from manage users to the login page when we click log out button?*/
@RunWith(AndroidJUnit4.class)
public class ManageUsersToLoginIntentTest {

    @Rule public IntentsRule intentsRule = new IntentsRule();
    @Rule public ActivityScenarioRule<ManageUsersActivity> activityRule =
            new ActivityScenarioRule<>(ManageUsersActivity.class);

    /*Actual test*/
    @Test
    public void clickingLogoutFromManageUsers_opensLogin() {
        intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        /*Click and see what happens*/
        onView(androidx.test.espresso.matcher.ViewMatchers.withId(R.id.btnLogout)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }
}
