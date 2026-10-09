package com.example.stepcal.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stepcal.MainActivity;
import com.example.stepcal.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the beautiful StepCal loading screen.
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences preferences =
                    getSharedPreferences("MyPrefs", MODE_PRIVATE);

            String authToken = preferences.getString("token", "");

            Intent intent;

            if (authToken != null && !authToken.trim().isEmpty()) {
                // User is already logged in
                intent = new Intent(SplashActivity.this, Profile.class);
            } else {
                // No saved login
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();

        }, SPLASH_DELAY);
    }
}