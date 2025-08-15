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

    private RecyclerView recyclerView;
    private DogLogAdapter adapter;
    private List<DogLog> allLogs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityViewLogsBinding binding = ActivityViewLogsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.logsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    private void filterLogs(String query) {
        List<DogLog> filtered = new ArrayList<>();
        for (DogLog log : allLogs) {
            if (log.getActivity().toLowerCase().contains(query.toLowerCase()) ||
                    log.getDate().toString().contains(query)) {
                filtered.add(log);
            }
        }
        adapter.updateList(filtered);
    }

    }


