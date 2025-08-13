package com.example.dogtraininglog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.entities.User;
import com.example.dogtraininglog.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private DogTrainingLogRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = DogTrainingLogRepository.getRepository(getApplication());

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        Button newUserBtn = findViewById(R.id.newUserButton);
        newUserBtn.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });
    }

    private void verifyUser() {
        String username = binding.userNameLoginEditText.getText().toString();


        if (username.isEmpty()) {
            toastMaker("username should not be blank");
            return;
        }
        LiveData<User> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, user -> {
            if (user != null) {
                String password = binding.passwordLoginEditText.getText().toString();
              if(password.equals(user.getPassword())){
                  startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), user.getId()));
                } else {
                    toastMaker("Invalid password");
                    binding.passwordLoginEditText.setSelection(0);
                }
            } else {
                toastMaker(String.format("%s is not a valid username", username));
                binding.userNameLoginEditText.setSelection(0);
            }
        });
    }

        private void toastMaker (String message){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        static Intent loginIntentFactory (Context context){
            return new Intent(context, LoginActivity.class);
        }

}
