package com.example.dogtraininglog.intents;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dogtraininglog.MainActivity;
import com.example.dogtraininglog.R;
import com.example.dogtraininglog.SelectDogActivity;

import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.entities.Dog;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SelectDogToMainIntentTest {

    @Rule public IntentsRule intentsRule = new IntentsRule();

    @Rule public ActivityScenarioRule<SelectDogActivity> activityRule =
            new ActivityScenarioRule<>(
                    new Intent(
                            ApplicationProvider.getApplicationContext(),
                            SelectDogActivity.class
                    ).putExtra(SelectDogActivity.EXTRA_USER_ID, 1)
            );

    @Before
    public void seedDog() throws Exception {
        activityRule.getScenario().onActivity(a -> {
            DogTrainingDatabase db = DogTrainingDatabase.getDatabase(a.getApplicationContext());
            DogTrainingDatabase.databaseWriteExecutor.execute(() -> {
                Dog d = new Dog();
                try { d.setUserId(1); } catch (Exception ignored) {}
                try { d.setName("Test Dog"); } catch (Exception ignored) {}
                try { d.setAge(3); } catch (Exception ignored) {}
                try { db.dogDAO().insert(d); } catch (Exception ignored) {}
            });
        });
        Thread.sleep(500);
    }

    @Test
    public void clickingContinue_opensMain() {
        intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        onView(withId(R.id.spinnerDogs)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.btnContinue)).perform(click());

        intended(hasComponent(MainActivity.class.getName()));
    }
}
