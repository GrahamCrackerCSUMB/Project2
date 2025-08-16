package com.example.dogtraininglog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.OnBackPressedCallback; // <-- add at top with other imports


import com.example.dogtraininglog.database.DogRepository;
import com.example.dogtraininglog.database.entities.Dog;
import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.DogLog;
import com.example.dogtraininglog.database.entities.User;
import com.example.dogtraininglog.databinding.ActivityMainBinding;
import com.example.dogtraininglog.viewholders.DogTrainingLogAdapter;
import com.example.dogtraininglog.viewholders.DogTrainingViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /*Constants*/

    private static final String MAIN_ACTIVITY_USER_ID = "com.example.dogtraininglog.MAIN_ACTIVITY_USER_ID";

    static final String SHARED_PREFERENCE_USERID_KEY = "com.example.dogtraininglog.SHARED_PREFERENCE_USERID_KEY";

    static final String SHARED_PREFERENCE_USERID_VALUE = "com.example.dogtraininglog.SHARED_PREFERENCE_USERID_VALUE";

    static final String SHARED_PREFERENCE_STATE_USERID_KEY = "com.example.dogtraininglog.SHARED_PREFERENCE_STATE_USERID_KEY";

    static final String SAVED_INSTANCE_STATE_USERID_KEY = "com.example.dogtraininglog.SAVED_INSTANCE_STATE_USERID_KEY";


    private static final int LOGGED_OUT = -1;

    private ActivityMainBinding binding;

    private DogTrainingLogRepository repository;

    private DogTrainingViewModel dogTrainingViewModel;



    public static final String TAG = "DAC_DOGLOG";
    String mActivity = "";
    int mReps = 0;

    boolean mSuccessful = false;

    private int loggedInUserId = -1;
    private int dogId = -1;
    private String selectedDogName = null;
    private User user;

    private final ArrayList<DogLog> currentLogs = new ArrayList<>();

