package com.example.stepcal.Activity;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stepcal.API.ApiInterface;
import com.example.stepcal.DTO.CompletedTask;
import com.example.stepcal.DTO.Task;
import com.example.stepcal.DTO.User;
import com.example.stepcal.R;
import com.example.stepcal.Retrofit.RetrofitClient;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Progress extends BaseActivity {

    private ApiInterface apiInterface;
    private String authToken;

    private ProgressGaugeView progressGauge;

    private TextView progressStatus;
    private TextView todayGoalStatus;
    private TextView todayGoalDetails;

    private TextView foodCaloriesProgress;
    private TextView foodCaloriesStatus;

    private TextView dailyCalorieGoalTitle;
    private TextView dailyCalorieGoal;
    private TextView dailyCalorieGoalStatus;

    private TextView burnedCalories;
    private TextView workoutStatus;

    private TextView todaySuggestionTitle;
    private TextView todaySuggestion;

    private TextView tomorrowTitle;
    private TextView tomorrowSuggestion;

    private double dailyCalorieGoalValue = 0.0;
    private double foodCaloriesToday = 0.0;
    private double workoutCaloriesToday = 0.0;

    private boolean workoutCompleted = false;
    private boolean foodLogged = false;

    private String userGoal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress);

        apiInterface =
                RetrofitClient.getRetrofit().create(ApiInterface.class);

        authToken = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .getString("token", "");

        progressGauge =
                findViewById(R.id.todayProgressGauge);

        progressStatus =
                findViewById(R.id.progressStatus);

        todayGoalStatus =
                findViewById(R.id.todayGoalStatus);

        todayGoalDetails =
                findViewById(R.id.todayGoalDetails);

        foodCaloriesProgress =
                findViewById(R.id.foodCaloriesProgress);

        foodCaloriesStatus =
                findViewById(R.id.foodCaloriesStatus);

        dailyCalorieGoalTitle =
                findViewById(R.id.dailyCalorieGoalTitle);

        dailyCalorieGoal =
                findViewById(R.id.dailyCalorieGoal);

        dailyCalorieGoalStatus =
                findViewById(R.id.dailyCalorieGoalStatus);

        burnedCalories =
                findViewById(R.id.burnedCalories);

        workoutStatus =
                findViewById(R.id.workoutStatus);

        todaySuggestionTitle =
                findViewById(R.id.todaySuggestionTitle);

        todaySuggestion =
                findViewById(R.id.todaySuggestion);

        tomorrowTitle =
                findViewById(R.id.tomorrowTitle);

        tomorrowSuggestion =
                findViewById(R.id.tomorrowSuggestion);

        progressGauge.setProgress(0);

        loadProgress();
    }

    private void loadProgress() {

        if (authToken == null || authToken.isEmpty()) {

            progressStatus.setText(
                    "Please login again."
            );

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        loadUserProfile();
        loadDailyTask();
        loadTodayCompletedTask();
    }

    private void loadUserProfile() {

        apiInterface.getProfile(
                "Bearer " + authToken
        ).enqueue(new Callback<User>() {

            @Override
            public void onResponse(
                    @NonNull Call<User> call,
                    @NonNull Response<User> response) {

                if (!response.isSuccessful()
                        || response.body() == null) {

                    userGoal = "";
                    updateGoalLabel();
                    return;
                }

                User user = response.body();

                if (user.getGoal() != null) {
                    userGoal = user.getGoal()
                            .trim()
                            .toLowerCase(Locale.getDefault());
                } else {
                    userGoal = "";
                }

                updateGoalLabel();
                updateProgressDisplay();
            }

            @Override
            public void onFailure(
                    @NonNull Call<User> call,
                    @NonNull Throwable t) {

                userGoal = "";
                updateGoalLabel();
            }
        });
    }

    private void updateGoalLabel() {

        if ("gain".equals(userGoal)) {

            dailyCalorieGoalTitle.setText(
                    "🎯 Daily Calorie Goal — Gain"
            );

        } else if ("lose".equals(userGoal)) {

            dailyCalorieGoalTitle.setText(
                    "🎯 Daily Calorie Goal — Lose"
            );

        } else {

            dailyCalorieGoalTitle.setText(
                    "🎯 Daily Calorie Goal"
            );
        }
    }

    private void loadDailyTask() {

        apiInterface.getTask(
                "Bearer " + authToken
        ).enqueue(new Callback<Task>() {

            @Override
            public void onResponse(
                    @NonNull Call<Task> call,
                    @NonNull Response<Task> response) {

                if (!response.isSuccessful()
                        || response.body() == null) {

                    progressStatus.setText(
                            "Unable to load today's goal."
                    );

                    return;
                }

                Task task = response.body();

                dailyCalorieGoalValue =
                        task.getTarget_Calorie();

                dailyCalorieGoal.setText(
                        String.format(
                                Locale.getDefault(),
                                "%.0f kcal",
                                dailyCalorieGoalValue
                        )
                );

                updateGoalLabel();
                updateProgressDisplay();
            }

            @Override
            public void onFailure(
                    @NonNull Call<Task> call,
                    @NonNull Throwable t) {

                progressStatus.setText(
                        "Unable to connect to the server."
                );
            }
        });
    }

    private void loadTodayCompletedTask() {

        apiInterface.getTodayCompletedTask(
                "Bearer " + authToken
        ).enqueue(new Callback<CompletedTask>() {

            @Override
            public void onResponse(
                    @NonNull Call<CompletedTask> call,
                    @NonNull Response<CompletedTask> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    CompletedTask completedTask =
                            response.body();

                    foodCaloriesToday =
                            completedTask.getCalorie_intake();

                    workoutCaloriesToday =
                            completedTask.getCalorie_burn();

                } else {

                    foodCaloriesToday = 0.0;
                    workoutCaloriesToday = 0.0;
                }

                foodLogged =
                        foodCaloriesToday > 0;

                workoutCompleted =
                        workoutCaloriesToday > 0;

                updateProgressDisplay();
            }

            @Override
            public void onFailure(
                    @NonNull Call<CompletedTask> call,
                    @NonNull Throwable t) {

                foodCaloriesToday = 0.0;
                workoutCaloriesToday = 0.0;

                foodLogged = false;
                workoutCompleted = false;

                updateProgressDisplay();
            }
        });
    }

    private void updateProgressDisplay() {

        if (dailyCalorieGoalValue <= 0
                && !foodLogged
                && !workoutCompleted) {

            return;
        }

        int progress = 0;

        if (foodLogged) {
            progress += 50;
        }

        if (workoutCompleted) {
            progress += 50;
        }

        progressGauge.setProgress(progress);

        foodCaloriesProgress.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f kcal",
                        foodCaloriesToday
                )
        );

        burnedCalories.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f kcal",
                        workoutCaloriesToday
                )
        );

        updateGoalLabel();
        updateDailyCalorieGoalStatus();
        updateFoodStatus();
        updateWorkoutStatus();
        updateGoalStatus();
        updateSuggestion();
        updateTomorrowSuggestion();
    }

    private void updateDailyCalorieGoalStatus() {

        if (dailyCalorieGoalValue <= 0) {

            dailyCalorieGoalStatus.setText(
                    "Daily calorie goal is not available yet."
            );

            return;
        }

        double difference =
                dailyCalorieGoalValue - foodCaloriesToday;

        if (difference > 0) {

            if ("gain".equals(userGoal)) {

                dailyCalorieGoalStatus.setText(
                        String.format(
                                Locale.getDefault(),
                                "Intake %.0f cal of food to meet your gain goal.",
                                difference
                        )
                );

            } else if ("lose".equals(userGoal)) {

                dailyCalorieGoalStatus.setText(
                        String.format(
                                Locale.getDefault(),
                                "Intake %.0f cal of food to meet your lose goal.",
                                difference
                        )
                );

            } else {

                dailyCalorieGoalStatus.setText(
                        String.format(
                                Locale.getDefault(),
                                "Intake %.0f cal of food to meet your daily goal.",
                                difference
                        )
                );
            }

        } else if (difference == 0) {

            dailyCalorieGoalStatus.setText(
                    "You've reached your daily calorie goal."
            );

        } else {

            dailyCalorieGoalStatus.setText(
                    String.format(
                            Locale.getDefault(),
                            "You've eaten %.0f cal above your daily goal.",
                            Math.abs(difference)
                    )
            );
        }
    }

    private void updateFoodStatus() {

        if (dailyCalorieGoalValue <= 0) {

            foodCaloriesStatus.setText(
                    "Daily calorie goal is not available yet."
            );

            return;
        }

        double difference =
                dailyCalorieGoalValue - foodCaloriesToday;

        if (difference > 0) {

            foodCaloriesStatus.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.0f kcal remaining for today's food goal.",
                            difference
                    )
            );

        } else if (difference == 0) {

            foodCaloriesStatus.setText(
                    "You've reached today's food goal."
            );

        } else {

            foodCaloriesStatus.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.0f kcal above today's food goal.",
                            Math.abs(difference)
                    )
            );
        }
    }

    private void updateWorkoutStatus() {

        if (workoutCompleted) {

            workoutStatus.setText(
                    String.format(
                            Locale.getDefault(),
                            "Workout complete • %.2f kcal burned.",
                            workoutCaloriesToday
                    )
            );

        } else {

            workoutStatus.setText(
                    "Workout not completed yet."
            );
        }
    }

    private void updateGoalStatus() {

        if (foodLogged && workoutCompleted) {

            todayGoalStatus.setText(
                    "🎉 Today's tracked goals are complete!"
            );

            todayGoalDetails.setText(
                    "Your food and workout are both logged for today."
            );

            progressStatus.setText(
                    "You've completed today's tracked goals."
            );

        } else if (foodLogged) {

            todayGoalStatus.setText(
                    "🍽️ Food intake logged"
            );

            todayGoalDetails.setText(
                    "Keep your intake aligned with your daily goal and complete your workout."
            );

            progressStatus.setText(
                    "Halfway there — your food is logged."
            );

        } else if (workoutCompleted) {

            todayGoalStatus.setText(
                    "💪 Workout completed"
            );

            todayGoalDetails.setText(
                    "Your workout is logged. Track your food intake to see how close you are to your daily goal."
            );

            progressStatus.setText(
                    "Halfway there — your workout is complete."
            );

        } else {

            todayGoalStatus.setText(
                    "🌱 Start today's progress"
            );

            todayGoalDetails.setText(
                    "Track your food and complete your recommended workout."
            );

            progressStatus.setText(
                    "Nothing tracked yet — let's get started."
            );
        }
    }

    private void updateSuggestion() {

        todaySuggestionTitle.setText(
                "💡 Your daily update"
        );

        if (foodLogged && workoutCompleted) {

            if (dailyCalorieGoalValue > 0
                    && foodCaloriesToday > dailyCalorieGoalValue) {

                double over =
                        foodCaloriesToday
                                - dailyCalorieGoalValue;

                todaySuggestion.setText(
                        String.format(
                                Locale.getDefault(),
                                "Your workout is complete and your food is logged. You are %.0f kcal above today's goal. Keep the rest of the day balanced and focus on hydration.",
                                over
                        )
                );

            } else {

                todaySuggestion.setText(
                        "Great work — your workout is complete and your food is logged. Today's tracked goals are complete. Focus on recovery, hydration, and a good night's sleep."
                );
            }

        } else if (foodLogged) {

            todaySuggestion.setText(
                    "Your food is logged for today. Keep your intake aligned with your daily goal and complete the recommended workout."
            );

        } else if (workoutCompleted) {

            todaySuggestion.setText(
                    "Your workout is complete. Log your food intake so StepCal can track the rest of today's progress."
            );

        } else {

            todaySuggestion.setText(
                    "Start by logging your food intake and completing today's recommended workout. Small steps add up."
            );
        }
    }

    private void updateTomorrowSuggestion() {

        tomorrowTitle.setText(
                "🌅 Your next day"
        );

        if (dailyCalorieGoalValue > 0) {

            tomorrowSuggestion.setText(
                    String.format(
                            Locale.getDefault(),
                            "Aim for around %.0f kcal tomorrow and complete your recommended workout. Stay consistent rather than trying to make up for one day.",
                            dailyCalorieGoalValue
                    )
            );

        } else {

            tomorrowSuggestion.setText(
                    "Follow tomorrow's recommended calorie target and complete your planned workout."
            );
        }
    }
}