package com.example.stepcal.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Task {

    @SerializedName("target_Calorie")
    private double target_Calorie;

    @SerializedName("exercises")
    private Map<String, Double> Exercises;

    public Task(double target_Calorie, Map<String, Double> exercises) {
        this.target_Calorie = target_Calorie;
        this.Exercises = exercises;
    }

    public Task() {
    }

    public double getTarget_Calorie() {
        return target_Calorie;
    }

    public void setTarget_Calorie(double target_Calorie) {
        this.target_Calorie = target_Calorie;
    }

    public Map<String, Double> getExercises() {
        return Exercises;
    }

    public void setExercises(Map<String, Double> exercises) {
        this.Exercises = exercises;
    }

    @Override
    public String toString() {
        return "Task{" +
                "target_Calorie=" + target_Calorie +
                ", Exercises=" + Exercises +
                '}';
    }
}