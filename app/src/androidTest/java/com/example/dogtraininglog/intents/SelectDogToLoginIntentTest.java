package com.example.dogtraininglog.intents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dogtraininglog.LoginActivity;
import com.example.dogtraininglog.R;
import com.example.dogtraininglog.SelectDogActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*Can we go from the select do to the login page?*/
@RunWith(AndroidJUnit4.class)
public class SelectDogToLoginIntentTest {

    @Rule public IntentsRule intentsRule = new IntentsRule();

    @Rule public ActivityScenarioRule<SelectDogActivity> activityRule =
            new ActivityScenarioRule<>(
                    new Intent(
                            ApplicationProvider.getApplicationContext(),
                            SelectDogActivity.class
                    ).putExtra(SelectDogActivity.EXTRA_USER_ID, 1)
            );

    @Test
    public void clickingLogout_opensLogin() {
        intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        /*We click and see what happens*/
        onView(withId(R.id.btnLogout)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }
}
