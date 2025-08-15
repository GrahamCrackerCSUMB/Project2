package com.example.dogtraininglog.database;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import java.util.ArrayList;

import com.example.dogtraininglog.viewholders.DogLogAdapter;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogtraininglog.databinding.ActivityViewLogsBinding;

import java.util.List;

public class ViewLogsActivity extends AppCompatActivity {

    /*RecylcerView to display the logs in a scrolling list*/
    private RecyclerView recyclerView;

    /*This binds the doglog data to each row*/
    private DogLogAdapter adapter;

    /*List of all logs*/
    private List<DogLog> allLogs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Inflate recycler view*/
        ActivityViewLogsBinding binding = ActivityViewLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = binding.logsRecyclerView;
        /*Use the layout manager for recylcer view*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*Create the adapter and attach it to recyler view*/
        adapter = new DogLogAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        DogTrainingLogRepository repo = DogTrainingLogRepository.getRepository(getApplication());
        repo.getAllLogsLive().observe(this, logs -> {
            allLogs = (logs != null) ? logs : new ArrayList<>();
            adapter.updateList(allLogs);
        });

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLogs(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
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


