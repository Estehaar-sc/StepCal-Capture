package com.example.stepcal.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stepcal.API.ApiInterface;
import com.example.stepcal.DTO.JwtResponse;
import com.example.stepcal.DTO.SignInRequest;
import com.example.stepcal.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.login);

        login.setOnClickListener(view -> doLogIn());
    }

    public void doLogIn() {

        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString();

        // Basic validation before contacting the server.
        if (emailText.isEmpty()) {
            email.setError("Enter your email");
            email.requestFocus();
            return;
        }

        if (passwordText.isEmpty()) {
            password.setError("Enter your password");
            password.requestFocus();
            return;
        }

        apiInterface = com.example.stepcal.Retrofit.RetrofitClient
                .getRetrofit()
                .create(ApiInterface.class);

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail(emailText);
        signInRequest.setPassword(passwordText);

        apiInterface.login(signInRequest).enqueue(new Callback<JwtResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<JwtResponse> call,
                    @NonNull Response<JwtResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    JwtResponse loginResponse = response.body();

                    String token = loginResponse.getToken();

                    if (token == null || token.trim().isEmpty()) {
                        Toast.makeText(
                                Login.this,
                                "Login failed: server returned no token",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    saveCredentials(emailText, token);

                    Toast.makeText(
                            Login.this,
                            "Welcome back!",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(Login.this, Profile.class);
                    startActivity(intent);
                    finish();

                } else if (response.code() == 401) {

                    Toast.makeText(
                            Login.this,
                            "Wrong email or password",
                            Toast.LENGTH_LONG
                    ).show();

                } else if (response.code() == 400) {

                    Toast.makeText(
                            Login.this,
                            "Please check your information",
                            Toast.LENGTH_LONG
                    ).show();

                } else {

                    Toast.makeText(
                            Login.this,
                            "Login failed (" + response.code() + ")",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<JwtResponse> call,
                    @NonNull Throwable t) {

                Toast.makeText(
                        Login.this,
                        "Unable to connect to StepCal",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    public void saveCredentials(String email, String token) {

        SharedPreferences preferences =
                getSharedPreferences("MyPrefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("email", email);
        editor.putString("token", token);

        editor.apply();
    }
}