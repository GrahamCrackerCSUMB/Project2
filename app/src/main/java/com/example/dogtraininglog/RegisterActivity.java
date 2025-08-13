package com.example.dogtraininglog;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.entities.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEt, passwordEt;
    private Button createBtn;
    private DogTrainingLogRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEt = findViewById(R.id.registerUsername);
        passwordEt = findViewById(R.id.registerPassword);
        createBtn = findViewById(R.id.createAccountButton);

        repo = DogTrainingLogRepository.getRepository(getApplication());

        createBtn.setOnClickListener(v -> attemptCreate());
    }

    private void attemptCreate() {
        String u = usernameEt.getText().toString().trim();
        String p = passwordEt.getText().toString();

        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Both fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        createBtn.setEnabled(false);

        DogTrainingLogRepository.databaseWriteExecutor.execute(() -> {
            User existing = repo.getUserByUsernameSync(u);
            repo.insertUser(new User(u, p));

            runOnUiThread(() -> {
                createBtn.setEnabled(true);
                Toast.makeText(
                        RegisterActivity.this,
                        (existing != null) ? "Replaced existing account" : "Account created!",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            });
        });
    }
}

