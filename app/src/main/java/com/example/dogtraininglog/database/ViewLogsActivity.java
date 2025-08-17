package com.example.dogtraininglog.database;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import java.util.ArrayList;
import com.example.dogtraininglog.LoginActivity;
import com.example.dogtraininglog.R;
import com.example.dogtraininglog.SelectDogActivity;
import com.example.dogtraininglog.viewholders.DogLogAdapter;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogtraininglog.databinding.ActivityViewLogsBinding;


import java.util.List;

public class ViewLogsActivity extends AppCompatActivity {

    private static final String EXTRA_ADMIN = "EXTRA_ADMIN";
    /*RecylcerView to display the logs in a scrolling list*/
    private RecyclerView recyclerView;

    /*This binds the doglog data to each row*/
    private DogLogAdapter adapter;

    /*List of all logs*/
    private List<DogLog> allLogs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean isAdminView = getIntent().getBooleanExtra(EXTRA_ADMIN, false);
        int userId = getIntent().getIntExtra(SelectDogActivity.EXTRA_USER_ID, -1);
        int dogId  = getIntent().getIntExtra(SelectDogActivity.EXTRA_DOG_ID,  -1);

        if (!isAdminView && (userId <= 0 || dogId <= 0)) {
            finish();
            return;
        }


        /*Inflate recycler view*/
        ActivityViewLogsBinding binding = ActivityViewLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Logout = clear stored userId and return to Login
        binding.btnLogout.setOnClickListener(v -> {
            getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                    .edit()
                    .remove(getString(R.string.preference_userId_key))
                    .apply();

            Intent i = new Intent(ViewLogsActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        if (isAdminView && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("ADMIN · All Logs");
        }

        recyclerView = binding.logsRecyclerView;
        /*Use the layout manager for recylcer view*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*Create the adapter and attach it to recyler view*/
        adapter = new DogLogAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLogs(s.toString().trim());
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        DogTrainingLogRepository repo = DogTrainingLogRepository.getRepository(getApplication());

        if (isAdminView) {
            /*Admins get to see everything*/
            repo.getAllLogsLive().observe(this, logs -> {
                allLogs = (logs != null) ? logs : new ArrayList<>();
                adapter.updateList(allLogs);
            });
        } else {
            /*regular users do not*/
            repo.getLogsForDog(userId, dogId).observe(this, logs -> {
                allLogs = (logs != null) ? logs : new ArrayList<>();
                adapter.updateList(allLogs);
            });
        }
    }

    /*Search by activity or date*/
    private void filterLogs(String query) {
        List<DogLog> filtered = new ArrayList<>();
        for (DogLog log : allLogs) {
            /*Make everything lower case to search*/
            if (log.getActivity().toLowerCase().contains(query.toLowerCase()) ||
                    log.getDate().toString().contains(query)) {
                filtered.add(log);
            }
        }
        /*Swap the data*/
        adapter.updateList(filtered);
    }

    }


