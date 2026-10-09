package com.example.stepcal.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stepcal.API.ApiInterface;
import com.example.stepcal.DTO.User;
import com.example.stepcal.R;
import com.example.stepcal.Retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLevel extends AppCompatActivity {

    private RadioGroup activityLevelRadioGroup;
    private Button submitButton;

    private ApiInterface apiInterface;
    private String authToken;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        activityLevelRadioGroup = findViewById(R.id.activityLevelRadioGroup);
        submitButton = findViewById(R.id.submit);

        apiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        authToken = getSavedToken();

        /*
         * There are two ways to reach this screen:
         *
         * 1. Signup -> Activity Level
         *    In this case the User object is already attached to the Intent.
         *
         * 2. Navigation drawer -> Activity Level
         *    In this case there is no User object, so we load the
         *    logged-in user's profile from the backend.
         */

        user = (User) getIntent().getSerializableExtra("user");

        if (user == null) {
            loadUserProfile();
        }

        submitButton.setOnClickListener(v -> saveActivityLevel());
    }

    private void saveActivityLevel() {

        RadioButton lowRadioButton = findViewById(R.id.low);
        RadioButton moderateRadioButton = findViewById(R.id.moderate);
        RadioButton highRadioButton = findViewById(R.id.high);
        RadioButton extremeRadioButton = findViewById(R.id.extreme);

        String level = "";

        if (lowRadioButton.isChecked()) {
            level = "low";
        } else if (moderateRadioButton.isChecked()) {
            level = "moderate";
        } else if (highRadioButton.isChecked()) {
            level = "high";
        } else if (extremeRadioButton.isChecked()) {
            level = "extreme";
        }

        if (level.isEmpty()) {
            Toast.makeText(
                    ActivityLevel.this,
                    "Please select an activity level",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (user == null) {
            Toast.makeText(
                    ActivityLevel.this,
                    "Loading your profile. Please try again.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        user.setActivity_level(level);

        Intent intent = new Intent(
                ActivityLevel.this,
                Goal.class
        );

        intent.putExtra("user", user);

        startActivity(intent);
    }

    private void loadUserProfile() {

        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(
                    ActivityLevel.this,
                    "Please log in again.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        apiInterface.getProfile("Bearer " + authToken)
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<User> call,
                            @NonNull Response<User> response
                    ) {

                        if (response.isSuccessful() && response.body() != null) {

                            user = response.body();

                        } else {

                            Toast.makeText(
                                    ActivityLevel.this,
                                    "Could not load your profile.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<User> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                ActivityLevel.this,
                                "Unable to connect to StepCal.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private String getSavedToken() {

        SharedPreferences preferences =
                getSharedPreferences("MyPrefs", MODE_PRIVATE);

        return preferences.getString("token", "");
    }
}