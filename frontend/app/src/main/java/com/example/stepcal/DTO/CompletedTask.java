package com.example.stepcal.DTO;

public class CompletedTask {

    private double calorie_burn;
    private double calorie_intake;

    public CompletedTask() {
    }

    public CompletedTask(double calorie_burn, double calorie_intake) {
        this.calorie_burn = calorie_burn;
        this.calorie_intake = calorie_intake;
    }

    public double getCalorie_burn() {
        return calorie_burn;
    }

    public void setCalorie_burn(double calorie_burn) {
        this.calorie_burn = calorie_burn;
    }

    public double getCalorie_intake() {
        return calorie_intake;
    }

    public void setCalorie_intake(double calorie_intake) {
        this.calorie_intake = calorie_intake;
    }
}