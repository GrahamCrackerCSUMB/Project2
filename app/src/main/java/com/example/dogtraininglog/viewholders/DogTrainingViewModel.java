package com.example.dogtraininglog.viewholders;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dogtraininglog.database.DogTrainingLogRepository;
import com.example.dogtraininglog.database.DogLog;

import java.util.List;

/*Give app data to the UI*/
public class DogTrainingViewModel extends AndroidViewModel {

    private final DogTrainingLogRepository repository;

    public DogTrainingViewModel(Application application){
        super(application);
        repository = DogTrainingLogRepository.getRepository(application);
    }

    /*Live list of logs by trainer*/
    public LiveData<List<DogLog>> getAllLogsById(int userId) {
        return repository.getAllLogsByUserIdLiveData(userId);
    }

    /*Get ALL logs - for admins*/
    public LiveData<List<DogLog>> getAllLogs() {   // LiveData version
        return repository.getAllLogsLive();
    }

    /*Insert*/
    public void insert(DogLog log){
        repository.insertDogLog(log);
    }


}

