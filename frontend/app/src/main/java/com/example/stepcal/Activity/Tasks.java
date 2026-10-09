package com.example.stepcal.Activity;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stepcal.API.ApiInterface;
import com.example.stepcal.DTO.CompletedTask;
import com.example.stepcal.DTO.Task;
import com.example.stepcal.R;
import com.example.stepcal.Retrofit.RetrofitClient;

import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tasks extends BaseActivity {

    private ApiInterface apiInterface;
    private String authToken;

    private TextView targetCalorie;
    private TextView workoutCalories;
    private GridLayout exerciseGrid;
    private TextView completeWorkoutButton;
    private TextView addExerciseButton;

    private TextView foodCaloriesToday;
    private TextView foodCaloriesRemaining;
    private EditText foodCaloriesInput;
    private TextView addFoodButton;

    private double dailyFoodGoal = 0.0;

    private double totalWorkoutCalories = 0.0;
    private double savedWorkoutCalories = 0.0;

    private double foodCaloriesTodayValue = 0.0;

    private boolean workoutCompleted = false;

    private Map<String, Double> availableExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tasks);

        apiInterface =
                RetrofitClient.getRetrofit().create(ApiInterface.class);

        authToken = getSavedToken();

        targetCalorie =
                findViewById(R.id.target);

        workoutCalories =
                findViewById(R.id.workoutCalories);

        exerciseGrid =
                findViewById(R.id.exerciseGrid);

        completeWorkoutButton =
                findViewById(R.id.completeWorkoutButton);

        addExerciseButton =
                findViewById(R.id.addExerciseButton);

        foodCaloriesToday =
                findViewById(R.id.foodCaloriesToday);

        foodCaloriesRemaining =
                findViewById(R.id.foodCaloriesRemaining);

        foodCaloriesInput =
                findViewById(R.id.foodCaloriesInput);

        addFoodButton =
                findViewById(R.id.addFoodButton);

        completeWorkoutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completeWorkout();
                    }
                }
        );

        addExerciseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddExerciseDialog();
                    }
                }
        );

        addFoodButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addFoodCalories();
                    }
                }
        );

        loadTasks();
    }

    private void loadTasks() {

        if (authToken.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        showLoadingExercise();

        apiInterface.getTask(
                "Bearer " + authToken
        ).enqueue(new Callback<Task>() {

            @Override
            public void onResponse(
                    @NonNull Call<Task> call,
                    @NonNull Response<Task> response) {

                if (!response.isSuccessful()
                        || response.body() == null) {

                    showExerciseMessage(
                            "Unable to load today's workout."
                    );

                    Toast.makeText(
                            Tasks.this,
                            "Unable to load today's plan",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                Task task = response.body();

                Log.d(
                        "STEPCAL_TASK_DEBUG",
                        "HTTP CODE: " + response.code()
                );

                Log.d(
                        "STEPCAL_TASK_DEBUG",
                        "TASK: " + task
                );

                Log.d(
                        "STEPCAL_TASK_DEBUG",
                        "CALORIES: "
                                + task.getTarget_Calorie()
                );

                Log.d(
                        "STEPCAL_TASK_DEBUG",
                        "EXERCISES: "
                                + task.getExercises()
                );

                availableExercises =
                        task.getExercises();

                showCalories(task);
                showExercises(task);

                loadTodayCompletedWorkout();
            }

            @Override
            public void onFailure(
                    @NonNull Call<Task> call,
                    @NonNull Throwable t) {

                Log.e(
                        "STEPCAL_TASK_DEBUG",
                        "LOAD TASK FAILED",
                        t
                );

                showExerciseMessage(
                        "Unable to connect to the server."
                );

                Toast.makeText(
                        Tasks.this,
                        "Connection error",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void loadTodayCompletedWorkout() {

        apiInterface.getTodayCompletedTask(
                "Bearer " + authToken
        ).enqueue(new Callback<CompletedTask>() {

            @Override
            public void onResponse(
                    @NonNull Call<CompletedTask> call,
                    @NonNull Response<CompletedTask> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    CompletedTask completed =
                            response.body();

                    savedWorkoutCalories =
                            completed.getCalorie_burn();

                    totalWorkoutCalories =
                            savedWorkoutCalories;

                    foodCaloriesTodayValue =
                            completed.getCalorie_intake();

                    workoutCompleted =
                            savedWorkoutCalories > 0;

                    updateWorkoutCalories();
                    updateFoodDisplay();

                    if (workoutCompleted) {

                        completeWorkoutButton.setText(
                                "✓  Workout Completed"
                        );

                        completeWorkoutButton.setEnabled(
                                false
                        );
                    }

                } else {

                    foodCaloriesTodayValue = 0.0;

                    updateFoodDisplay();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<CompletedTask> call,
                    @NonNull Throwable t) {

                Log.e(
                        "STEPCAL_WORKOUT_DEBUG",
                        "TODAY'S RECORD CHECK FAILED",
                        t
                );

                updateFoodDisplay();
            }
        });
    }

    private void showCalories(Task task) {

        dailyFoodGoal =
                task.getTarget_Calorie();

        targetCalorie.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f kcal",
                        dailyFoodGoal
                )
        );

        updateFoodDisplay();
    }

    private void showExercises(Task task) {

        Map<String, Double> exercises =
                task.getExercises();

        exerciseGrid.removeAllViews();

        if (exercises == null
                || exercises.isEmpty()) {

            showExerciseMessage(
                    "No exercises available right now."
            );

            totalWorkoutCalories =
                    savedWorkoutCalories;

            updateWorkoutCalories();

            return;
        }

        for (Map.Entry<String, Double> entry
                : exercises.entrySet()) {

            addExerciseCard(
                    entry.getKey(),
                    entry.getValue()
            );
        }

        if (savedWorkoutCalories <= 0) {
            totalWorkoutCalories = 0.0;
        }

        updateWorkoutCalories();
    }

    private void addExerciseCard(
            String exerciseName,
            Double calories) {

        TextView card =
                new TextView(this);

        String name =
                formatExerciseName(exerciseName);

        String calorieText =
                calories == null
                        ? "—"
                        : String.format(
                                Locale.getDefault(),
                                "%.2f kcal",
                                calories
                        );

        card.setText(
                name + "\n" + calorieText
        );

        card.setTextColor(
                Color.rgb(65, 48, 70)
        );

        card.setTextSize(
                13
        );

        card.setTypeface(
                Typeface.DEFAULT,
                Typeface.NORMAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(11),
                dp(9),
                dp(11),
                dp(9)
        );

        card.setBackgroundResource(
                R.drawable.capture_info_background
        );

        card.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.WHITE
                )
        );

        card.setElevation(
                dp(2)
        );

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 0;
        params.height = dp(68);

        params.columnSpec =
                GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                );

        params.setMargins(
                dp(4),
                dp(4),
                dp(4),
                dp(4)
        );

        card.setLayoutParams(params);

        exerciseGrid.addView(card);
    }

    private void showLoadingExercise() {

        exerciseGrid.removeAllViews();

        TextView loading =
                createExerciseMessage(
                        "Loading today's exercises..."
                );

        exerciseGrid.addView(loading);
    }

    private void showExerciseMessage(
            String message) {

        exerciseGrid.removeAllViews();

        TextView messageView =
                createExerciseMessage(message);

        exerciseGrid.addView(messageView);
    }

    private TextView createExerciseMessage(
            String message) {

        TextView messageView =
                new TextView(this);

        messageView.setText(message);
        messageView.setTextColor(
                Color.rgb(102, 89, 106)
        );
        messageView.setTextSize(13);
        messageView.setGravity(Gravity.CENTER);
        messageView.setPadding(
                dp(8),
                dp(20),
                dp(8),
                dp(20)
        );

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = -1;
        params.height = dp(68);

        params.columnSpec =
                GridLayout.spec(
                        0,
                        2
                );

        messageView.setLayoutParams(params);

        return messageView;
    }

    private void updateWorkoutCalories() {

        workoutCalories.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f kcal",
                        totalWorkoutCalories
                )
        );
    }

    private void updateFoodDisplay() {

        foodCaloriesToday.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f kcal",
                        foodCaloriesTodayValue
                )
        );

        if (dailyFoodGoal <= 0) {

            foodCaloriesRemaining.setText(
                    "Your daily food goal is loading..."
            );

            return;
        }

        double remaining =
                dailyFoodGoal
                        - foodCaloriesTodayValue;

        if (remaining > 0) {

            foodCaloriesRemaining.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.0f kcal remaining from your daily goal",
                            remaining
                    )
            );

        } else if (remaining == 0) {

            foodCaloriesRemaining.setText(
                    "You've reached your daily food goal."
            );

        } else {

            double over =
                    Math.abs(remaining);

            foodCaloriesRemaining.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.0f kcal above today's food goal",
                            over
                    )
            );
        }
    }

    private void addFoodCalories() {

        String input =
                foodCaloriesInput
                        .getText()
                        .toString()
                        .trim();

        if (input.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter the calories you ate.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double calories;

        try {

            calories =
                    Double.parseDouble(input);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid calorie amount.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (calories <= 0) {

            Toast.makeText(
                    this,
                    "Calories must be greater than 0.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (authToken.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please login again.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        addFoodButton.setEnabled(false);

        addFoodButton.setText(
                "Saving..."
        );

        CompletedTask foodTask =
                new CompletedTask(
                        0.0,
                        calories
                );

        apiInterface.addTask(
                "Bearer " + authToken,
                foodTask
        ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Response<ResponseBody> response) {

                addFoodButton.setEnabled(true);

                addFoodButton.setText(
                        "+  Add Food Calories"
                );

                if (response.isSuccessful()) {

                    foodCaloriesTodayValue +=
                            calories;

                    updateFoodDisplay();

                    foodCaloriesInput.setText("");

                    Toast.makeText(
                            Tasks.this,
                            String.format(
                                    Locale.getDefault(),
                                    "%.0f kcal added to today's food intake.",
                                    calories
                            ),
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Toast.makeText(
                            Tasks.this,
                            "Unable to save food intake. Server error: "
                                    + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Throwable t) {

                addFoodButton.setEnabled(true);

                addFoodButton.setText(
                        "+  Add Food Calories"
                );

                Log.e(
                        "STEPCAL_FOOD_DEBUG",
                        "ADD FOOD FAILED",
                        t
                );

                Toast.makeText(
                        Tasks.this,
                        "Connection error while saving food.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void showAddExerciseDialog() {

        if (availableExercises == null
                || availableExercises.isEmpty()) {

            Toast.makeText(
                    this,
                    "Exercises are still loading.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        final String[] exerciseKeys =
                availableExercises.keySet()
                        .toArray(
                                new String[0]
                        );

        final String[] exerciseNames =
                new String[exerciseKeys.length];

        for (int i = 0;
             i < exerciseKeys.length;
             i++) {

            exerciseNames[i] =
                    formatExerciseName(
                            exerciseKeys[i]
                    );
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle(
                "Add Exercise"
        );

        builder.setItems(
                exerciseNames,
                (dialog, which) -> {

                    String selectedKey =
                            exerciseKeys[which];

                    Double calories =
                            availableExercises.get(
                                    selectedKey
                            );

                    if (calories == null) {
                        return;
                    }

                    addSelectedExercise(
                            selectedKey,
                            calories
                    );
                }
        );

        builder.setNegativeButton(
                "Cancel",
                null
        );

        builder.show();
    }

    private void addSelectedExercise(
            String exerciseName,
            double calories) {

        if (workoutCompleted) {

            saveAdditionalExercise(
                    exerciseName,
                    calories
            );

        } else {

            totalWorkoutCalories +=
                    calories;

            updateWorkoutCalories();

            addExerciseCard(
                    exerciseName,
                    calories
            );

            Toast.makeText(
                    Tasks.this,
                    formatExerciseName(
                            exerciseName
                    ) + " added!",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void saveAdditionalExercise(
            String exerciseName,
            double calories) {

        addExerciseButton.setEnabled(false);

        CompletedTask additionalTask =
                new CompletedTask(
                        calories,
                        0.0
                );

        apiInterface.addTask(
                "Bearer " + authToken,
                additionalTask
        ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Response<ResponseBody> response) {

                addExerciseButton.setEnabled(true);

                if (response.isSuccessful()) {

                    savedWorkoutCalories +=
                            calories;

                    totalWorkoutCalories =
                            savedWorkoutCalories;

                    updateWorkoutCalories();

                    addExerciseCard(
                            exerciseName,
                            calories
                    );

                    Toast.makeText(
                            Tasks.this,
                            formatExerciseName(
                                    exerciseName
                            ) + " added to today's workout!",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Toast.makeText(
                            Tasks.this,
                            "Unable to add exercise. Server error: "
                                    + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Throwable t) {

                addExerciseButton.setEnabled(true);

                Log.e(
                        "STEPCAL_WORKOUT_DEBUG",
                        "ADD EXERCISE FAILED",
                        t
                );

                Toast.makeText(
                        Tasks.this,
                        "Connection error while adding exercise.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void completeWorkout() {

        if (authToken.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (workoutCompleted) {

            Toast.makeText(
                    this,
                    "Today's workout is already completed.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (totalWorkoutCalories <= 0) {

            Toast.makeText(
                    this,
                    "Add or select exercises before completing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        completeWorkoutButton.setEnabled(false);

        completeWorkoutButton.setText(
                "Saving..."
        );

        CompletedTask completedTask =
                new CompletedTask(
                        totalWorkoutCalories,
                        0.0
                );

        apiInterface.addTask(
                "Bearer " + authToken,
                completedTask
        ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    workoutCompleted = true;

                    savedWorkoutCalories =
                            totalWorkoutCalories;

                    completeWorkoutButton.setText(
                            "✓  Workout Completed"
                    );

                    completeWorkoutButton.setEnabled(
                            false
                    );

                    updateWorkoutCalories();

                    Toast.makeText(
                            Tasks.this,
                            String.format(
                                    Locale.getDefault(),
                                    "Great job! %.2f kcal recorded.",
                                    totalWorkoutCalories
                            ),
                            Toast.LENGTH_LONG
                    ).show();

                } else {

                    completeWorkoutButton.setEnabled(
                            true
                    );

                    completeWorkoutButton.setText(
                            "✓  Mark Workout Complete"
                    );

                    Toast.makeText(
                            Tasks.this,
                            "Unable to save workout. Server error: "
                                    + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ResponseBody> call,
                    @NonNull Throwable t) {

                Log.e(
                        "STEPCAL_WORKOUT_DEBUG",
                        "ADD TASK FAILED",
                        t
                );

                completeWorkoutButton.setEnabled(
                        true
                );

                completeWorkoutButton.setText(
                        "✓  Mark Workout Complete"
                );

                Toast.makeText(
                        Tasks.this,
                        "Connection error while saving workout.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private String formatExerciseName(
            String name) {

        if (name == null || name.isEmpty()) {
            return "Exercise";
        }

        String formatted =
                name.replace("_", " ");

        String[] words =
                formatted.split(" ");

        StringBuilder result =
                new StringBuilder();

        for (String word : words) {

            if (word.isEmpty()) {
                continue;
            }

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );

            if (word.length() > 1) {

                result.append(
                        word.substring(1)
                                .toLowerCase()
                );
            }

            result.append(" ");
        }

        return result.toString().trim();
    }

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    private String getSavedToken() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "MyPrefs",
                        MODE_PRIVATE
                );

        return preferences.getString(
                "token",
                ""
        );
    }
}