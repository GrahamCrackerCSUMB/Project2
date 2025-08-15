package com.example.dogtraininglog.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dogtraininglog.database.entities.Dog;

import java.util.List;

@Dao
public interface DogDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Dog dog);

    @Update
    int update(Dog dog);

    @Delete
    int delete(Dog dog);

    @Query("SELECT * FROM dog WHERE id = :id LIMIT 1")
    LiveData<Dog> getDogById(int id);

    @Query("SELECT * FROM dog WHERE userId = :userId ORDER BY name")
    LiveData<List<Dog>> getDogsForUser(int userId);

    @Query("SELECT name FROM dog WHERE userId = :userId ORDER BY name")
    LiveData<List<String>> getDogNamesForUser(int userId);

    @Query("SELECT COUNT(*) FROM dog WHERE userId = :userId")
    LiveData<Integer> countDogsForUser(int userId);

    @Query("SELECT * FROM dog ORDER BY name")
    LiveData<List<Dog>> getAllDogs();
}
