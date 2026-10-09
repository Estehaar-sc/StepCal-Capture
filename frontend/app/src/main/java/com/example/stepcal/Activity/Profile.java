package com.example.stepcal.Activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stepcal.API.ApiInterface;
import com.example.stepcal.DTO.Task;
import com.example.stepcal.DTO.User;
import com.example.stepcal.R;
import com.example.stepcal.MainActivity;
import com.example.stepcal.Retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends BaseActivity {

    private ApiInterface apiInterface;
    private String authToken;

    private TextView name;
    private TextView email;
    private TextView gender;
    private TextView age;
    private TextView height;
    private TextView weight;
    private TextView goal;
    private TextView activityLevel;
    private TextView dailyCalories;

    private String userGoal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        apiInterface =
                RetrofitClient.getRetrofit().create(ApiInterface.class);

        authToken = getSavedToken();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        goal = findViewById(R.id.goal);
        activityLevel = findViewById(R.id.activity_level);
        dailyCalories = findViewById(R.id.daily_calories);

        /*
         * IMPORTANT:
         * Keep this as a Button because activity_profile.xml
         * uses a Button for @id/task.
         */
        Button taskButton = findViewById(R.id.task);

        if (taskButton != null) {

            taskButton.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                Profile.this,
                                Tasks.class
                        );

                startActivity(intent);
            });
        }

        loadProfile();
    }

    private void loadProfile() {

        if (authToken == null || authToken.trim().isEmpty()) {

            goToLogin();
            return;
        }

        apiInterface.getProfile("Bearer " + authToken)
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<User> call,
                            @NonNull Response<User> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    Profile.this,
                                    "Unable to load profile",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        User profile = response.body();

                        /*
                         * =================================================
                         * NAME
                         * =================================================
                         */

                        String firstName =
                                profile.getFirst_name();

                        String lastName =
                                profile.getLast_name();

                        if (firstName == null) {
                            firstName = "";
                        }

                        if (lastName == null) {
                            lastName = "";
                        }

                        String fullName =
                                (firstName + " " + lastName).trim();

                        if (!fullName.isEmpty()) {

                            name.setText(
                                    fullName
                            );

                        } else {

                            name.setText(
                                    "StepCal User"
                            );
                        }

                        /*
                         * =================================================
                         * EMAIL
                         * =================================================
                         */

                        if (profile.getEmail() != null) {

                            email.setText(
                                    profile.getEmail()
                            );
                        }

                        /*
                         * =================================================
                         * CLEAN PROFILE VALUES
                         * =================================================
                         *
                         * The XML already has the field labels on the
                         * left side, so Java must NOT repeat:
                         *
                         * "Gender:"
                         * "Age:"
                         * "Height:"
                         * etc.
                         */

                        if (profile.getGender() != null) {

                            gender.setText(
                                    cleanValue(
                                            profile.getGender()
                                    )
                            );
                        }

                        age.setText(
                                String.valueOf(
                                        profile.getAge()
                                )
                        );

                        height.setText(
                                String.valueOf(
                                        profile.getHeight()
                                )
                        );

                        weight.setText(
                                String.valueOf(
                                        profile.getWeight()
                                )
                        );

                        /*
                         * =================================================
                         * GOAL
                         * =================================================
                         */

                        if (profile.getGoal() != null) {

                            userGoal =
                                    profile.getGoal().trim();

                            goal.setText(
                                    capitalizeGoal(
                                            userGoal
                                    )
                            );
                        }

                        /*
                         * =================================================
                         * ACTIVITY LEVEL
                         * =================================================
                         */

                        if (profile.getActivity_level() != null) {

                            activityLevel.setText(
                                    cleanValue(
                                            profile.getActivity_level()
                                    )
                            );
                        }

                        loadCalories();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<User> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                Profile.this,
                                "Could not connect to server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadCalories() {

        apiInterface.getTask("Bearer " + authToken)
                .enqueue(new Callback<Task>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Task> call,
                            @NonNull Response<Task> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            double calories =
                                    response.body()
                                            .getTarget_Calorie();

                            String goalText;

                            if ("gain".equalsIgnoreCase(userGoal)) {

                                goalText =
                                        "For Gain";

                            } else if ("lose".equalsIgnoreCase(userGoal)) {

                                goalText =
                                        "For Lose";

                            } else {

                                goalText =
                                        "For " +
                                        capitalizeGoal(userGoal);
                            }

                            String calorieText =
                                    "🔥 DAILY CALORIE TARGET\n" +
                                    String.format(
                                            "%.0f kcal",
                                            calories
                                    ) +
                                    "\n" +
                                    goalText +
                                    "\n" +
                                    "Aim to eat around this amount today";

                            SpannableString styledText =
                                    new SpannableString(
                                            calorieText
                                    );

                            int titleStart =
                                    0;

                            int titleEnd =
                                    "🔥 DAILY CALORIE TARGET"
                                            .length();

                            String calorieValue =
                                    String.format(
                                            "%.0f kcal",
                                            calories
                                    );

                            int calorieStart =
                                    calorieText.indexOf(
                                            calorieValue
                                    );

                            int calorieEnd =
                                    calorieStart +
                                    calorieValue.length();

                            int goalStart =
                                    calorieText.indexOf(
                                            goalText
                                    );

                            int goalEnd =
                                    goalStart +
                                    goalText.length();

                            String helperText =
                                    "Aim to eat around this amount today";

                            int helperStart =
                                    calorieText.indexOf(
                                            helperText
                                    );

                            int helperEnd =
                                    helperStart +
                                    helperText.length();

                            /*
                             * Title bold
                             */
                            styledText.setSpan(
                                    new StyleSpan(
                                            Typeface.BOLD
                                    ),
                                    titleStart,
                                    titleEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );

                            /*
                             * Calorie number large + bold
                             */
                            styledText.setSpan(
                                    new AbsoluteSizeSpan(
                                            24,
                                            true
                                    ),
                                    calorieStart,
                                    calorieEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );

                            styledText.setSpan(
                                    new StyleSpan(
                                            Typeface.BOLD
                                    ),
                                    calorieStart,
                                    calorieEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );

                            /*
                             * Gain / Lose bold
                             */
                            styledText.setSpan(
                                    new StyleSpan(
                                            Typeface.BOLD
                                    ),
                                    goalStart,
                                    goalEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );

                            /*
                             * Helper text smaller
                             */
                            styledText.setSpan(
                                    new AbsoluteSizeSpan(
                                            12,
                                            true
                                    ),
                                    helperStart,
                                    helperEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );

                            dailyCalories.setText(
                                    styledText
                            );

                        } else {

                            dailyCalories.setText(
                                    "🔥 DAILY CALORIE TARGET\n" +
                                    "Not available"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Task> call,
                            @NonNull Throwable t
                    ) {

                        dailyCalories.setText(
                                "🔥 DAILY CALORIE TARGET\n" +
                                "Not available"
                        );
                    }
                });
    }

    /*
     * Removes accidental whitespace while keeping the actual
     * profile value clean.
     */
    private String cleanValue(String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String capitalizeGoal(String value) {

        if (value == null || value.trim().isEmpty()) {

            return "Your Goal";
        }

        String trimmed =
                value.trim();

        return trimmed.substring(0, 1).toUpperCase() +
                trimmed.substring(1).toLowerCase();
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

    private void goToLogin() {

        Intent intent =
                new Intent(
                        Profile.this,
                        MainActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(
                intent
        );

        finish();
    }
}