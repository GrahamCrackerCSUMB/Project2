package com.example.dogtraininglog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.Observer;


import androidx.appcompat.app.AppCompatActivity;

import com.example.dogtraininglog.database.DogRepository;
import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.ViewLogsActivity;
import com.example.dogtraininglog.database.entities.Dog;
import com.example.dogtraininglog.database.entities.User;

import java.util.ArrayList;
import java.util.List;


public class SelectDogActivity extends AppCompatActivity{
    public static final String EXTRA_USER_ID = "com.example.dogtraininglog.extra.USER_ID";
    public static final String EXTRA_DOG_ID  = "com.example.dogtraininglog.extra.DOG_ID";

    private int userId;

    private Spinner spinnerDogs;
    private Button btnAddDog, btnContinue;
    private TextView tvWelcome;

    private DogRepository dogRepo;
    private final List<Dog> currentDogs = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dog);

        /*Check to see if we have a vla*/
        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (userId <= 0) {
            finish();
            return;
        }

        // Bind views
        spinnerDogs = findViewById(R.id.spinnerDogs);
        btnAddDog   = findViewById(R.id.btnAddDog);
        btnContinue = findViewById(R.id.btnContinue);
        tvWelcome   = findViewById(R.id.tvUserName);

        dogRepo = new DogRepository(getApplication());

        // Setup spinner adapter
        spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>()
        );
        spinnerDogs.setAdapter(spinnerAdapter);

        // Observe dogs for this user
        dogRepo.getDogsForUser(userId).observe(this, new Observer<List<Dog>>() {
            @Override
            public void onChanged(List<Dog> dogs) {
                currentDogs.clear();
                spinnerAdapter.clear();
                if (dogs != null) {
                    currentDogs.addAll(dogs);
                    for (Dog d : dogs) {
                        spinnerAdapter.add(d.getName());
                    }
                }
                spinnerAdapter.notifyDataSetChanged();
            }
        });

        /*If you click on add dog you go to a new screen*/
        btnAddDog.setOnClickListener(v -> {
            Intent addIntent = new Intent(this, AddDogActivity.class);
            addIntent.putExtra(EXTRA_USER_ID, userId);
            startActivity(addIntent);
        });

        /*When they click continue go to see training, new activity*/
        btnContinue.setOnClickListener(v -> {
            if (currentDogs.isEmpty()) return;
            int pos = spinnerDogs.getSelectedItemPosition();
            if (pos < 0 || pos >= currentDogs.size()) return;

            int dogId = currentDogs.get(pos).getId();

            Intent logsIntent = new Intent(this, ViewLogsActivity.class);
            logsIntent.putExtra(EXTRA_USER_ID, userId);
            logsIntent.putExtra(EXTRA_DOG_ID, dogId);
            startActivity(logsIntent);
        });
    }

    /* Intent factory */
    public static Intent selectDogIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, SelectDogActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }
}

