package com.example.dogtraininglog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dogtraininglog.database.DogRepository;
import com.example.dogtraininglog.database.entities.Dog;

public class AddDogActivity extends AppCompatActivity{
    private int userId;
    private EditText etDogName, etDogAge, etDogOwner, etDogNotes;
    private Button btnSave;
    private DogRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        userId = getIntent().getIntExtra(SelectDogActivity.EXTRA_USER_ID, -1);
        if (userId <= 0) { finish(); return; }

        repo = new DogRepository(getApplication());

        etDogName  = findViewById(R.id.etDogName);
        etDogAge   = findViewById(R.id.etDogAge);
        etDogNotes = findViewById(R.id.etDogNotes);
        etDogOwner = findViewById(R.id.etDogOwner);
        btnSave    = findViewById(R.id.btnSaveDog);

        if (etDogName == null || etDogAge == null || etDogOwner == null || etDogNotes == null || btnSave == null) {
            throw new IllegalStateException("activity_add_dog.xml must expose etDogName, etDogAge, etDogOwner, etDogNotes, btnSaveDog");
        }

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

            android.util.Log.d("DogRepo", "Attempt insert userId=" + userId + " name=" + name + " age=" + age);
            repo.insert(d);

            setResult(RESULT_OK, new Intent());
            finish();
        });
    }
}