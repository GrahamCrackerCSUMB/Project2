package com.example.dogtraininglog.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.dogtraininglog.database.DogTrainingDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity(tableName = DogTrainingDatabase.DOG_LOG_TABLE)
public class DogLog {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String activity;

    private int reps;

    private boolean successful;
    private LocalDateTime date;
    private int userId;

    public DogLog(String activity, int reps, boolean successful, int userId) {
        this.activity = activity;
        this.reps = reps;
        this.successful = successful;
        this.userId = userId;
        date = LocalDateTime.now();
    }

    public DogLog(){}

    @NonNull
    @Override
    public String toString() {
        return activity + '\n' +
                "Repetitions: " + reps + '\n' +
                "Sucessful? " + successful + '\n' +
                "Date: " + date.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")) +'\n' +
                "=-=-=-=-=-=-=-=-=-=\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DogLog dogLog = (DogLog) o;
        return reps == dogLog.reps && successful == dogLog.successful && userId == dogLog.userId && Objects.equals(id, dogLog.id) && Objects.equals(activity, dogLog.activity) && Objects.equals(date, dogLog.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, activity, reps, successful, date, userId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
