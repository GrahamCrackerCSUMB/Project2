package com.example.dogtraininglog.viewholders;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.entities.DogLog;

import java.util.List;

public class DogTrainingViewModel extends AndroidViewModel {
    private final DogTrainingLogRepository repository;

    public DogTrainingViewModel(Application application){
        super(application);
        repository = DogTrainingLogRepository.getRepository(application);
    }

    public LiveData<List<DogLog>> getAllLogsById(int userId) {
        return repository.getAllLogsByUserIdLiveData(userId);
    }

    public LiveData<List<DogLog>> getAllLogs() {   // LiveData version
        return repository.getAllLogsLive();
    }

    public void insert(DogLog log){
        repository.insertGymLog(log);
    }


}

