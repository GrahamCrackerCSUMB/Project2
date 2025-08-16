package com.example.dogtraininglog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.entities.User;


/*Creates new user accounts*/
public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEt, passwordEt;
    private Button createBtn;
    private DogTrainingLogRepository repo;

    public static Intent makeIntent(Context ctx) {
        return new Intent(ctx, RegisterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Inflate layout so you can see registration screen*/
        setContentView(R.layout.activity_register);

        Button back = findViewById(R.id.btnBackToLogin);
        back.setOnClickListener(v -> {
            startActivity(LoginActivity.makeIntent(RegisterActivity.this));
            finish();
        });

        /*Bind views*/
        usernameEt = findViewById(R.id.registerUsername);
        passwordEt = findViewById(R.id.registerPassword);
        createBtn  = findViewById(R.id.createAccountButton);

        repo = DogTrainingLogRepository.getRepository(getApplication());

        /*When you click button, attempt to create a new user*/
        createBtn.setOnClickListener(v -> attemptCreate());

        back.setOnClickListener(v -> {
            startActivity(LoginActivity.makeIntent(RegisterActivity.this));
            finish();
        });
    }

    private void attemptCreate() {
        /*format the user entered username and password*/
        String u = usernameEt.getText().toString().trim();
        String p = passwordEt.getText().toString();

        /*If either field is empty, tell user*/
        if (u.isEmpty() || p.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Both fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        /*Stop button temporaily so we don't accidentally create duplicates*/
        createBtn.setEnabled(false);

        new Thread(() -> {
            /*Insert user record*/
            repo.insertUser(new User(u, p));

            /*We need to be on main UI*/
            runOnUiThread(() -> {
                /*Make button work again*/
                createBtn.setEnabled(true);
                /*Tell user success*/
                Toast.makeText(RegisterActivity.this, "Account saved.", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
