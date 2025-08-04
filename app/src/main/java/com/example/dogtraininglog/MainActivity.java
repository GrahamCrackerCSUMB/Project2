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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.entities.DogLog;
import com.example.dogtraininglog.database.entities.User;
import com.example.dogtraininglog.databinding.ActivityMainBinding;
import com.example.dogtraininglog.viewholders.DogTrainingLogAdapter;
import com.example.dogtraininglog.viewholders.DogTrainingViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_USER_ID = "com.example.dogtraininglog.MAIN_ACTIVITY_USER_ID";

    static final String SHARED_PREFERENCE_USERID_KEY = "com.example.dogtraininglog.SHARED_PREFERENCE_USERID_KEY";

    static final String SHARED_PREFERENCE_USERID_VALUE = "com.example.dogtraininglog.SHARED_PREFERENCE_USERID_VALUE";

    static final String SHARED_PREFERENCE_STATE_USERID_KEY = "com.example.dogtraininglog.SHARED_PREFERENCE_STATE_USERID_KEY";

    static final String SAVED_INSTANCE_STATE_USERID_KEY = "com.example.dogtraininglog.SAVED_INSTANCE_STATE_USERID_KEY";


    private static final int LOGGED_OUT = -1;

    private ActivityMainBinding binding;

    private DogTrainingLogRepository repository;

    private DogTrainingViewModel dogTrainingViewModel;



    public static final String TAG = "DAC_GYMLOG";
    String mActivity = "";
    int mReps = 0;

    boolean mSuccessful = false;

    private int loggedInUserId = -1;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dogTrainingViewModel = new ViewModelProvider(this).get(DogTrainingViewModel.class);


        RecyclerView recyclerView = binding.logDisplayRecyclerView;
        final DogTrainingLogAdapter adapter = new DogTrainingLogAdapter(new DogTrainingLogAdapter.GymLogDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = DogTrainingLogRepository.getRepository(getApplication());
        loginUser(savedInstanceState);

        dogTrainingViewModel.getAllLogsById(loggedInUserId).observe(this, gymLogs -> {
            adapter.submitList(gymLogs);
        });

        //User is not logged in at this point, go to login screen
        if(loggedInUserId == -1){
            Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(intent);
        }
        updateSharedPreference();


        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationFromDisplay();
                insertGymLogRecord();
                //        upDateDisplay();
            }
        });

    }

    private void loginUser(Bundle savedInstanceState) {
        //check shared preferencce for logged in user
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        loggedInUserId = sharedPreferences.getInt(getString(R.string.preference_userId_key), LOGGED_OUT);

        if(loggedInUserId == LOGGED_OUT & savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY)){
            loggedInUserId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT);
        }
        if (loggedInUserId == LOGGED_OUT){
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }
        if (loggedInUserId == LOGGED_OUT){
            return;
        }

        LiveData<User> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                this.user = user;
                invalidateOptionsMenu();
            }else{
                //TODO VERIF IF ISSUE?
                // logout();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_USERID_KEY, loggedInUserId);
        updateSharedPreference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem);
        item.setVisible(true);
        if(user==null){
            return false;
        }
        item.setTitle(user.getUsername());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {

                showLogoutDialog();
                return false;
            }
        });
        return true;
    }

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

    private void logout() {

        loggedInUserId = LOGGED_OUT;

        updateSharedPreference();
        getIntent().putExtra(MAIN_ACTIVITY_USER_ID,LOGGED_OUT);

        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
    }

    private void updateSharedPreference(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(getString(R.string.preference_userId_key), loggedInUserId);
        sharedPrefEditor.apply();}

    static Intent mainActivityIntentFactory(Context context, int userId){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }

    private void insertGymLogRecord(){
        if (mActivity.isEmpty()){
            return;
        }
        DogLog log = new DogLog(mActivity, mReps, mSuccessful,loggedInUserId);
        repository.insertGymLog(log);
    }



    @Deprecated
    private void upDateDisplay() {
        ArrayList<DogLog> allLogs = repository.getAllLogsByUserId(loggedInUserId);
        if (allLogs.isEmpty()) {
            //           binding.logDisplayTextView.setText(R.string.nothing_to_show_time_to_hit_the_gym);
        }
        StringBuilder sb = new StringBuilder();
        for (DogLog log : allLogs) {
            sb.append(log);
        }
        //      binding.logDisplayTextView.setText(sb.toString());
    }



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
