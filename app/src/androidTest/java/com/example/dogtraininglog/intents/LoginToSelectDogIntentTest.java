package com.example.dogtraininglog.intents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.Instrumentation;


import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.rule.IntentsRule;

import com.example.dogtraininglog.LoginActivity;
import com.example.dogtraininglog.SelectDogActivity;
import com.example.dogtraininglog.R;
import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.UserDAO;
import com.example.dogtraininglog.database.entities.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/*Does button to go from log into select dog page work?*/
@RunWith(AndroidJUnit4.class)
public class LoginToSelectDogIntentTest {

    @Rule public IntentsRule intentsRule = new IntentsRule();
    @Rule public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /*Because the page is protected by log in, give it a fake user*/
    @Before
    public void seedKnownUser() throws Exception {
        activityRule.getScenario().onActivity(activity -> {
            DogTrainingDatabase db = DogTrainingDatabase.getDatabase(activity.getApplicationContext());
            DogTrainingDatabase.databaseWriteExecutor.execute(() -> {
                UserDAO dao = db.userDAO();

                /*Clear all users so it is empty*/
                try { dao.deleteAll(); } catch (Exception ignored) {}

                /*Crete a default user*/
                User admin = new User("admin1", "admin1");
                try { admin.setAdmin(true); } catch (Exception ignored) {}

                /*Insert user*/
                try { dao.insert(admin); } catch (Exception ignored) {}
            });
        });
        Thread.sleep(400);
    }

    @Test
    public void clickingLogin_opensSelectDog() {
        intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        /*enter in username and password*/
        onView(withId(R.id.userNameLoginEditText)).perform(replaceText("admin1"), closeSoftKeyboard());
        onView(withId(R.id.passwordLoginEditText)).perform(replaceText("admin1"), closeSoftKeyboard());

        /*Click button, see what happens.*/
        onView(withId(R.id.loginButton)).perform(click());
        intended(hasComponent(SelectDogActivity.class.getName()));
    }
}