/*Sets up views, listeners, and initial state.*/

    /*Inflate binding and set content view*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        dogId   = getIntent().getIntExtra(SelectDogActivity.EXTRA_DOG_ID, -1);
        selectedDogName = getIntent().getStringExtra(SelectDogActivity.EXTRA_DOG_NAME);
        applyDogHeader();

        dogId = getIntent().getIntExtra(SelectDogActivity.EXTRA_DOG_ID, -1);


        dogId = getIntent().getIntExtra(SelectDogActivity.EXTRA_DOG_ID, -1);

        dogTrainingViewModel = new ViewModelProvider(this).get(DogTrainingViewModel.class);

        DogRepository dogRepo = new DogRepository(getApplication());

        if (dogId > 0) {
            dogRepo.getDogByIdLive(dogId).observe(this, (Dog dog) -> {
                if (dog != null) {
                    binding.tvUserNameTop.setText(dog.getName().toUpperCase(java.util.Locale.ROOT));
                }
            });
        } else {
            binding.tvUserNameTop.setText("");
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(SelectDogActivity.makeIntent(MainActivity.this, loggedInUserId));
                finish();
            }
        });

        /*Wire up recycler view - this displays the training logs*/
        RecyclerView recyclerView = binding.logDisplayRecyclerView;
        final DogTrainingLogAdapter adapter = new DogTrainingLogAdapter(new DogTrainingLogAdapter.GymLogDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*Get the repository*/
        repository = DogTrainingLogRepository.getRepository(getApplication());
        loginUser(savedInstanceState);


        //User is not logged in at this point, go to login screen
        if(loggedInUserId == -1){
            startActivity(LoginActivity.makeIntent(MainActivity.this));
        }
        updateSharedPreference();


        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationFromDisplay();
                insertDogLogRecord();
            }
        });

        /*Go back to previous screen*/
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void applyDogHeader() {
        if (binding != null && binding.tvUserNameTop != null) {
            if (selectedDogName != null && !selectedDogName.isEmpty()) {
                binding.tvUserNameTop.setText(selectedDogName.toUpperCase(java.util.Locale.ROOT));
            } else {
                binding.tvUserNameTop.setText("");
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        dogId   = intent.getIntExtra(SelectDogActivity.EXTRA_DOG_ID, -1);
        selectedDogName = intent.getStringExtra(SelectDogActivity.EXTRA_DOG_NAME);
        applyDogHeader();
    }


    @Override
    protected void onResume() {
        super.onResume();
        applyDogHeader();
    }

        private void loginUser(Bundle savedInstanceState) {
        final int LOGGED_OUT = -1;

        /*Persistance - if you are logged in you stay logged in*/
        SharedPreferences sp = getApplicationContext()
                .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int prefId   = sp.getInt(getString(R.string.preference_userId_key), LOGGED_OUT);
        int intentId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        int savedId  = (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY))
                ? savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT)
                : LOGGED_OUT;

        /*Look first at save state, then intent, then finally shared preferenes*/
        if (savedId != LOGGED_OUT) {
            loggedInUserId = savedId;
        } else if (intentId != LOGGED_OUT) {
            loggedInUserId = intentId;
        } else {
            loggedInUserId = prefId;
        }

        /*If we have a user id, log the user and look at their logs.*/
        if (loggedInUserId == LOGGED_OUT) return;

        sp.edit().putInt(getString(R.string.preference_userId_key), loggedInUserId).apply();

        LiveData<User> userLive = repository.getUserByUserId(loggedInUserId);
        userLive.observe(this, new Observer<User>() {
            @Override public void onChanged(User u) {
                MainActivity.this.user = u;
                if (u == null) return;


                /*Display who the trainer is*/
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setSubtitle(u.getUsername().toUpperCase(Locale.ROOT));
                }


                /*If admin display admin view*/

                final int selectedDogId = getIntent().getIntExtra(SelectDogActivity.EXTRA_DOG_ID, -1);


                if (getSupportActionBar() != null) {
                    if (u.isAdmin() && selectedDogId <= 0) {
                        getSupportActionBar().setSubtitle("ADMIN VIEW");
                    } else {
                        getSupportActionBar().setSubtitle(u.getUsername().toUpperCase(java.util.Locale.ROOT));
                    }
                }

                androidx.lifecycle.LiveData<java.util.List<com.example.dogtraininglog.database.DogLog>> stream;

                if (selectedDogId > 0) {
                    stream = repository.getLogsForDog(loggedInUserId, selectedDogId);
                } else if (u.isAdmin()) {
                    stream = dogTrainingViewModel.getAllLogs();
                } else {
                    stream = dogTrainingViewModel.getAllLogsById(loggedInUserId);
                }

                stream.observe(MainActivity.this, logList -> {
                    currentLogs.clear();
                    if (logList != null) currentLogs.addAll(logList);
                    DogTrainingLogAdapter adapter =
                            (DogTrainingLogAdapter) binding.logDisplayRecyclerView.getAdapter();
                    if (adapter != null) adapter.submitList(new java.util.ArrayList<>(currentLogs));
                });

                invalidateOptionsMenu();
            }
        });
    }

    /*Make the current user id persistant*/
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_USERID_KEY, loggedInUserId);
        updateSharedPreference();
    }

    /*Inflate overflow menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.logout_menu,menu);
        return true;
    }

    /*Prepare menu to have the username and add a click listener so user an log out */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem);
        item.setVisible(true);
        if(user==null){
            return false;
        }
        /*Add user name to top*/
        item.setTitle("LOG OUT");

        /*Log out when clicked*/
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {

                showLogoutDialog();
                return false;
            }
        });
        return true;
    }

    /*Show dialouge before logging out*/
    private void showLogoutDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alertDialog = alertBuilder.create();

        alertBuilder.setMessage("Logout?");

        alertBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertBuilder.create().show();
    }

    /*Log out and clear task*/
    private void logout() {
        loggedInUserId = -1;

        getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                .edit()
                .remove(getString(R.string.preference_userId_key))  // or .putInt(..., -1)
                .apply();

        startActivity(LoginActivity.makeIntent(MainActivity.this));
        finishAffinity();
    }

    /*Logged in user id to shared preferences for persistance purposes.*/
    private void updateSharedPreference(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(getString(R.string.preference_userId_key), loggedInUserId);
        sharedPrefEditor.apply();}

    /*Intent - opens mainactivity*/
    static Intent mainActivityIntentFactory(Context context, int userId, int dogId, String dogName){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        intent.putExtra(SelectDogActivity.EXTRA_DOG_ID,  dogId);
        intent.putExtra(SelectDogActivity.EXTRA_DOG_NAME, dogName);
        return intent;
    }

    /*Inserts DogLog data*/
    private void insertDogLogRecord(){
        if (mActivity.isEmpty()) return;
        if (dogId <= 0) {
            android.widget.Toast.makeText(this, "No dog selected.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        DogLog log = new DogLog(mActivity, mReps, mSuccessful, loggedInUserId, dogId);
        repository.insertDogLog(log);
    }



    //click listener for logout button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutMenuItem) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        updateSharedPreference();

        Intent sel = SelectDogActivity.makeIntent(MainActivity.this, loggedInUserId);
        sel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(sel);
        finish();
    }

    /*Reads UI field to help construct doglog*/
    private void getInformationFromDisplay(){
        mActivity = binding.trainingInputEditText.getText().toString();

        try{
            mReps = Integer.parseInt(binding.repetitionsInputEditText.getText().toString());
        } catch (NumberFormatException e){
            Log.d(TAG, "Error reading value from reps edit text.");
        }

        mSuccessful = binding.successCheckbox.isChecked();
    }
}
