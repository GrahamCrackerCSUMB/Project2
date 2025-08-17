package com.example.dogtraininglog.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dogtraininglog.MainActivity;
import com.example.dogtraininglog.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/*This class creates an API to access data. It also centralizes threading so writes
* go on a background thread. Viewmodel depends on this.*/
public class DogTrainingLogRepository {

    private final DogTrainingLogDAO dogTrainingLogDAO;
    private final UserDAO userDAO;

    private LiveData<List<DogLog>> allLogs;

    private static DogTrainingLogRepository repository;

    /*Get singleton room database instance, then get the DAO*/

    private DogTrainingLogRepository(Application application){
        DogTrainingDatabase db = DogTrainingDatabase.getDatabase(application);
        this.dogTrainingLogDAO = db.dogTrainingLogDAO();
        this.userDAO = db.userDAO();
        this.allLogs = this.dogTrainingLogDAO.getAllRecords();
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

    /*Get all the logs*/
    public LiveData<List<DogLog>> getAllLogs() {
        Future<LiveData<List<DogLog>>> future = DogTrainingDatabase.databaseWriteExecutor.submit(
                new Callable<LiveData<List<DogLog>>>() {
                    @Override
                    public LiveData<List<DogLog>> call() throws Exception {
                        return dogTrainingLogDAO.getAllRecords();
                    }
                }
        );
        try{
            return future.get();
        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
            Log.i(MainActivity.TAG,"Problem when getting all logs in the repository");
        }
        return null;
    }

    /*Insert a log*/
    public void insertDogLog(DogLog dogTrainingLog) {
        DogTrainingDatabase.databaseWriteExecutor.execute(() ->
        {
            dogTrainingLogDAO.insert(dogTrainingLog);
        });
    }

    /*Insert a user*/
    public void insertUser(User... user) {
            userDAO.insert(user);
    }

    /*Get logs for the dog*/
    public LiveData<List<DogLog>> getLogsForDog(int userId, int dogId) {
        return dogTrainingLogDAO.getLogsForDog(userId, dogId);
    }

    /*get all of the logs*/
    public LiveData<List<DogLog>> getAllLogsLive() {
        return dogTrainingLogDAO.getAllLogsLive();
    }

    /*Get user by username*/
    public LiveData<User> getUserByUserName(String username) {
        return userDAO.getUserByUserName(username);
    }

    /*Get user by id*/
    public LiveData<User> getUserByUserId(int userId) {
        return userDAO.getUserByUserId(userId);
    }

    /*Get all logs by a spefic user*/
    public LiveData<List<DogLog>>getAllLogsByUserIdLiveData(int loggedInUserId){
        return dogTrainingLogDAO.getRecordsetUserIdLiveData(loggedInUserId);
    }

    /*Still works. get all logs by user id but better to use live data above*/
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

