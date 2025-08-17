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

import com.example.dogtraininglog.AdminActivity;
import com.example.dogtraininglog.R;

import com.example.dogtraininglog.database.ViewLogsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminToViewLogsIntentTest {

    @Rule public IntentsRule intentsRule = new IntentsRule();
    @Rule public ActivityScenarioRule<AdminActivity> activityRule =
            new ActivityScenarioRule<>(AdminActivity.class);

    @Test
    public void clickingAllLogs_opensViewLogs() {
        intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        onView(androidx.test.espresso.matcher.ViewMatchers.withId(R.id.btnAllLogs)).perform(click());
        intended(hasComponent(ViewLogsActivity.class.getName()));
    }
}
