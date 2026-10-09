package com.example.stepcal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stepcal.DTO.User;
import com.example.stepcal.R;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(view -> doSave());
    }

    public void doSave() {

        EditText firstName = findViewById(R.id.first_name);
        EditText lastName = findViewById(R.id.last_name);
        EditText email = findViewById(R.id.email);
        EditText phone = findViewById(R.id.phone_no);
        EditText gender = findViewById(R.id.gender);
        EditText password = findViewById(R.id.password);
        EditText height = findViewById(R.id.height);
        EditText weight = findViewById(R.id.weight);
        EditText age = findViewById(R.id.age);

        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String phoneText = phone.getText().toString().trim();
        String genderText = gender.getText().toString().trim();
        String passwordText = password.getText().toString();
        String heightText = height.getText().toString().trim();
        String weightText = weight.getText().toString().trim();
        String ageText = age.getText().toString().trim();

        if (firstNameText.isEmpty()
                || lastNameText.isEmpty()
                || emailText.isEmpty()
                || phoneText.isEmpty()
                || genderText.isEmpty()
                || passwordText.isEmpty()
                || heightText.isEmpty()
                || weightText.isEmpty()
                || ageText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            int ageValue = Integer.parseInt(ageText);
            double heightValue = Double.parseDouble(heightText);
            double weightValue = Double.parseDouble(weightText);
            long phoneValue = Long.parseLong(phoneText);

            User user = new User();

            user.setFirst_name(firstNameText);
            user.setLast_name(lastNameText);
            user.setEmail(emailText);
            user.setPassword(passwordText);
            user.setGender(genderText);
            user.setAge(ageValue);
            user.setHeight(heightValue);
            user.setWeight(weightValue);
            user.setPhone_no(phoneValue);

            Intent intent = new Intent(
                    SignUp.this,
                    ActivityLevel.class
            );

            intent.putExtra("user", user);
            startActivity(intent);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers for age, height, weight and phone",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}