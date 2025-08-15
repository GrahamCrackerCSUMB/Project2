package com.example.dogtraininglog.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
/* This is a Data Access Object for the dog training table
*
* Room needs a DEO to read and write to the table
*
* This file has our database operations.
*
* It is important to use LiveData bc queries will run on background threads.*/
@Dao
public interface DogTrainingLogDAO {

    /*New log entry*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (DogLog dogTrainingLog);

    /*Get ALL logs (for admins)*/
    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " ORDER BY date DESC")
    List<DogLog> getAllLogs();

    /*Get ALL logs (for admins) - this might be a duplicate*/
    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " ORDER BY date DESC" )
    List<DogLog> getAllRecords();

    /*Get ALL logs (for admins) - this might be a duplicate*/
    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " ORDER BY date DESC")
    LiveData<List<DogLog>> getAllLogsLive();

    /*Get all logs by trainer*/
    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<DogLog>> getAllLogsByUserId(int userId);

    /*get all logs bu logged in trainer*/
    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    List<DogLog> getRecordableUserId(int loggedInUserId);

    /*Duplicate of above but livedata?*/
    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    LiveData<List<DogLog>> getRecordsetUserIdLiveData(int loggedInUserId);
}
