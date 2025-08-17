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
import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dogtraininglog.LoginActivity;
import com.example.dogtraininglog.R;
import com.example.dogtraininglog.SelectDogActivity;
import com.example.dogtraininglog.database.ViewLogsActivity;
import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.entities.Dog;
import com.example.dogtraininglog.database.entities.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ViewLogsToLoginIntentTest {

    @Rule public IntentsRule intentsRule = new IntentsRule();

    @Test
    public void clickingLogoutFromViewLogs_opensLogin() throws Exception {
        Context ctx = ApplicationProvider.getApplicationContext();
        DogTrainingDatabase db = DogTrainingDatabase.getDatabase(ctx);
        CountDownLatch latch = new CountDownLatch(1);
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> {
            try { db.userDAO().insert(new User("admin1","admin1")); } catch (Exception ignored) {}
            Dog d = new Dog();
            try { d.setId(1); } catch (Exception ignored) {}
            try { d.setUserId(1); } catch (Exception ignored) {}
            try { d.setName("Test Dog"); } catch (Exception ignored) {}
            try { db.dogDAO().insert(d); } catch (Exception ignored) {}
            latch.countDown();
        });
        latch.await(1, TimeUnit.SECONDS);

        Intent intent = new Intent(ctx, ViewLogsActivity.class)
                .putExtra(SelectDogActivity.EXTRA_USER_ID, 1)
                .putExtra(SelectDogActivity.EXTRA_DOG_ID, 1);

        ActivityScenario<ViewLogsActivity> scenario = ActivityScenario.launch(intent);

        intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        onView(withId(R.id.btnLogout)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));

        scenario.close();
    }
}
