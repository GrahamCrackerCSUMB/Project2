package com.example.dogtraininglog.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dogtraininglog.MainActivity;
import com.example.dogtraininglog.database.entities.DogLog;
import com.example.dogtraininglog.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DogTrainingLogRepository {

    private final DogTrainingLogDAO dogTrainingLogDAO;
    private final UserDAO userDAO;

    private ArrayList<DogLog> allLogs;

    private static DogTrainingLogRepository repository;

    private DogTrainingLogRepository(Application application){
        DogTrainingDatabase db = DogTrainingDatabase.getDatabase(application);
        this.dogTrainingLogDAO = db.dogLogDAO();
        this.userDAO = db.userDAO();
        this.allLogs = (ArrayList<DogLog>) this.dogTrainingLogDAO.getAllRecords();
    }

    public static DogTrainingLogRepository getRepository(Application application){
        if (repository != null){
            return repository;
        }

        Future<DogTrainingLogRepository> future = DogTrainingDatabase.databaseWriteExecutor.submit(
                new Callable<DogTrainingLogRepository>() {
                    @Override
                    public DogTrainingLogRepository call() throws Exception {
                        return new DogTrainingLogRepository(application);
                    }
                }
        );
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e){
            Log.e(MainActivity.TAG, "Problem getting GymLogRepository, thread error.", e);
        }
        return null;
    }

    public ArrayList<DogLog> getAllLogs() {
        Future<ArrayList<DogLog>> future = DogTrainingDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<DogLog>>() {
                    @Override
                    public ArrayList<DogLog> call() throws Exception {
                        return (ArrayList<DogLog>) dogTrainingLogDAO.getAllRecords();
                    }
                }
        );
        try{
            return future.get();
        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
            Log.i(MainActivity.TAG,"Problem when getting all GymLogs in the repository");
        }
        return null;
    }

    public void insertGymLog(DogLog dogTrainingLog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() ->
        {
            dogTrainingLogDAO.insert(dogTrainingLog);
        });
    }

    public void insertUser(User... user) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() -> {
            userDAO.insert(user);
        });
    }

    public LiveData<User> getUserByUserName(String username) {
        return userDAO.getUserByUserName(username);
    }

    public LiveData<User> getUserByUserId(int userId) {
        return userDAO.getUserByUserId(userId);
    }

    public LiveData<List<DogLog>>getAllLogsByUserIdLiveData(int loggedInUserId){
        return dogTrainingLogDAO.getRecordsetUserIdLiveData(loggedInUserId);
    }

    @Deprecated
    public ArrayList<DogLog> getAllLogsByUserId(int loggedInUserId) {
        Future<ArrayList<DogLog>> future = DogTrainingDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<DogLog>>() {
                    @Override
                    public ArrayList<DogLog> call() throws Exception {
                        return (ArrayList<DogLog>) dogTrainingLogDAO.getRecordableUserId(loggedInUserId);
                    }
                }
        );
        try{
            return future.get();
        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
            Log.i(MainActivity.TAG,"Problem when getting all GymLogs in the repository");
        }
        return null;
    }
    }

