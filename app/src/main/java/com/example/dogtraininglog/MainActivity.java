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


import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.entities.DogLog;
import com.example.dogtraininglog.database.entities.User;
import com.example.dogtraininglog.databinding.ActivityMainBinding;
import com.example.dogtraininglog.viewholders.DogTrainingLogAdapter;
import com.example.dogtraininglog.viewholders.DogTrainingViewModel;

import java.util.ArrayList;
import java.util.List;

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

    private final ArrayList<DogLog> currentLogs = new ArrayList<>();


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
                insertDogLogRecord();
            }
        });

    }

    private void loginUser(Bundle savedInstanceState) {
        final int LOGGED_OUT = -1;

        SharedPreferences sp = getApplicationContext()
                .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int prefId   = sp.getInt(getString(R.string.preference_userId_key), LOGGED_OUT);
        int intentId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        int savedId  = (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY))
                ? savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT)
                : LOGGED_OUT;

        if (savedId != LOGGED_OUT) {
            loggedInUserId = savedId;
        } else if (intentId != LOGGED_OUT) {
            loggedInUserId = intentId;
        } else {
            loggedInUserId = prefId;
        }

        if (loggedInUserId == LOGGED_OUT) return;

        sp.edit().putInt(getString(R.string.preference_userId_key), loggedInUserId).apply();

        LiveData<User> userLive = repository.getUserByUserId(loggedInUserId);
        userLive.observe(this, new Observer<User>() {
            @Override public void onChanged(User u) {
                MainActivity.this.user = u;
                if (u == null) return;

                if (u.isAdmin()) {
                    dogTrainingViewModel.getAllLogs().observe(MainActivity.this,
                            new Observer<List<DogLog>>() {
                                @Override public void onChanged(List<DogLog> logList) {
                                    currentLogs.clear();
                                    if (logList != null) currentLogs.addAll(logList);
                                    DogTrainingLogAdapter adapter =
                                            (DogTrainingLogAdapter) binding.logDisplayRecyclerView.getAdapter();
                                    if (adapter != null) adapter.submitList(new ArrayList<>(currentLogs));
                                }
                            });
                } else {
                    dogTrainingViewModel.getAllLogsById(loggedInUserId).observe(MainActivity.this,
                            new Observer<List<DogLog>>() {
                                @Override public void onChanged(List<DogLog> logList) {
                                    currentLogs.clear();
                                    if (logList != null) currentLogs.addAll(logList);
                                    DogTrainingLogAdapter adapter =
                                            (DogTrainingLogAdapter) binding.logDisplayRecyclerView.getAdapter();
                                    if (adapter != null) adapter.submitList(new ArrayList<>(currentLogs));
                                }
                            });
                }

                invalidateOptionsMenu();
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

        loggedInUserId = -1;

        updateSharedPreference();

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
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

    private void insertDogLogRecord(){
        if (mActivity.isEmpty()){
            return;
        }
        DogLog log = new DogLog(mActivity, mReps, mSuccessful,loggedInUserId);
        repository.insertGymLog(log);
    }



    //click listener for logout button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutMenuItem) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Deprecated
    private void upDateDisplay() {
        ArrayList<DogLog> allLogs = repository.getAllLogsByUserId(loggedInUserId);
        if (allLogs.isEmpty()) {
        }
        StringBuilder sb = new StringBuilder();
        for (DogLog log : allLogs) {
            sb.append(log);
        }
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
