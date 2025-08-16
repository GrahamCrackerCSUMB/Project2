package com.example.dogtraininglog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.dogtraininglog.database.DogRepository;
import com.example.dogtraininglog.database.UserRepository;
import com.example.dogtraininglog.database.entities.Dog;

import java.util.ArrayList;
import java.util.List;


public class SelectDogActivity extends AppCompatActivity{
    public static final String EXTRA_USER_ID = "com.example.dogtraininglog.extra.USER_ID";
    public static final String EXTRA_DOG_ID  = "com.example.dogtraininglog.extra.DOG_ID";
    public static final String EXTRA_DOG_NAME = "com.example.dogtraininglog.extra.DOG_NAME";


    private static final String PREFS = "app_prefs";
    private static final String KEY_LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID";

    private int userId;

    private Spinner spinnerDogs;
    private Button btnAddDog, btnContinue, btnLogout, btnAdmin;
    private TextView tvWelcome;

    private DogRepository dogRepo;
    private final List<Dog> currentDogs = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dog);


        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (userId <= 0) {
            int prefUserId = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                    .getInt(getString(R.string.preference_userId_key), -1);
            userId = prefUserId;
        }
        if (userId <= 0) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        spinnerDogs = findViewById(R.id.spinnerDogs);
        btnAddDog   = findViewById(R.id.btnAddDog);
        btnContinue = findViewById(R.id.btnContinue);
        tvWelcome   = findViewById(R.id.tvUserName);


        btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                        .edit()
                        .remove(getString(R.string.preference_userId_key))
                        .apply();
                getSharedPreferences(PREFS, MODE_PRIVATE)
                        .edit()
                        .remove(KEY_LOGGED_IN_USER_ID)
                        .apply();

                Intent i = new Intent(SelectDogActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            });
        }

        dogRepo = new DogRepository(getApplication());
        btnAdmin = findViewById(R.id.btnAdmin);
        btnAdmin.setVisibility(View.GONE); // hidden by default

        UserRepository userRepo = UserRepository.getRepository(getApplication());

        userRepo.getUserByIdLive(userId).observe(this, u -> {
            if (u != null) {
                String name = u.getUsername();
                if (name != null && !name.isEmpty()) {
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                }
                tvWelcome.setText(name);

                if (u.isAdmin()) {
                    tvWelcome.append(" (ADMIN)");
                    btnAdmin.setVisibility(View.VISIBLE);
                    btnAdmin.setOnClickListener(v -> {
                        Intent i = new Intent(this, AdminActivity.class);
                        startActivity(i);
                    });
                }
            }
        });

        spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>()
        );
        spinnerDogs.setAdapter(spinnerAdapter);

        dogRepo.getDogsForUser(userId).observe(this, dogs -> {
            currentDogs.clear();
            spinnerAdapter.clear();
            if (dogs != null) {
                currentDogs.addAll(dogs);
                for (Dog d : dogs) spinnerAdapter.add(d.getName());
            }
            spinnerAdapter.notifyDataSetChanged();
        });

        btnAddDog.setOnClickListener(v -> {
            Intent addIntent = new Intent(this, AddDogActivity.class);
            addIntent.putExtra(EXTRA_USER_ID, userId);
            startActivity(addIntent);
        });

        btnContinue.setOnClickListener(v -> {
            if (currentDogs.isEmpty()) {
                Toast.makeText(
                        SelectDogActivity.this,
                        "Select a dog or add one.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }
            int pos = spinnerDogs.getSelectedItemPosition();
            int dogId = currentDogs.get(pos).getId();
            String dogName = currentDogs.get(pos).getName();

            Intent next = new Intent(this, MainActivity.class);
            next.putExtra(EXTRA_USER_ID, userId);
            next.putExtra(EXTRA_DOG_ID,  dogId);
            next.putExtra(EXTRA_DOG_NAME, dogName);
            startActivity(next);
        });
    }



    /* Intent factory */
    public static Intent selectDogIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, SelectDogActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }
}

