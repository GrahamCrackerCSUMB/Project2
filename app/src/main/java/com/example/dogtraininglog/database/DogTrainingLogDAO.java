package com.example.dogtraininglog.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dogtraininglog.database.entities.DogLog;

import java.util.List;

@Dao
public interface DogTrainingLogDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (DogLog dogTrainingLog);

    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " ORDER BY date DESC" )
    List<DogLog> getAllRecords();

    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<DogLog>> getAllLogsByUserId(int userId);

    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    List<DogLog> getRecordableUserId(int loggedInUserId);

    @Query("SELECT * FROM " + DogTrainingDatabase.DOG_LOG_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    LiveData<List<DogLog>> getRecordsetUserIdLiveData(int loggedInUserId);
}
