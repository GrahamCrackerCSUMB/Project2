package com.example.dogtraininglog.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.dogtraininglog.database.DogTrainingDatabase;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = DogTrainingDatabase.DOG_LOG_TABLE)
public class DogLog {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String exercise;
    private double weight;
    private int reps;
    private LocalDateTime date;
    private int userId;

    public DogLog(String exercise, double weight, int reps, int userId) {
        this.exercise = exercise;
        this.weight = weight;
        this.reps = reps;
        this.userId = userId;
        date = LocalDateTime.now();
    }

    @NonNull
    @Override
    public String toString() {
        return exercise + '\n' +
                "weight: " + weight + '\n' +
                "reps: " + reps + '\n' +
                "date: " + date.toString() +'\n' +
                "=-=-=-=-=-=-=-=-=-=\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DogLog dogLog = (DogLog) o;
        return Double.compare(weight, dogLog.weight) == 0 && reps == dogLog.reps && userId == dogLog.userId && Objects.equals(id, dogLog.id) && Objects.equals(exercise, dogLog.exercise) && Objects.equals(date, dogLog.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exercise, weight, reps, date, userId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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
}
