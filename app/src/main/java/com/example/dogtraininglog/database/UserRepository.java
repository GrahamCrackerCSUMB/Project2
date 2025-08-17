package com.example.dogtraininglog.database;

import android.app.Application;

import androidx.lifecycle.LiveData;


import com.example.dogtraininglog.database.entities.User;

public class UserRepository {
    private static UserRepository repository;

    private final UserDAO userDAO;

    /*Get singleton room database instance, then get the DAO*/
    private UserRepository(Application application) {
        DogTrainingDatabase db = DogTrainingDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
    }

    public static UserRepository getRepository(Application application) {
        if (repository != null) return repository;
        synchronized (UserRepository.class) {
            if (repository == null) {
                repository = new UserRepository(application);
            }
        }
        return repository;
    }

    /*Get user by id*/
    public LiveData<User> getUserByIdLive(int id) {
        return userDAO.getUserByIdLive(id);
    }
}