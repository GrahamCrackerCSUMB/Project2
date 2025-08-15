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

/*Repositories centralize threading. This means you can't block the main thread.*/
public class DogRepository {

/*The interface room genreates implementations for*/
    private final DogDAO dogDAO;

    /*Fetch the single (only one database) instance and get the DAO*/
    public DogRepository(Context context) {
        dogDAO = DogTrainingDatabase.getDatabase(context).dogDAO();
    }

    /*Live data automatically updates the UI when the table changes.
    *
    * Return ALL dogs */

    public LiveData<List<Dog>> getAllDogs() {
        return dogDAO.getAllDogs();
    }

    /*Get dogs by trainer*/
    public LiveData<List<Dog>> getDogsForUser(int userId) {
        return dogDAO.getDogsForUser(userId);
    }

    /*Get dog names by trainer*/
    public LiveData<List<String>> getDogNamesForUser(int userId) {
        return dogDAO.getDogNamesForUser(userId);
    }

    /*Get dog count by trainer*/
    public LiveData<Integer> countDogsForUser(int userId) {
        return dogDAO.countDogsForUser(userId);
    }

    /*Add a dog*/
    public void insert(Dog dog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> dogDAO.insert(dog));
    }

    /*Update a dog*/
    public void update(Dog dog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> dogDAO.update(dog));
    }

    /*Delete a dog*/
    public void delete(Dog dog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> dogDAO.delete(dog));
    }


}
