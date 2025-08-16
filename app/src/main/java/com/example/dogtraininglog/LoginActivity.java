package com.example.dogtraininglog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public static final String EXTRA_USER_ID = "com.example.dogtraininglog.extra.USER_ID";
    public static final String EXTRA_USERNAME = "com.example.dogtraininglog.extra.USERNAME";


    public static Intent makeIntent(Context ctx) {
        Intent i = new Intent(ctx, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return i;
    }


    /*Sets up view, listners, and inital state here.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*Inflate the XML*/
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = DogTrainingLogRepository.getRepository(getApplication());

        /*What happens when we press the button*/
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        Button newUserBtn = findViewById(R.id.newUserButton);
        newUserBtn.setOnClickListener(v -> {
            startActivity(RegisterActivity.makeIntent(LoginActivity.this));
        });
    }

    /*See if username and password match stored*/
    private void verifyUser() {
        /*User enters in username*/
        String username = binding.userNameLoginEditText.getText().toString();

        /*What to do if the username is blank*/
        if (username.isEmpty()) {
            toastMaker("username should not be blank");
            return;
        }

        /*go look in our database by username*/
        LiveData<User> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, user -> {
            /*if username is not empty get the password they entered in*/
            if (user != null) {
                String password = binding.passwordLoginEditText.getText().toString();
                /* if it is a match*/
              if(password.equals(user.getPassword())){
                  SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
                  sp.edit().putInt(getString(R.string.preference_userId_key), user.getId()).apply();



                  /*start select dog activity*/
                  startActivity(SelectDogActivity.selectDogIntentFactory(
                          getApplicationContext(),
                          user.getId()
                  ));
                  finish();
                } else {
                  /*Bad password*/
                    toastMaker("Invalid password");
                    binding.passwordLoginEditText.requestFocus();
                }
            } else {
                /*Tell user if they have a bad username*/
                toastMaker(String.format("%s is not a valid username", username));
                binding.userNameLoginEditText.requestFocus();
                binding.userNameLoginEditText.setSelection(0);
            }
        });
    }


        /*Shows short message to user*/
        private void toastMaker (String message){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        /*Intent factory*/
        static Intent loginIntentFactory (Context context){
            return new Intent(context, LoginActivity.class);
        }



}
