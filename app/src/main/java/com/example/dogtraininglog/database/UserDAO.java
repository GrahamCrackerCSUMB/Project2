package com.example.dogtraininglog.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dogtraininglog.database.entities.User;

import java.util.List;


/*DAO means data access object
*
* these are sql operations that run on our table*/
@Dao
public interface UserDAO {

    /*Inserts a user row.*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... user);

    @Delete
    void delete(User user);

    @Query(("SELECT * FROM " + DogTrainingDatabase.USER_TABLE + " ORDER BY username"))
    LiveData<List<User>> getAllUsers();

    @Query("DELETE from " + DogTrainingDatabase.USER_TABLE)
    void deleteAll();

    @Query("SELECT * from " + DogTrainingDatabase.USER_TABLE + " WHERE username == :username LIMIT 1")
    LiveData<User> getUserByUserName(String username);

    @Query("SELECT * from " + DogTrainingDatabase.USER_TABLE + " WHERE id == :userId LIMIT 1")
    LiveData<User> getUserByUserId(int userId);

    @Query("SELECT * FROM " + DogTrainingDatabase.USER_TABLE + " WHERE username = :u LIMIT 1")
    User getByUsernameSync(String u);

    @Query("SELECT * FROM usertable WHERE id = :id LIMIT 1")
    LiveData<User> getUserByIdLive(int id);

    @Update(onConflict = OnConflictStrategy.ABORT)
    int update(User user);
}
