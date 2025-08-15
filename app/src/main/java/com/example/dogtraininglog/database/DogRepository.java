package com.example.dogtraininglog.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dogtraininglog.MainActivity;
import com.example.dogtraininglog.database.entities.User;
import com.example.dogtraininglog.database.entities.Dog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
public class DogRepository {


    private final DogDAO dogDAO;

    public DogRepository(Context context) {
        dogDAO = DogTrainingDatabase.getDatabase(context).dogDAO();
    }

    public LiveData<List<Dog>> getAllDogs() {
        return dogDAO.getAllDogs();
    }

    public LiveData<List<Dog>> getDogsForUser(int userId) {
        return dogDAO.getDogsForUser(userId);
    }

    public LiveData<List<String>> getDogNamesForUser(int userId) {
        return dogDAO.getDogNamesForUser(userId);
    }

    public LiveData<Integer> countDogsForUser(int userId) {
        return dogDAO.countDogsForUser(userId);
    }

    public void insert(Dog dog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> dogDAO.insert(dog));
    }

    public void update(Dog dog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> dogDAO.update(dog));
    }

    public void delete(Dog dog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> dogDAO.delete(dog));
    }


}
