package com.example.dogtraininglog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dogtraininglog.database.DogRepository;
import com.example.dogtraininglog.database.entities.Dog;

public class AddDogActivity extends AppCompatActivity{

    private static final String EXTRA_USER_ID = "com.example.dogtraininglog.adddog.USER_ID";
    private int userId;
    private EditText etDogName, etDogAge, etDogOwner, etDogNotes;
    private Button btnSave;
    private DogRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        userId = readUserId(getIntent());

        /*If we don't get something quit.*/
        if (userId <= 0) { finish(); return; }

        repo = new DogRepository(getApplication());

        /*Fields*/
        etDogName  = findViewById(R.id.etDogName);
        etDogAge   = findViewById(R.id.etDogAge);
        etDogNotes = findViewById(R.id.etDogNotes);
        etDogOwner = findViewById(R.id.etDogOwner);
        btnSave    = findViewById(R.id.btnSaveDog);

        /*Troublshooting*/
        if (etDogName == null || etDogAge == null || etDogOwner == null || etDogNotes == null || btnSave == null) {
            throw new IllegalStateException("One of the add dogs fields is null");
        }

        /*Back button*/
        Button backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                startActivity(SelectDogActivity.makeIntent(AddDogActivity.this, userId));
                finish();
            });
        }

        /*Save button*/
        btnSave.setOnClickListener(v -> {
            String name   = etDogName.getText().toString().trim();
            String ageStr = etDogAge.getText().toString().trim();
            String owner  = etDogOwner.getText().toString().trim();
            String notes  = etDogNotes.getText().toString().trim();

            if (name.isEmpty()) {
                etDogName.setError("Required");
                return;
            }

            int age = 0;
            if (!ageStr.isEmpty()) {
                try { age = Integer.parseInt(ageStr); }
                catch (NumberFormatException e) { etDogAge.setError("Enter a number"); return; }
            }

            Dog d = new Dog();
            d.setUserId(userId);
            d.setName(name);
            d.setAge(age);
            if (!owner.isEmpty()) d.setOwner(owner);
            if (!notes.isEmpty()) d.setNotes(notes);

            /*Troubleshooting some more*/
            android.util.Log.d("DogRepo", "Attempt insert userId=" + userId + " name=" + name + " age=" + age);
            repo.insert(d);

            setResult(RESULT_OK, new Intent());
            finish();
        });
    }

    /*Intent factory*/
    public static Intent makeIntent(Context ctx, int userId) {
        Intent i = new Intent(ctx, AddDogActivity.class);
        i.putExtra(EXTRA_USER_ID, userId);
        return i;
    }

    /*Gets userid*/
    public static int readUserId(Intent intent) {
        return intent.getIntExtra(EXTRA_USER_ID, -1);
    }
}